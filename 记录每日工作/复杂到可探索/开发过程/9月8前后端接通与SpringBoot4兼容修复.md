# 9月8 前后端接通前置 + Spring Boot 4 兼容三连修

> 主题：把「后端切服务器 MySQL/Redis/Neo4j 的配置落盘」推进到「后端在 Spring Boot 4 下能真正启动」，给前后端接通清路
> 状态：后端本地已可正常启动（连服务器 MySQL/Redis）；「前端切真实接口跑通一次分析」还差几步待办

---

## 一、关键认知（先记住）

1. 本工程被 **Spring AI 2.0 要求 Boot 4** 锁死基线：Boot 4.1.1-SNAPSHOT / Spring Framework 7 / Jackson 3 / **默认禁止 Bean 循环引用**。底层 ORM、JSON、注入写法都按这套来，不能沿用 Boot 2/3 时代组件。
2. 前后端契约最易踩的坑：**成功码**。后端原为 `200`（HTTP 风格），前端与 mock 按 `code === 0` 判成功 → 本次统一为 **0**。

---

## 二、接线前已确认就绪（服务器侧，本次核对）

| 项 | 状态 |
|---|---|
| MySQL 5 张表（user/project/project_source/file/project_overview） | ✅ 已在服务器建好 |
| 环境变量（密码类） | ✅ 已写入 IDEA Run Configuration（`MYSQL_HOST=172.21.160.203` / `MYSQL_PASSWORD` / `NEO4J_PASSWORD`；**`DEEPSEEK_API_KEY` 未设，仍是启动前提**） |
| 端口连通 | ✅ 本机 `Test-NetConnection` 对 3306 / 6379 / 7687 均为 True |
| Redis | ✅ 已部署（`docker ps` 可见），登录/JWT 校验的硬依赖就绪 |
| `file` 表名保留字 | ✅ `@TableName("`file`")` 已加反引号 |

---

## 三、改动清单（本次工作区，均未提交）

| 文件 | 改动 | 归属 |
|---|---|---|
| `pom.xml` | `mybatis-plus-boot-starter:3.5.8` → `mybatis-plus-spring-boot4-starter:3.5.17` | 兼容修复 |
| `common/result/ResultCode.java` | `SUCCESS(200)` → `SUCCESS(0)` | 前后端契约 |
| `config/SecurityConfig.java` | 三个 web Bean 由构造器注入改 `filterChain()` 方法参数注入 | 解循环 |
| `entity/FileEntity.java` | `@TableName("file")` → `` @TableName("`file`") `` | 保留字 |
| `service/impl/ProjectOverviewServiceImpl.java` | ObjectMapper Jackson2 → Jackson3（`tools.jackson`） | 兼容修复 |
| `resources/application.yml` | redis host → 服务器 IP | 接线 |

---

## 四、三个启动错误逐层拆解（今天的重头戏）

### 问题 1：MyBatis-Plus 3.5.8 × Boot 4 → `SqlSessionFactory` 缺失

**现象**：启动在 web server 阶段失败，`userMapper` bean 创建报 `Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required`。

**原因**（翻本地 jar 找到实锤）：
1. `mybatis-plus-boot-starter:3.5.8` 配套的自动配置 `mybatis-plus-spring-boot-autoconfigure:3.5.8`，其 pom 声明依赖的是 **`spring-boot-autoconfigure:2.7.18`** —— 它是 **Boot 2.x 时代**的组件。
2. 它声明 `@AutoConfigureAfter(DataSourceAutoConfiguration.class)`，引用**旧包名** `org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration`。而 Boot 4 把 JDBC 拆进独立模块 `spring-boot-jdbc`，该类**搬家到新包** `org.springframework.boot.jdbc.autoconfigure`，旧包不存在 → 该自动配置在 Boot 4 下整体失效 → `SqlSessionFactory` 从不被创建 → 所有 Mapper 初始化失败。
3. pom 里手动覆盖 `mybatis:3.5.19` + `mybatis-spring:4.0.0` 只能换「核心库」，**换不了「自动配置层」**。

**解决**：换成官方 Boot 4 专属坐标 **`com.baomidou:mybatis-plus-spring-boot4-starter:3.5.17`**——BOM 对齐 Boot 4.0.1、自带 `mybatis-spring:4.0.0`、自动配置按 Boot 4 重新编译（引用新包名）。
⚠️ 注意：3.5.13 是 Boot 4 支持首个版本，有「更新日志号称支持实际跑不起来」的 issue（#6970），**别用 3.5.13**，用 ≥3.5.17。
DAO 层只用了 `BaseMapper` + `LambdaQueryWrapper` 稳定 API，3.5.8→3.5.17 无需改业务代码。

### 问题 2：Bean 循环依赖（Spring 7 默认禁止）

**现象**：`jwtAuthenticationFilter → authServiceImpl → userServiceImpl → securityConfig →(回到) jwtAuthenticationFilter`，报循环引用。

**原因**：`SecurityConfig` 用构造器注入了 `JwtAuthenticationFilter`；而 `filter → authService → userService` 需要 `passwordEncoder`，`passwordEncoder` 恰好是**定义在 `SecurityConfig` 上的 @Bean** → 想产出 passwordEncoder 得先把 `SecurityConfig` 建完，建 `SecurityConfig` 又要先建 filter → 死环。（此前被问题 1 挡住没暴露。）

**解决**：把 `jwtAuthenticationFilter / authenticationEntryPoint / accessDeniedHandler` 三个从「构造器 final 字段注入」改为 **`filterChain(HttpSecurity, ...)` 方法参数注入**（Spring Security 官方样例写法），删掉 `@RequiredArgsConstructor` 和三个字段。`SecurityConfig` 实例创建不再依赖 filter，环即断开。

### 问题 3：注入不到 ObjectMapper bean（Boot 4 只有 Jackson 3）

**现象**：`ProjectOverviewServiceImpl` 构造第 5 参需要 `com.fasterxml.jackson.databind.ObjectMapper`，找不到该 bean。

**原因**：Boot 4 的 `starter-json` 默认序列化库已升到 **Jackson 3**（坐标组 `tools.jackson`，databind 解析为 3.1.5），只自动提供一个 `tools.jackson` 的 ObjectMapper。项目里另两个 handler（`AuthenticationEntryPointImpl` / `AccessDeniedHandlerImpl`）早就用 `tools.jackson`，唯独这个 Service 还注入 Jackson 2（`com.fasterxml.jackson`）类型 → 无对应 bean。

**解决**：`import` 换成 `tools.jackson.databind.ObjectMapper`；异常捕获从 `JsonProcessingException` 改为 `tools.jackson.core.JacksonException`（Jackson 3 已把 JsonProcessingException 并入 JacksonException）。

---

## 五、现状与剩余待办

- ✅ 后端在 Boot 4.1.1-SNAPSHOT 下本地可正常启动（本地连服务器库）。
- ⏳ 前端「接通 + 一次项目概况分析」还剩：
  1. 设 `DEEPSEEK_API_KEY`（不设则找不到 ChatModel，应用起不来）；
  2. 手动插一条 `project(id=1, ...)`——后端没有「建项目」API，而前端 `PROJECT_ID=1` 硬编码，上传/分析都依赖它存在；
  3. 可选：`analyze` 返回体补 `projectName` + `source` 元信息，否则前端顶部标题空白、同步状态显示 '—'。
- 提交提醒：以上改动与 9/7 未提交内容都在工作区，跑通后可一起 commit。

---

## 六、给后续的坑位提示（省一次踩坑）

- 遇到 Mapper 报 `sqlSessionFactory required` 别再怀疑环境，先查 MyBatis 自动配置是否与 Boot 大版本配套（Boot4 只能 `mybatis-plus-spring-boot4-starter`）。
- Spring 7 禁循环引用：安全配置类不要「构造时」依赖 filter 链上的 Bean，用 `@Bean` 方法参数。
- Boot 4 里写 Jackson 一律认准 `tools.jackson` 包。
