# AI Agent交互模块

第一版定位：

项目问答。

例如：

用户：

> 用户登录流程在哪里？

Agent:

```
登录流程：

UserController.login()

↓

AuthenticationService

↓

UserMapper

↓

Database


涉及文件:

UserController.java

AuthService.java
```

SpringAI
负责问答总结
