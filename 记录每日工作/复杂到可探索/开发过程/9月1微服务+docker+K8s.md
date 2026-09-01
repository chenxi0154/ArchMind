把微服务，Docker，K8s看层三个不同层次的问题：
>微服务解决“系统怎么拆”
>Docker解决“程序怎么标准化地允许”
>K8s解决“这么多程序怎么部署，管理，扩缩和自愈”

## 整体理解

### 微服务架构
```
                    用户
                     │
                     ▼
              ┌─────────────┐
              │   Gateway   │
              └──────┬──────┘
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
   用户服务       商品服务       订单服务
   User Service   Product       Order
        │            │            │
        ▼            ▼            ▼
      MySQL        MySQL        MySQL
```
然后：
### Docker

```
User Service
      ↓
  Docker镜像
      ↓
 Docker Container
```

如果系统变成了：
```
User Service       × 10
Product Service    × 8
Order Service      × 15
Payment Service    × 5
Gateway            × 3
```
这些东西部署在几十台服务器上，那么：
### k8s管理
```
              Kubernetes
                   │
        ┌──────────┼──────────┐
        ▼          ▼          ▼
      Node1       Node2      Node3
        │          │          │
      Docker      Docker     Docker
        │          │          │
      Pod         Pod        Pod
```

微服务是架构思想，Docker是容器化技术，K8s是容器编排平台


---


## 微服务架构的问题(深刻讲讲)

当项目被拆解成：
```
user-service
product-service
order-service
payment-service
inventory-service
```
每一个都是一个SpringBoot项目。
那么部署的时候，每一个SpringBoot项目都需要配环境
```
JDK
配置文件
环境变量
依赖
端口
网络
日志
```

比如：

`服务器A Java 17 user-service.jar`

服务器B：

`Java 17 order-service.jar`

服务器C：

`Java 17 payment-service.jar`

越来越麻烦。

于是出现了 **Docker**。


---

## Docker解决了什么？
Docker的核心思想：
	 把应用和它运行所需要的环境一起打包

```
Order Service
     │
     ├── order-service.jar
     ├── JDK
     ├── Linux依赖
     ├── 配置
     └── 其他依赖
           ↓
      Docker Image
           ↓
      Container
```


## Docker对微服务的重要性

微服务架构的特点是： 很多的独立服务
```
User Service
Product Service
Order Service
Payment Service
Inventory Service
```

如果没有容器：
```
服务器
 ├── Java
 ├── User
 ├── Java
 ├── Product
 ├── Java
 ├── Order
 └── ...
```

环境管理很麻烦

Docker：
```
Docker
│
├── user-service
├── product-service
├── order-service
├── payment-service
└── inventory-service
```



---


## Docker实际上解决了一个非常经典的问题

开发人员：

> “我电脑上能运行啊。”

服务器：

> “为什么运行不了？”

因为环境不同：

`开发环境 Java 17 MySQL 8 Redis 7 Linux`

服务器：

`Java 11 MySQL 5 Redis 6`

Docker把环境标准化：

`开发 ↓ Docker Image ↓ 测试 ↓ Docker Image ↓ 生产 ↓ Docker Image`

因此：

> **只要 Docker 运行环境一致，应用运行环境就更加可控。**


---

## 当微服务量较大时：

假设你现在有：

`100个微服务实例`

比如：
```
user-service × 10
order-service × 20
payment-service × 10
product-service × 20
inventory-service × 20
gateway × 5
...
```

你用 Docker 手动管理：

```
docker run ...
docker stop ...
docker restart ...
docker logs ...
```
会发生什么？

**管理灾难。**


---

## K8s管理

自动部署
自动扩容
自动故障恢复



---


## 三者关系

```
                 微服务架构
                     │
            ┌────────┴────────┐
            │                 │
       User Service      Order Service
            │                 │
            ▼                 ▼
       Docker Image      Docker Image
            │                 │
            ▼                 ▼
        Container          Container
            │                 │
            └────────┬────────┘
                     ▼
               Kubernetes
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
      Node 1       Node 2       Node 3
```

```
微服务
  ↓
决定“拆成什么”

Docker
  ↓
决定“怎么标准化运行”

K8s
  ↓
决定“怎么大规模管理”
```

企业中存在关系：
```
                   用户
                    │
                    ▼
               Load Balancer
                    │
                    ▼
              Kubernetes
                    │
       ┌────────────┼────────────┐
       ▼            ▼            ▼
   Gateway Pod   Gateway Pod   Gateway Pod
       │
       ├───────────────┐
       ▼               ▼
 User Service      Order Service
   Pods               Pods
       │               │
       ▼               ▼
    Database          Database
```
而每一个 Pod 里面运行的应用通常是容器化的。

所以：

`微服务 ↓ Docker ↓ Kubernetes`

在企业环境中经常形成这样的组合。

## 三个技术依次出现的原因

```
单体应用
   ↓
应用越来越复杂
   ↓
需要拆分
   ↓
微服务
   ↓
服务数量越来越多
   ↓
需要标准化运行环境
   ↓
Docker
   ↓
容器数量越来越多
   ↓
人工管理困难
   ↓
Kubernetes
```