## Gateway Filter和Global Filter

## 概述

Spring Cloud Gateway中的Filter从接口实现上分为两种：一种是Gateway Filter，另外一种是Global Filter。

## Gateway Filter概述

Gateway Filter是从Web Filter中复制过来的，相当于一个Filter过滤器，可以对访问的URL过滤，进行横切处理（切面处理），应用场景包括超时、安全等。

##  Global Filter概述

Spring Cloud Gateway定义了Global Filter的接口，让我们可以自定义实现自己的GlobalFilter。Global Filter是一个全局的Filter，作用于所有路由。

## Gateway Filter和Global Filter的区别

从路由的作用范围来看，Global filter 会被应用到所有的路由上，而 Gateway filter 则应用到单个路由或者一个分组的路由上。从源码设计来看，Gateway Filter和Global Filter两个接口中定义的方法一样都是 Mono filter()，唯一的区别就是 GatewayFilter 继承了 ShortcutConfigurable，而 GlobalFilter 没有任何继承。

## 快速入门

### 创建应用

创建一个命名为： `gateway-cloud-client-global-filter-example` 的 Spring cloud 应用。

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

### 配置

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
logging:
  level:
    org.springframework.cloud.gateway: trace
    org.springframework.http.server.reactive: debug
    org.springframework.web.reactive: debug
    reactor.ipc.netty: debug

````

- `logging`: 设置 `gateway ` 请求打印日志级别

### 创建自定义的Gateway Filter

```java
public class RequestTimeFilter implements GatewayFilter, Ordered {
    private static final Log log = LogFactory.getLog(GatewayFilter.class);
    private static final String START_TIME = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    Long startTime = exchange.getAttribute(START_TIME);
                    if (startTime != null) {
                        Long endTime = System.currentTimeMillis() - startTime;
                        log.info(exchange.getRequest().getURI().getRawPath() + ": " + endTime + "ms");
                    }
                })
        );
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
```

### 路由配置

```java
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes().route(r ->
                r.path("/route")
                        //转发路由
                        .uri("https://example.org")
                        //注册自定义过滤器
                        .filters(new RequestTimeFilter())
                        //给定id
                        .id("filter_route"))
                .build();
    }
```

### 启动测试

请求 `http://localhost:8093/route`,控制台打印如下：

```java
2020-05-11 16:14:05.924  INFO 352 --- [ctor-http-nio-3] o.s.cloud.gateway.filter.GatewayFilter   : /route: 1ms
```

控制台看到请求响应时间。

### 创建自定义的Global Filter

```java
@Component
public class GlobalOrderFilter {
    private static final Log log = LogFactory.getLog(GlobalOrderFilter.class);

    @Bean
    @Order(-1)
    public GlobalFilter a() {
        return (exchange, chain) -> {
            log.info("first pre filter");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("third post filter");
            }));
        };
    }

    @Bean
    @Order(0)
    public GlobalFilter b() {
        return (exchange, chain) -> {
            log.info("second pre filter");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("second post filter");
            }));
        };
    }

    @Bean
    @Order(1)
    public GlobalFilter c() {
        return (exchange, chain) -> {
            log.info("third pre filter");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("first post filter");
            }));
        };
    }
}
```

### 启动测试

请求 `http://localhost:8093/route`,控制台打印如下：

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/65c58650-5e47-5f54-e074-663229a4575b.png)



过滤器链会使用 `org.springframework.core.Ordered` 注解所指定的顺序，进行排序。Spring Cloud Gateway区分了过滤器逻辑执行的”pre”和”post”阶段，所以优先级高的过滤器将会在pre阶段最先执行，优先级最低的过滤器则在post阶段最后执行。

### Global Filter token

```java
@Component
public class GlobalTokenFilter implements GlobalFilter, Ordered {

    private static final Log log = LogFactory.getLog(GlobalTokenFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getQueryParams().getFirst("token");
        if (token == null || token.isEmpty()) {
            log.info("token is empty...");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
       return Ordered.HIGHEST_PRECEDENCE;
    }

}
```

`getOrder` 值越小越先执行。

### 启动测试：

请求 `http://localhost:8093/route`,控制台打印如下：

```java
2020-05-11 16:36:35.236  INFO 9644 --- [ctor-http-nio-3] c.m.g.c.c.g.f.example.GlobalTokenFilter  : token is empty...
```

## 文章参考

- *https://cloud.spring.io/spring-cloud-static/spring-cloud-gateway*

## 代码示例

本文示例代码访问下面查看仓库：

- *Github：* [https://github.com/mtcarpenter/spring-cloud-learning](https://github.com/mtcarpenter/spring-cloud-learning)
- *Gitee：* [https://gitee.com/mtcarpenter/spring-cloud-learning](https://gitee.com/mtcarpenter/spring-cloud-learning)

其中，本文示例代码名称： 

- `gateway-cloud-client-global-filter-example`：Gateway Filter和Global Filter