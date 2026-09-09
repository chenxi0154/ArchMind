package com.example.archmind.service;

import com.example.archmind.dto.request.ProjectCreateRequest;
import com.example.archmind.dto.response.ProjectResponse;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(Long userId, ProjectCreateRequest request);

    List<ProjectResponse> listMyProjects(Long userId);

    void deleteProject(Long projectId);
}
