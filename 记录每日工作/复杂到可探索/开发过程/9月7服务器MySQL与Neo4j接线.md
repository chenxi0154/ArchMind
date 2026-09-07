# 9月7 服务器 MySQL/Neo4j 部署 + 后端接线

> 主题：第一次在服务器上用 Docker 部署 MySQL/Neo4j，打通「本地后端 → 远程库」链路，把 application.yml 从 SQLite 切到 MySQL
> 状态：连通性验证通过；配置已落盘（本次待提交）；建表脚本已给出，待执行

---

## 一、结论

后端要连上服务器数据库，「用 服务器IP:端口 连」这句话成立，但有前提。核心认知两点：

1. 从本地连远程库，必须**同时**满足 4 层条件（见二），缺一层都不通；
2. **「端口能连上」≠「服务正常」，更不能拿浏览器打开与否当判断标准**——不同端口跑的是不同协议（见三）。

---

## 二、打通远程连接的四层条件

| 层 | 内容 | 检查方式 |
|---|---|---|
| ① Docker 端口映射 | 容器没 `-p` 映射，外部访问不到 | 服务器 `docker ps` 看 PORTS 列 |
| ② 容器内 DB 监听地址 | 要监听 0.0.0.0 而非只绑 localhost | MySQL 官方镜像默认 OK；Neo4j 自定义过要查 `server.default_listen_address` |
| ③ MySQL 账号 host 权限 | root 常限 localhost，**本地连不上** | 建 `'app'@'%'` 账号 + `GRANT ... ON 库.*` |
| ④ 云安全组 + 防火墙 | SSH(22) 通不代表 3306 通 | 本地 `Test-NetConnection IP -Port 3306` |

**最容易踩的坑是③**：端口全通也报 `Access denied` / `Host not allowed`，就是因为账号 host 是 localhost。

---

## 三、各端口是什么协议（浏览器为何打不开 3306）

| 端口 | 协议 | 谁在用 | 浏览器能否打开 |
|---|---|---|---|
| 3306 | MySQL 客户端二进制协议 | mysql 客户端 / JDBC / DBeaver | ❌ 打不开（正常） |
| 7474 | Neo4j HTTP | Neo4j Browser 网页界面 | ✅ |
| 7473 | Neo4j HTTPS | 加密网页 | ✅ |
| 7687 | Bolt 二进制协议 | 程序驱动（Spring Data Neo4j） | ❌ |

浏览器只发 HTTP 报文；MySQL 3306 期待握手指令，语言不通直接掐连接 → 浏览器显示「无法使用当前页面」。**能被浏览器打开 ≠ 数据库正常，只是它刚好是 HTTP 服务。** Neo4j 网页能开（7474）不意味着驱动用的 7687 通，需单独测。

---

## 四、服务器实测结果

- `mysql:8.0` 容器已映射 `0.0.0.0:3306->3306/tcp`
- 本地 `Test-NetConnection <IP> -Port 3306` = **True**（防火墙/安全组通过）
- Neo4j Browser 7474 本地可开（7687 尚未测）
- 已建库 `archmind`（utf8mb4）+ 账号 `archmind@'%'`（`GRANT ALL ON archmind.*`），`mysql.user` 核对 host=`%` ✅

---

## 五、环境变量传参（配置文件不写死密码）

Spring Boot 的 **relaxed binding**：环境变量可覆盖 yml 任意属性，且优先级高于 yml。规则 = 点分属性名换成大写 + 下划线：

| yml 属性 | 环境变量 |
|---|---|
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` |
| `spring.neo4j.uri` / `authentication.password` | `SPRING_NEO4J_URI` / `SPRING_NEO4J_AUTHENTICATION_PASSWORD` |
| `spring.data.redis.host` | `SPRING_DATA_REDIS_HOST` |
| `spring.ai.deepseek.api-key` | `SPRING_AI_DEEPSEEK_API_KEY` |

`${}` 占位符：`${VAR}` 无默认、取不到**启动即报**；`${VAR:默认}` 取不到用默认值。
传法：IDEA Run Configuration → Environment variables（多个分号分隔）；或命令行 `export` 后再 `./mvnw spring-boot:run`。
**落盘**：yml 两处明文密码已改为 `${MYSQL_PASSWORD}` / `${NEO4J_PASSWORD}`，避免提交进 git。

---

## 六、概念补课（本次问答沉淀）

- **MySQL vs PostgreSQL**：都是关系型 SQL。MySQL 出身 Web 偏简单快、对新手友好；PG 功能全、贴近 SQL 标准、复杂查询/JSONB 强。项目 SQLite→MySQL，DDL 方言要换（自增/时间/布尔、冲突语句写法）。
- **Bolt 7687 vs 7474**：Bolt 是 Neo4j 官方二进制驱动协议（程序用）；7474 是 HTTP（浏览器界面用）。端口号是历史默认约定，无特殊语义。`bolt://IP:7687` 写法正确。
- **为何不用 root 连**：最小权限。root 是超级管理员，一旦代码被注入等于整库沦陷；`archmind` 只被授权自己的库，出事只影响这一个库。

---

## 七、建表规划（判断：到底需要哪些表）

核对代码后结论：

- **实际需要的 5 张表**：`user` / `project` / `project_source` / `file` / `project_overview`，其实体字段与 schema.sql 列**完全一致**；
- `CodeElement` / `CodeRelation` / `ProjectDependency` 三个实体+Mapper **零引用（遗留）**，不需要建表；
- SQLite→MySQL 方言差异 3 点：主键 **BIGINT 自增**（MyBatis-Plus `@TableId` 默认雪花算法，19 位 id 用 INT 会溢出）、时间 **DATETIME**、布尔 `has_children` 用 **TINYINT(1)**；
- `file` 表名在 MySQL 可能算保留字 → `FileEntity` 的 `@TableName` 建议包反引号 `"`file`"`（一行）。

DDL 脚本已给出（在会话中），建表步骤 = 服务器 `cat > init_mysql.sql` + `docker exec -i mysql mysql -uroot -p archmind < init_mysql.sql` + `SHOW TABLES` 验证。

---

## 八、判断：配置齐不齐 / 连上能否正常用

**MySQL 链路已齐全**（驱动✅ yml✅ 账号✅ 端口✅），建完表即通。但「系统正常使用」还有两个前置缺口：

1. **Redis 是登录/鉴权硬依赖**：`login()` 把 token 写 Redis、JWT 过滤器每次请求 `validateTokenFromRedis()` 查 Redis。服务器**还没有 Redis 容器**，yml 又指向 localhost → Redis 未就绪时 login / 带 token 接口直接报错。**先用 register 接口验证 MySQL 链路**（不依赖 Redis）。
2. **`DEEPSEEK_API_KEY` 必须设**：yml `api-key: ${DEEPSEEK_API_KEY}` 无默认值，且 `model.chat: deepseek` 激活自动配置，不设可能启动即失败。

Neo4j 代码当前零引用，暂不影响 MySQL 使用。

---

## 九、本次配置改动（工作区落盘，待提交）

- `pom.xml`：新增 `com.mysql:mysql-connector-j`（runtime）
- `application.yml`：
  - datasource：`sqlite` → `mysql`（driver `com.mysql.cj.jdbc.Driver` + HikariCP；url 用 `${MYSQL_HOST:127.0.0.1}:3306/archmind?useSSL=false&allowPublicKeyRetrieval=true...`，`allowPublicKeyRetrieval=true` 是 MySQL8 `caching_sha2_password` 非 SSL 连接的必需项）
  - 移除 `spring.sql.init` schema.sql 自动执行（SQLite 方言，MySQL 下会报错）
  - DeepSeek 补 `base-url: https://api.deepseek.com`
  - 新增 `spring.neo4j` 块（uri `bolt://172.21.160.203:7687`，密码走 `${NEO4J_PASSWORD}`）

---

## 十、待办 / 风险

- [ ] **在服务器执行 5 张表 DDL**（脚本已给，尚未执行）
- [ ] 部署 Redis，并把 `spring.data.redis.host` 指向它
- [ ] 设环境变量：`DEEPSEEK_API_KEY` / `MYSQL_HOST` / `MYSQL_PASSWORD` / `NEO4J_PASSWORD`
- [ ] **更换弱口令**（root / archmind 曾用 `your_password`），收敛 `root@'%'`
- [ ] `Test-NetConnection IP -Port 7687` 确认 Neo4j Bolt 是否放行
- [ ] register 冒烟（纯 MySQL 链路）→ 起 Redis 后测 login
- [ ] `FileEntity` 表名改反引号（`file` 保留字隐患，一行）
- [ ] `schema.sql` 仍是 SQLite 方言（已从 yml 摘除 init），后续删掉或换 Flyway
