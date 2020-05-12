## Gateway 端点监控(actuator)

## 概述

Spring Cloud Gateway源码中提供了 GatewayControllerEndpoint 类来修改路由配置。

## 快速入门

### 创建应用

创建一个命名为： `gateway-cloud-client-actuator-example` 的 Spring cloud 应用。

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
        <!--端点监控-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
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

### 配置

在 `application.yml`中配置 

````properties
server:
  port: 8094
spring:
  application:
    name: gateway-actuator
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
    gateway:
      routes:
        - id: path_route
          uri: https://example.org
          predicates:
            - Path=/route
logging:
  level:
    org.springframework.cloud.gateway: trace
    org.springframework.http.server.reactive: debug
    org.springframework.web.reactive: debug
    reactor.ipc.netty: debug
management:
  endpoints:
    web:
      exposure:
        #  可以使用 '*' 代表全部
        include: gateway
````

- `management.endpoints.web.exposure.include=gateway`: 表示将 gateway 端点暴露

### 启动测试

请求 `http://localhost:8094/actuator`

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/e6a9802f-6fc2-901e-c557-3535c41477b0.png)



## 监控端点列表

> 例如：`routes` 的全路径是 `/actuator/gateway/globalfilters` ，以此类推。

| ID              | HTTP Method        | Description                                     |
| :-------------- | :----------------- | :---------------------------------------------- |
| `globalfilters` | GET                | 展示所有的全局过滤器                            |
| `routefilters`  | GET                | 展示所有的过滤器工厂（GatewayFilter factories） |
| `refresh`       | POST【无消息体】   | 清空路由缓存                                    |
| `routes`        | GET                | 展示路由列表                                    |
| `routes/{id}`   | GET                | 展示指定id的路由的信息                          |
| `routes/{id}`   | POST【消息体如下】 | 新增一个路由                                    |
| `routes/{id}`   | DELETE【无消息体】 | 删除一个路由                                    |

## 全局过滤器列表

请求地址： http://localhost:8094/actuator/gateway/globalfilters

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/a3062340-7a58-10bc-4b92-3a5431c5d87f.png)

> 如果有自定义也会显示。

## 过滤器工厂列表

请求地址 ：  http://localhost:8094/actuator/gateway/routefilters

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/a15143c2-a16a-425a-3eef-a11231a7d742.png)

## 路由列表

请求地址 ：  http://localhost:8094/actuator/gateway/routefilters

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/b0666c2f-5989-0df8-3d6e-639f510b9ee3.png)





## 动态新增路由

请求地址 ：  http://localhost:8094/actuator/gateway/routes/new_route

```json
{
	"predicates": [{
		"name": "Path",
		"args": {
			"_genkey_0": "/new"
		}
	}],
	"uri": "https://example.org",
	"filters": [],
	"order": 0
}
```

**postman 测试**

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/ad909c27-b89a-6ce1-01c0-589aa22095ee.png)

**动态路由新增结果如下**

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/d2e70f8d-9d01-2f5a-6478-339c78297fce.png)

> 如果路由没有出来，可以通过端点刷新（`refresh`）。

## 文章参考

- *https://cloud.spring.io/spring-cloud-gateway/multi/multi__actuator_api.html*

## 代码示例

本文示例代码访问下面查看仓库：

- *Github：* [https://github.com/mtcarpenter/spring-cloud-learning](https://github.com/mtcarpenter/spring-cloud-learning)
- *Gitee：* [https://gitee.com/mtcarpenter/spring-cloud-learning](https://gitee.com/mtcarpenter/spring-cloud-learning)

其中，本文示例代码名称： 

- `gateway-cloud-client-actuator-example`：Gateway 端点监控(actuator)