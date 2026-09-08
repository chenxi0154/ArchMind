package com.example.archmind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.archmind.common.exception.BusinessException;
import com.example.archmind.dao.FileEntityMapper;
import com.example.archmind.dao.ProjectOverviewMapper;
import com.example.archmind.dao.ProjectSourceMapper;
import com.example.archmind.dto.response.ProjectOverviewResponse;
import com.example.archmind.entity.FileEntity;
import com.example.archmind.entity.ProjectOverview;
import com.example.archmind.entity.ProjectSource;
import com.example.archmind.service.FileContentService;
import com.example.archmind.service.ProjectOverviewService;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目概况分析：从已上传项目中读取 pom.xml / README，裁剪降噪后组装提示词，
 * 调用 LLM 生成结构化概况，写入 project_overview 表（一个项目一条，重分析覆盖）。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectOverviewServiceImpl implements ProjectOverviewService {

    private static final String DEFAULT_MODEL = "deepseek-chat";

    private final ProjectSourceMapper projectSourceMapper;
    private final FileEntityMapper fileEntityMapper;
    private final ProjectOverviewMapper projectOverviewMapper;
    private final FileContentService fileContentService;
    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    @Override
    public ProjectOverviewResponse projectOverview(Long projectId) {
        // 1. 定位项目落盘根目录（project_source.content 存的是解压根目录绝对路径）
        ProjectSource source = findSource(projectId);
        Path extractDir = Paths.get(source.getContent());
        Long rootFileId = source.getFileId();

        // 2. 读取 pom.xml 与 README
        String pom = readPom(projectId, rootFileId, extractDir);
        String readme = readReadme(projectId, rootFileId, extractDir);

        // 3. 裁剪 pom
        String cutPom = cutPom(pom);

        // 4. 组装提示词
        String prompt = buildPrompt(readme, cutPom);

        // 5. 调用 LLM 得到结构化概况
        ProjectOverviewResponse response = callLlm(prompt);

        // 6. 写入数据库（幂等覆盖）
        saveOverview(projectId, response);

        return response;
    }

    private ProjectSource findSource(Long projectId) {
        ProjectSource source = projectSourceMapper.selectOne(
                new LambdaQueryWrapper<ProjectSource>()
                        .eq(ProjectSource::getProjectId, projectId)
                        .isNotNull(ProjectSource::getFileId)
                        .orderByDesc(ProjectSource::getId)
                        .last("limit 1"));
        if (source == null || source.getContent() == null) {
            throw new BusinessException("该项目尚未上传源码或未完成扫描");
        }
        return source;
    }

    private String readPom(Long projectId, Long rootFileId, Path extractDir) {
        // 优先取根目录下的 pom.xml；没有则退化为该项目任意一个 pom.xml
        FileEntity pom = fileEntityMapper.selectOne(
                new LambdaQueryWrapper<FileEntity>()
                        .eq(FileEntity::getProjectId, projectId)
                        .eq(FileEntity::getFileName, "pom.xml")
                        .eq(FileEntity::getParentId, rootFileId)
                        .last("limit 1"));
        if (pom == null) {
            pom = fileEntityMapper.selectOne(
                    new LambdaQueryWrapper<FileEntity>()
                            .eq(FileEntity::getProjectId, projectId)
                            .eq(FileEntity::getFileName, "pom.xml")
                            .last("limit 1"));
        }
        if (pom == null) {
            throw new BusinessException("未找到 pom.xml，暂仅支持 Maven 项目");
        }
        return readFile(extractDir, pom);
    }

    private String readReadme(Long projectId, Long rootFileId, Path extractDir) {
        List<FileEntity> rootFiles = fileEntityMapper.selectList(
                new LambdaQueryWrapper<FileEntity>()
                        .eq(FileEntity::getProjectId, projectId)
                        .eq(FileEntity::getParentId, rootFileId));
        FileEntity readme = rootFiles.stream()
                .filter(f -> f.getFileName() != null
                        && f.getFileName().toLowerCase().startsWith("readme"))
                .findFirst()
                .orElse(null);
        return readme == null ? null : readFile(extractDir, readme);
    }

    private String readFile(Path extractDir, FileEntity file) {
        if (file.getFilePath() == null) {
            return null;
        }
        Path absolute = extractDir.resolve(file.getFilePath()).normalize();
        return fileContentService.readTextContent(absolute);
    }

    /**
     * 裁剪 pom.xml：只保留 parent / properties / modules / dependencies 四个顶层标签。
     * 这是给 LLM 降噪的语义上下文，非精确依赖解析（精确解析见 project_dependency）。
     */
    private String cutPom(String pom) {
        StringBuilder sb = new StringBuilder();
        for (String tag : List.of("parent", "properties", "modules", "dependencies")) {
            Pattern pattern = Pattern.compile("<" + tag + "\\b[^>]*>.*?</" + tag + ">", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(pom);
            if (matcher.find()) {
                sb.append(matcher.group()).append('\n');
            }
        }
        return sb.length() == 0 ? pom : sb.toString();
    }

    private String buildPrompt(String readme, String cutPom) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是软件架构分析助手。根据以下项目的 README 和 pom.xml（裁剪），输出结构化的项目概况 JSON。\n\n");
        sb.append("要求：\n");
        sb.append("1. projectType 取以下枚举之一：后端服务 / 前端应用 / 全栈应用 / 库 / 工具 / 其他。\n");
        sb.append("2. summary 一句话概括项目；description 用 2-4 句话详细描述用途。\n");
        sb.append("3. techStack 归纳用到的框架/中间件（name/category/version/role）。\n");
        sb.append("4. architecture 给出高层架构：style 取 分层/单体/微服务/其他，layers 列出分层名，description 说明。\n");
        sb.append("5. modules 归纳主要代码模块（name/responsibility/keyFiles）。\n");
        sb.append("6. 全部用中文，只输出 JSON，不要输出解释或代码块标记。\n\n");
        sb.append("=== README ===\n").append(readme == null ? "(无 README)" : readme).append("\n\n");
        sb.append("=== pom.xml（裁剪） ===\n").append(cutPom).append('\n');
        return sb.toString();
    }

    private ProjectOverviewResponse callLlm(String prompt) {
        try {
            ChatClient chatClient = ChatClient.create(chatModel);
            return chatClient.prompt(prompt).call().entity(ProjectOverviewResponse.class);
        } catch (Exception e) {
            log.error("调用 LLM 生成项目概况失败", e);
            throw new BusinessException("调用 LLM 失败: " + e.getMessage());
        }
    }

    private void saveOverview(Long projectId, ProjectOverviewResponse response) {
        ProjectOverview entity = new ProjectOverview();
        entity.setProjectId(projectId);
        entity.setProjectType(response.getProjectType());
        entity.setSummary(response.getSummary());
        entity.setDescription(response.getDescription());
        try {
            entity.setTechStackJson(objectMapper.writeValueAsString(response.getTechStack()));
            entity.setArchitectureJson(objectMapper.writeValueAsString(response.getArchitecture()));
            entity.setModulesJson(objectMapper.writeValueAsString(response.getModules()));
        } catch (JacksonException e) {
            throw new BusinessException("概况结果序列化失败: " + e.getMessage());
        }
        entity.setRawResponse(toJsonQuietly(response));
        entity.setModel(DEFAULT_MODEL);
        entity.setCreateTime(LocalDateTime.now());

        // 幂等：一个项目只保留一条概况，重分析时覆盖
        projectOverviewMapper.delete(new LambdaQueryWrapper<ProjectOverview>()
                .eq(ProjectOverview::getProjectId, projectId));
        projectOverviewMapper.insert(entity);
    }

    private String toJsonQuietly(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException e) {
            log.warn("序列化 LLM 返回结果失败", e);
            return null;
        }
    }
}
