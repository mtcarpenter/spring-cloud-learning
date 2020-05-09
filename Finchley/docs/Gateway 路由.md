## Gateway 路由篇

## 概述

Spring cloud gateway是spring官方基于Spring 5.0、Spring Boot2.0和Project Reactor等技术开发的网关，Spring Cloud Gateway旨在为微服务架构提供简单、有效和统一的API路由管理方式，Spring Cloud Gateway作为Spring Cloud生态系统中的网关，目标是替代Netflix Zuul，其不仅提供统一的路由方式，并且还基于Filer链的方式提供了网关基本的功能，例如：安全、监控/埋点、限流等。

## 核心概念

网关提供 API 全托管服务，丰富的API管理功能，辅助企业管理大规模的API，以降低管理成本和安全风险，包括协议适配、协议转发、安全策略、防刷、流量、监控日志等贡呢。一般来说网关对外暴露的URL或者接口信息，我们统称为路由信息。如果研发过网关中间件或者使用过Zuul的人，会知道网关的核心是Filter以及Filter Chain（Filter责任链）。Sprig Cloud Gateway也具有路由和Filter的概念。下面介绍一下Spring Cloud Gateway中几个重要的概念。

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/385d5b2c-cafe-c0ef-f227-6481ab477c45.png)

- 路由。路由是网关最基础的部分，路由信息有一个ID、一个目的URL、一组断言和一组Filter组成。如果断言路由为真，则说明请求的URL和配置匹配
- 断言。Java8中的断言函数。Spring Cloud Gateway中的断言函数输入类型是Spring5.0框架中的ServerWebExchange。Spring Cloud Gateway中的断言函数允许开发者去定义匹配来自于http request中的任何信息，比如请求头和参数等。
- 过滤器。一个标准的Spring webFilter。Spring cloud gateway中的filter分为两种类型的Filter，分别是Gateway Filter和Global Filter。过滤器Filter将会对请求和响应进行修改处理。

## 工作原理图





![img](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/0b9fcab1-4a15-671f-5d9f-26c08f091e5e.png)



Gateway 的客户端会向 Spring Cloud Gateway 发起请求，请求首先会被 HtpWebHandlerAdapter 进行提取组装成网关的上下文，然后网关的上下文会传递到DispatcherHandler. DispatcherHandler 是所有请求的分发处理器，DispatcherHandler主要负责分发请求对应的处理器，比如将请求分发到对应RoutePredicate-
HandlerMapping (路由断言处理映射器)。路由断言处理映射器主要用于路由的查找，以及找到路由后返回对应的FilteringWebHandler。FilteringWebHandler 主要负责组装Filter链表并调用 Filter 执行- - 系列的Filter处理，然后把请求转到后端对应的代理服务处理，处理完毕之后，将 Response 返回到Gateway客户端。

## 服务端

### 1、创建应用

创建一个命名为： `gateway-cloud-server-example` 的 Spring cloud 应用，作为gateway 请求的服务端测试。

### 2、添加依赖

```xml
    <properties>
        <java.version>1.8</java.version>
        <spring.cloud.alibaba.version>2.1.2.RELEASE</spring.cloud.alibaba.version>
        <spring.cloud.version>Greenwich.RELEASE</spring.cloud.version>
    </properties>   
	<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>

        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring.cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring.cloud.alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

        </dependencies>
    </dependencyManagement>

```

### 3、增加配置

在 `application.properties` 中配置  的地址：

````properties
server:
  port: 8090
spring:
  application:
    name: gateway-server
  cloud:
    # nacos 服务端地址
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
````

### 4、加注解

在启动类上加入注解`@EnableDiscoveryClient`。

### 5、控制类

```java
@RestController
@RequestMapping("/route")
public class RouteTestController {

    @GetMapping(value = "")
    public String hello() {
        return "8090:hello";
    }

    @GetMapping(value = "/sayHello/{name}")
    public String sayHello(@PathVariable String name) {
        return "8090:sayHello:"+name;
    }

}

```

## 客户端

### 1、创建应用

创建一个命名为： `gateway-cloud-client-route-example` 的 Spring cloud 应用，作为 gateway 路由相关测试。

### 2、添加依赖

```xml
    <properties>
        <java.version>1.8</java.version>
        <spring.cloud.alibaba.version>2.1.2.RELEASE</spring.cloud.alibaba.version>
        <spring.cloud.version>Greenwich.RELEASE</spring.cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!-- gateway -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        <!-- nacos -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
    </dependencies>
 	<dependencyManagement>

        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring.cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring.cloud.alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

        </dependencies>
    </dependencyManagement>
```

### 3、增加配置

在 `application.properties` 中配置  的地址：

````properties
server:
  port: 8091
spring:
  application:
    name: gateway-route
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
    gateway:
      routes:
        - id: path_route
          uri: http://localhost:8090
          predicates:
            - Path=/route
````

- gateway 配置解释：
  - `id`: 路由名称，标识，唯一
  - `uri`：转发的目标地址
  - `predicates`: 路由规则
  - `Path`:  路径

**gateway 配置文件等同于如下代码配置：**

```java
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder){
        // 1 、简单路由
        return routeLocatorBuilder.routes()
                .route(r-> r.path("/route")
                        .uri("http://localhost:8090")
                        .id("path_route"))
                .build();
    }

```

### 4、加注解

在启动类上加入注解`@EnableDiscoveryClient`。

### 5、测试

分别启动服务端（8090）和客户端(8091)，客户端请求地址 http://localhost:8091/route，会转发到 http://localhost:8091/route。

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/05c588e6-b1f8-7587-6d73-6df42ccaf05f.png)

### 6 、服务地址转发

```yaml
server:
  port: 8091
spring:
  application:
    name: gateway-route
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
    gateway:
      routes:
        # 通过IP 地址转发
        - id: path_route
          uri: http://localhost:8090
          predicates:
            - Path=/route
        #  通过服务地址
        - id: path_route_lb
          uri: lb://gateway-server
          predicates:
            - Path=/route/**
```

- `lb://gateway-server`:  `gateway-server`注册在 nacos 的服务名称，通过服务名称转发。
- `/route/**`: `/**`表示多级路径(path)，如:`route/say`,`route\hi\q` 等。



## 文章参考

- *https://cloud.spring.io/spring-cloud-static/spring-cloud-gateway*

## 代码示例

本文示例代码访问下面查看仓库：

- *Github：* [https://github.com/mtcarpenter/spring-cloud-learning](https://github.com/mtcarpenter/spring-cloud-learning)
- *Gitee：* [https://gitee.com/mtcarpenter/spring-cloud-learning](https://gitee.com/mtcarpenter/spring-cloud-learning)

其中，本文示例代码名称：

- `gateway-cloud-server-example`：gateway 服务端
- `gateway-cloud-client-route-example` : gateway 路由客户端