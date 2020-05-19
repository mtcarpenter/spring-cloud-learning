## Spring Cloud  Sleuth 整合 Zipkin 

## 概述

Spring-Cloud-Sleuth是Spring Cloud的组成部分之一，为SpringCloud应用实现了一种分布式追踪解决方案，其兼容了Zipkin, HTrace和log-based追踪。

## 术语

**Span**：基本工作单元，发送一个远程调度任务就会产生一个Span，Span是用一个64位ID唯一标识的，Trace是用另一个64位ID唯一标识的。Span还包含了其他的信息，例如摘要、时间戳事件、Span的ID以及进程ID。

**Trace**：由一系列Span组成的，呈树状结构。请求一个微服务系统的API接口，这个API接口需要调用多个微服务单元，调用每个微服务单元都会产生一个新的Span，所有由这个请求产生的Span组成了这个Trace。

**Annotation**：用于记录一个事件，一些核心注解用于定义一个请求的开始和结束，这些注解如下。

- **cs** - Client Sent ：客户端发送一个请求，这个注解描述了Span的开始。
- **sr** - Server Received ：服务端获得请求并准备开始处理它，如果将其sr减去cs时间戳，便可得到网络传输的时间。
- **ss** - Server Sent ：服务端发送响应，该注解表明请求处理的完成（当请求返回客户端），用ss的时间戳减去sr时间戳，便可以得到服务器请求的时间。
- **cr** - Client Received ：客户端接收响应，此时Span结束，如果cr的时间戳减去cs时间戳，便可以得到整个请求所消耗的时间。

## 服务端

### 创建应用

创建一个命名为： `spring-cloud-sleuth-server-example` 的 Spring cloud 应用。

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
        <!-- nacos -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-sleuth</artifactId>
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
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-sleuth-dependencies</artifactId>
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
  port: 8080
spring:
  application:
    name: sleuth-server
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848


````

### 控制类实现

```java
@RestController
@RequestMapping("/server")
public class SleuthServerController {

    private static final Log log = LogFactory.getLog(SleuthServerController.class);

    @GetMapping("/sayHello")
    public String sayHello(){
        log.info("say hello");
        return "sayHello";
    }

    @GetMapping("/error")
    public String error(){
        throw new RuntimeException();
    }

}
```

### 启动测试

访问 http://localhost:8080/server/sayHello ，控制台显示如下：

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/402a963a-1b6d-2354-b52f-d9a54c1b5bad.png)

```java
2020-05-19 13:13:32.852 ERROR [sleuth-server,72b0a74e0fe92075,72b0a74e0fe92075,false] 16812 --- [nio-8080-exec-3] o.s.c.s.i.web.ExceptionLoggingFilter     : Uncaught exception thrown

```

在控制太会出现 sleuth 日志信息表示 sleth 整合已经成功了。

sleuth 控制台内容是由[appname, traceId, spanId, exportable]组成的，具体含义如下：

- `appname`：服务的名称，也就是spring.application.name的值。
-  `traceId`：整个请求的唯一ID，它标识整个请求的链路。
- `spanId`：基本的工作单元，发起一次远程调用就是一个span。
- `exportable`：决定是否导入数据到Zipkin中。

## Zipkin

### 那么什么是Zipkin呢？

​		Zipkin是Twitter的一个开源项目，它基于Google的Dapper实现，被业界广泛使用。Zipkin致力于收集分布式系统的链路数据，提供了数据持久化策略，也提供面向开发者的API接口，用于查询数据，还提供了UI组件帮助我们查看具体的链路信息。

Zipkin提供了可插拔式的数据存储方式，目前支持的数据存储有In-Memory、MySQL、Cassandra和ElasticSearch。

Zipkin的架构图如图13-1所示，它主要由4个核心组件构成：

- Collector：链路数据收集器，主要用于处理从链路客户端发送过来的链路数据，将这些数据转换为Zipkin内部处理的Span格式，以支持后续的存储、分析和展示等功能。

- Storage：存储组件，用来存储接收到的链路数据，默认会将这些数据存储在内存中，同时支持多种存储策略，比如将链路数据存储在MySQL、Cassandra和ElasticSearch中。

- RESTful API：API组件，它是面向开发者的，提供外部访问API接口，可以通过这些API接口来自定义展示界面。

- Web UI：UI组件，基于API接口实现的上层应用，用户利用UI组件可以很方便地查询和分析链路数据。

  ![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/c99886fe-80a6-b91d-519d-e88205bb24a0.png)

### 下载 

**docker 运行**

```sh
docker run -d -p 9411:9411 openzipkin/zipkin
```

**Jar 运行**

```java
curl -sSL https://zipkin.io/quickstart.sh | bash -s
java -jar zipkin.jar
```

### 启动

访问路径  http://localhost:9411/zipkin/

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/eb98e300-5f1f-7a40-56c2-df9baef224c1.png)

> zipkin 客户端就算启动成功，下面整合sping cloud 中。

### spring cloud 整合 zipkin

```yaml
<!--<dependency>-->
    <!--<groupId>org.springframework.cloud</groupId>-->
    <!--<artifactId>spring-cloud-starter-sleuth</artifactId>-->
<!--</dependency>-->
<!--zipkin-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```

- zipkin 中有 sleuth 依赖 不需要单独引入

### 在 `application.yml`中配置 

```yaml
  zipkin:
    base-url: http://localhost:9411
    # 支持通过服务发现来定位host name
    locator:
      discovery:
        enabled: true
  sleuth:
    sampler:
      # zipkin 抽样比例 在默认情况下，该值为0.1
      probability: 1.0
```

> 通过客户端演示请求链路

## 客户端

### 创建应用

创建一个命名为： `spring-cloud-sleuth-client-example` 的 Spring cloud 应用。

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
        <!-- nacos -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

        <!--zipkin-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zipkin</artifactId>
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
  port: 8083
spring:
  application:
    name: sleuth-client
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
  zipkin:
    base-url: http://localhost:9411
    locator:
      discovery:
        enabled: true
  sleuth:
    sampler:
      # zipkin 抽样比例 在默认情况下，该值为0.1
      probability: 1.0

````

###  服务端 feign 接口

```java
@FeignClient(name = "sleuth-server", path = "server")
public interface SleuthServerFeign {

    @GetMapping("/sayHello")
    String sayHello();

    @GetMapping("/error")
    String error();
}
```

### 控制类

```java
@RestController
@RequestMapping("/client")
public class SleuthClientController {
    @Autowired
    private SleuthServerFeign sleuthServerFeign;

    @GetMapping("/sayHello")
    public String sayHello() {
        return sleuthServerFeign.sayHello();
    }

    @GetMapping("/error")
    public String error() {
        return sleuthServerFeign.error();
    }
}
```

### 启动类

```java
@SpringBootApplication
@EnableFeignClients
public class SpringCloudSleuthClientExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudSleuthClientExampleApplication.class, args);
    }

}
```

- `@EnableFeignClients`: feign 生效

### 启动测试

分别多次请求地址 ： http://localhost:8083/client/sayHello 和 http://localhost:8083/client/error，进入 zipkin 服务端页面进入查询

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/21263952-7d0d-f7af-f50a-9e7b7d327dc4.png)

**请求详情**

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/fe1f477f-b73f-72b0-6e62-fcdcd3a7cc94.png)

每一段请求的执行时间 ，点击服务端可以很清楚看到  `cs`、`sr`、`ss`、`cr`的执行时间

## 文章参考

- *https://cloud.spring.io/spring-cloud-sleuth/2.1.x/single/spring-cloud-sleuth.html*
- 深入理解Spring Cloud与微服务构建（第2版） 
- Spring Cloud微服务：入门、实战与进阶 

## 代码示例

本文示例代码访问下面查看仓库：

- *Github：* [https://github.com/mtcarpenter/spring-cloud-learning](https://github.com/mtcarpenter/spring-cloud-learning)
- *Gitee：* [https://gitee.com/mtcarpenter/spring-cloud-learning](https://gitee.com/mtcarpenter/spring-cloud-learning)

其中，本文示例代码名称： 

- `spring-cloud-sleuth-server-example`： Spring Cloud Sleuth 服务端
- `spring-cloud-sleuth-client-example`:  Spring Cloud Sleuth 客户端