package com.example.archmind.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProjectOverviewResponse {
    private String projectType;
    private String summary;
    private String description;
    private List<TechStackItem> techStack;
    private Architecture architecture;
    private List<ModuleItem> modules;

    @Data public static class TechStackItem {
        private String name; private String category; private String version; private String role;
    }
    @Data
    public static class Architecture {
        private String style; private List<String> layers; private String description;
    }
    @Data public static class ModuleItem {
        private String name; private String responsibility; private List<String> keyFiles;
    }

}
