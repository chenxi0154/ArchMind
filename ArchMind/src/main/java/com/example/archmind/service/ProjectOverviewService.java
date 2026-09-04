package com.example.archmind.service;

import com.example.archmind.dto.response.ProjectOverviewResponse;

public interface ProjectOverviewService {
    ProjectOverviewResponse projectOverview(Long projectId);
}
