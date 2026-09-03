package com.example.archmind.common.util;

import com.example.archmind.common.exception.BusinessException;
import com.example.archmind.common.security.SecurityUser;
import com.example.archmind.dao.ProjectMapper;
import com.example.archmind.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckProjectUtil {

    private final ProjectMapper projectMapper;

    public void checkProject(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID为空");
        }
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在: " + projectId);
        }
        checkOwnership(project);
    }

    private void checkOwnership(Project project) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser securityUser) {
            Long userId = securityUser.getUserId();
            if (userId != null && project.getUserId() != null && !userId.equals(project.getUserId())) {
                throw new BusinessException("无权操作该项目");
            }
        }
    }
}
