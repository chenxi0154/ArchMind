package com.example.archmind.model.ast;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 全项目解析结果的总容器。
 * A 阶段产出它，B/C 阶段在它上面建索引和消解关系。
 */
@Data
public class ParsedProject {

    private List<ParsedFile> files = new ArrayList<>();
}
