package com.example.archmind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.archmind.common.exception.BusinessException;
import com.example.archmind.dao.ProjectMapper;
import com.example.archmind.dto.request.ProjectCreateRequest;
import com.example.archmind.dto.response.ProjectResponse;
import com.example.archmind.entity.Project;
import com.example.archmind.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponse createProject(Long userId, ProjectCreateRequest request) {
        if (userId == null) {
            throw new BusinessException("未获取到登录用户");
        }
        Project project = new Project();
        project.setUserId(userId);
        project.setName(request.getName().trim());
        project.setDescription(request.getDescription());
        project.setGitUrl(request.getGitUrl());
        project.setStatus("CREATED");
        project.setCreateTime(LocalDateTime.now());
        projectMapper.insert(project);
        return toResponse(project);
    }

    @Override
    public List<ProjectResponse> listMyProjects(Long userId) {
        if (userId == null) {
            throw new BusinessException("未获取到登录用户");
        }
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getUserId, userId)
                .orderByDesc(Project::getCreateTime);
        return projectMapper.selectList(wrapper).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .language(project.getLanguage())
                .framework(project.getFramework())
                .gitUrl(project.getGitUrl())
                .version(project.getVersion())
                .status(project.getStatus())
                .createTime(project.getCreateTime())
                .build();
    }
}
