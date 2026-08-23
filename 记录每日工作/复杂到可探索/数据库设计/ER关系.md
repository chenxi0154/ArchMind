# 数据库设计（ER 关系）

> **权威表结构以 [[复杂到可探索/数据库表结果.canvas]] 为准**，本文档为文字版说明与关系梳理。
> 旧版字段说明（类型、语义）已合并进来，并修正了旧版中 `project.status` 重复、分析状态误放等矛盾。

---

## 关系总览

```
user 用户
  │ 1:N
project 项目
  │ 1:N
file 文件（parent_id 自引用构成目录树）
  │ 1:N
code_element 代码元素（parent_id 自引用构成类/方法层级）
```

```
project 1:N project_source（项目资源）
project 1:N project_dependency（依赖）
project 1:N business_candidate（候选业务）
project 1:N ai_analysis_task（分析任务）
```

```
business_candidate 1:1 business_domain（业务领域）
business_domain 1:N business_domain_file（领域-文件关系）
business_domain 1:N capability（能力）
entity_domain ↔ entity_code_mapper ↔ code_element
capability ↔ capability_code_mapper ↔ code_element
code_element ↔ code_relation ↔ code_element
```

---

## 一、用户与项目

### user 用户表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| username | varchar | 用户名 |
| password | varchar | 密码 |
| email | varchar | 邮箱 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

### project 项目表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| user_id | bigint | 所属用户 |
| name | varchar | 项目名 |
| description | text | 项目描述 |
| language | varchar | 主要语言 |
| framework | varchar | 框架 |
| git_url | varchar | Git 地址（可选） |
| version | varchar | 版本 |
| status | varchar | 项目状态 |
| create_time | datetime | 创建时间 |

> 说明：`status` 表示**项目本身**的状态（如上传中、就绪、已删除）；分析进度由 `ai_analysis_task.status` 承载，两者语义分离。

### project_source 项目资源表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| project_id | bigint | 所属项目 |
| source_type | varchar | 来源类型（zip / git / 本地） |
| file_id | bigint | 关联文件 |
| analysis_status | varchar | 分析状态 |
| create_time | datetime | 创建时间 |

### project_dependency 依赖表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| project_id | bigint | 所属项目 |
| group_id | varchar | 依赖 groupId |
| artifact_id | varchar | 依赖 artifactId |
| version | varchar | 版本 |
| type | varchar | 依赖类型 |

---

## 二、文件与代码元素

### file 文件表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| project_id | bigint | 所属项目 |
| parent_id | bigint | 父目录（自引用，构成目录树） |
| file_name | varchar | 文件名 |
| file_path | varchar | 文件路径 |
| file_type | varchar | 文件类型 |
| file_size | bigint | 文件大小 |
| language | varchar | 语言 |
| hash | varchar | 内容哈希（用于去重） |
| create_time | datetime | 创建时间 |

### code_element 代码元素表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| file_id | bigint | 所属文件 |
| parent_id | bigint | 父元素（自引用，构成类/方法层级） |
| element_type | varchar | 元素类型（类 / 方法 / 字段…） |
| class_name | varchar | 类名 |
| start_line | int | 起始行 |
| end_line | int | 结束行 |
| metadata_json | text | 元数据 |

### code_relation 代码关系表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| project_id | bigint | 所属项目 |
| source_element_id | bigint | 源代码元素 |
| target_element_id | bigint | 目标代码元素 |
| relation_type | varchar | 关系类型（调用 / 依赖…） |

---

## 三、分析任务

### ai_analysis_task 分析任务表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| project_id | bigint | 所属项目 |
| analysis_type | varchar | 分析类型（扫描 / 领域建模 / 架构分析…） |
| target_type | varchar | 目标类型 |
| target_id | bigint | 目标 ID |
| prompt | text | 输入 Prompt |
| response | text | LLM 返回结果 |
| model | varchar | 使用的模型 |
| status | varchar | 任务状态（WAITING / RUNNING / SUCCESS / FAILED） |
| create_time | datetime | 创建时间 |

> 旧版 `analysis_task` 中的 `progress`（进度）、`message`（消息）字段在新版中移除，如需进度展示建议在此表补充。

---

## 四、业务领域层

### business_candidate 候选业务表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| project_id | bigint | 所属项目 |
| name | varchar | 候选名 |
| description | varchar | 描述 |
| source_type | varchar | 来源类型 |
| confidence | decimal | 置信度 |
| file_count | int | 涉及文件数 |
| element_count | int | 涉及代码元素数 |
| status | varchar | 状态 |

### business_domain 业务领域表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| project_id | bigint | 所属项目 |
| candidate_id | bigint | 来源候选业务 |
| name | varchar | 领域名 |
| description | varchar | 描述 |
| business_goal | varchar | 业务目标 |
| boundary | varchar | 领域边界 |
| importance | varchar | 重要度 |
| analysis_result_json | text | 分析结果 JSON |

### business_domain_file 业务领域-文件关系表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| business_domain_id | bigint | 业务领域 |
| file_id | bigint | 文件 |
| relation_type | varchar | 关系类型 |
| score | decimal | 关联度得分 |

### entity_domain 实体类领域表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| project_id | bigint | 所属项目 |
| name | varchar | 名称 |
| description | varchar | 描述 |
| table_name | varchar | 对应数据表名 |
| entity_type | varchar | 实体类型 |

### entity_code_mapper 实体-代码关系表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| entity_id | bigint | 实体 ID |
| code_element_id | bigint | 代码元素 ID |
| relation_type | varchar | 关系类型 |

---

## 五、能力层

### capability 能力表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| business_domain_id | bigint | 所属业务领域 |
| name | varchar | 能力名 |
| description | varchar | 描述 |
| business_value | varchar | 业务价值 |
| input | varchar | 输入 |
| output | varchar | 输出 |
| status | varchar | 状态 |
| analysis_json | text | 分析结果 JSON |

### capability_code_mapper 能力-代码关系表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint | 主键 |
| capability_id | bigint | 能力 ID |
| code_element_id | bigint | 代码元素 ID |
| relation_type | varchar | 关系类型 |

---

## 旧版差异（已修正）

- 旧版 `project` 表存在两个 `status` 字段，且第二个实际为「分析状态」，已移出，分析状态统一由 `ai_analysis_task.status` 承载。
- 旧版 `analysis_task` 的 `progress` / `message` 字段在新版 canvas 中不存在，如需保留建议补回。
- 旧版 `project_file` 表无 `parent_id` / `file_type` / `language` / `hash`，新版已补齐以支持目录树与去重。
