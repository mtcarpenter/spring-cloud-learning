## Gateway 过滤器工厂

## 概述

GatewayFilter Factory是Spring Cloud Gateway中提供的过滤器工厂。Spring CloudGateway的路由过滤器允许以某种方式修改传入的HTTP请求或输出的HTTP响应，只作用于特定的路由。Spring Cloud Gateway中内置了很多过滤器工厂，直接采用配置的方式使用即可，同时也支持自定义GatewayFilter Factory来实现更复杂的业务需求。

## 快速入门

### 创建应用

创建一个命名为： `gateway-cloud-client-filter-example` 的 Spring cloud 应用。

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

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
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

### AddRequestHeader过滤器工厂

通过名称我们可以快速明白这个过滤器工厂的作用是添加请求头。

在 `application.yml`中配置 

````properties
server:
  port: 8092
spring:
  application:
    name: gateway-route
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
    gateway:
      routes:
        - id: add_request_header_route
          uri: https://example.org
          predicates:
            - After=2020-05-01T14:45:39.145+08:00[Asia/Shanghai]
          filters:
            - AddRequestHeader=X-Request-Foo, Bar
logging:
  level:
    org.springframework.cloud.gateway: trace
    org.springframework.http.server.reactive: debug
    org.springframework.web.reactive: debug
    reactor.ipc.netty: debug

````

- `-AddRequestHeader=X-Request-Foo, Bar` :  为请求添加名为 `X-Request-Foo` ，值为 `Bar` 的请求头。
- `logging`: 设置 `gateway ` 请求打印日志级别

**断点测试**

断点打在 `org.springframework.cloud.gateway.filter.NettyRoutingFilter#filter` ，debug 启动：

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/bd017d95-2964-0118-607c-8ed329bf7de2.png)

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/e432677d-8ea1-47e5-e7e5-39ca65cc22d9.png)

### AddRequestParameter 过滤器工厂

新增请求参数

在 `application.yml`中配置 

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: add_request_header_route
          uri: https://example.org
          predicates:
            - After=2020-05-01T14:45:39.145+08:00[Asia/Shanghai]
          filters:
          #   请求参数 token:abc
            - AddRequestParameter=token,abc
```

### Retry 过滤器工厂

Retry 过滤器工厂是一种定义的重试的过滤器工厂。在 Retry 过滤器工厂有以下 5 个参数。

- retries：默认为3，重试次数，非负数
- series：用来指定哪些段的状态码需要重试，默认`SERVER_ERROR`，即5xx。
- statuses：用于指定哪些状态需要重试，默认为空，它跟`series`至少得指定一个。一般不怎么配置这个。
- methods：于指定那些方法的请求需要重试，默认为`GET`
- exceptions：用于指定哪些异常需要重试，默认为`java.io.IOException`、`java.util.concurrent.TimeoutException`

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: add_request_header_route
          uri: https://example.org
          predicates:
            - After=2020-05-01T14:45:39.145+08:00[Asia/Shanghai]
          filters:
             # 过滤器工厂名称
            - name: Retry
              args:
                  # 默认 3
                retries: 3
                series: SERVER_ERROR
                statuses: INTERNAL_SERVER_ERROR,BAD_GATEWAY
                methods: GET,POST,DELETE,PUT
                exceptions:
                  - java.io.IOException
                  - java.util.concurrent.TimeoutException
```

## 文章参考

- *https://cloud.spring.io/spring-cloud-static/spring-cloud-gateway*

## 代码示例

本文示例代码访问下面查看仓库：

- *Github：* [https://github.com/mtcarpenter/spring-cloud-learning](https://github.com/mtcarpenter/spring-cloud-learning)
- *Gitee：* [https://gitee.com/mtcarpenter/spring-cloud-learning](https://gitee.com/mtcarpenter/spring-cloud-learning)

其中，本文示例代码名称： 

- `gateway-cloud-client-filter-example`：过滤器断言工厂