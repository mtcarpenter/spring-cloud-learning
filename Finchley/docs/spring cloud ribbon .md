## Spring Cloud Ribbon

## 概述

目前主流的负载方案分为两种：一种是集中式负载均衡，在消费者和服务提供方中间使用独立的代理方式进行负载，有硬件的（比如F5），也有软件的（比如Nginx）。另一种则是客户端自己做负载均衡，根据自己的请求情况做负载，Ribbon就属于客户端自己做负载。如果用一句话介绍，那就是：Ribbon是Netflix开源的一款用于客户端负载均衡的工具软件。GitHub地址：https://github.com/Netflix/ribbon。《Spring Cloud微服务：入门、实战与进阶 》

## 服务端

### 创建应用

创建一个命名为： `spring-cloud-ribbon-server-example` 的 Spring cloud 应用。

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
  port: 8080
spring:
  application:
    name: ribbon-server
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
````

### 控制类实现

```java
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Log log = LogFactory.getLog(UserController.class);

    @GetMapping("/sayHello")
    public String sayHello(){
        log.info("say hello");
        return "sayHello";
    }

}

```

启动两个服务端 ，端口为`8080` 和 `8081`

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/c3d0b377-381e-25f7-6496-282a74a070d9.png)

## 客户端

### 创建应用

创建一个命名为： `spring-cloud-ribbon-resttemplate-example` 的 Spring cloud 应用。

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
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
  application:
    name: ribbon-rest


````

### RestTemplate 配置

```java
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}

```

### 控制类

```java
@RestController
@RequestMapping("/test")
public class TestController {
    private static final Log log = LogFactory.getLog(TestController.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/instances")
    public  List<ServiceInstance> instances(){
        List<ServiceInstance> instances = discoveryClient.getInstances("ribbon-server");
        return instances;
    }

    @GetMapping("/randomServerUrl")
    public String randomServerUrl(){
        List<ServiceInstance> instances = discoveryClient.getInstances("ribbon-server");
        List<String> urls = instances.stream().map(s->s.getUri().toString()).collect(Collectors.toList());
        // 随机获取
        int i = ThreadLocalRandom.current().nextInt(urls.size());
        String requestUrl = urls.get(i).concat("/user/sayHello");
        log.info("request url = "+requestUrl);
        String result = restTemplate.getForObject(requestUrl, String.class);
        return result;
    }



}

```

- 接口`instances`：查询 `ribbon-server` 基本信息
- 接口`randomServerUrl`：随机访问服务端

### 启动程序

访问接口 http://localhost:8083/test/instances ,显示服务端如下信息:

```json
[
    {
        serviceId: "ribbon-server",
        host: "192.168.2.105",
        port: 8081,
        secure: false,
        metadata: {
            nacos.instanceId: "192.168.2.105#8081#DEFAULT#DEFAULT_GROUP@@ribbon-server",
            nacos.weight: "1.0",
            nacos.cluster: "DEFAULT",
            nacos.healthy: "true",
            preserved.register.source: "SPRING_CLOUD"
        },
        uri: "http://192.168.2.105:8081",
        scheme: null,
        instanceId: null
    },
    {
        serviceId: "ribbon-server",
        host: "192.168.2.105",
        port: 8080,
        secure: false,
        metadata: {
            nacos.instanceId: "192.168.2.105#8080#DEFAULT#DEFAULT_GROUP@@ribbon-server",
            nacos.weight: "1.0",
            nacos.cluster: "DEFAULT",
            nacos.healthy: "true",
            preserved.register.source: "SPRING_CLOUD"
        },
        uri: "http://192.168.2.105:8080",
        scheme: null,
        instanceId: null
    }
]
```

通过应用名称获取实例的基本信息。

浏览器多次访问接口 http://localhost:8083/test/randomServerUrl ,进入控制台查看，

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/dd978b6c-31ce-3529-e7ec-302d8d546a56.png)

从输出的结果中可以看到，负载起作用了。

## @RestTemplate 整合 Ribbon

**加入依赖**

```xml
	<dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
    </dependency>
```

> 使用`spring-cloud-starter-alibaba-nacos-discovery` 在当前版本不需要额外整合 ribbon 

### 修改

```java
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
```

- ` @LoadBalanced`:开启客户端负载均衡功能

### 控制类

```java
  @GetMapping("/serviceId")
    public String serviceId(){
        String result = restTemplate.getForObject("http://ribbon-server/user/sayHello", String.class);
        return result;
    }
```

也能达到上面相同的效果。

## Ribbon 相关类和概念说明

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/5f35a7fb-5aed-1c0d-499d-8f42e062fb33.png)

## Ribbon 负载均衡策略

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/c436bbe8-de42-aef2-cbff-f01f40536eb3.png)

## Java代码配置

**通过代码实现 随机访问服务端**

```java
@Configuration
public class RibbonConfig {

    @Bean
    public IRule iRule(){
        return new RandomRule();
    }
}
```

> 此配置类需要和配置类隔离，不能被 @ComponentScan 扫描到，spring 和 ribbon 上下文重叠，不然会被 ribbon 客户端共享，官方提示如下：
>
> The `CustomConfiguration` class must be a `@Configuration` class, but take care that it is not in a `@ComponentScan` for the main application context. Otherwise, it is shared by all the `@RibbonClients`. If you use `@ComponentScan` (or `@SpringBootApplication`), you need to take steps to avoid it being included (for instance, you can put it in a separate, non-overlapping package or specify the packages to scan explicitly in the `@ComponentScan`).

 ```java
@SpringBootApplication
@RibbonClient(name = "ribbon-server",configuration = RibbonConfig.class)
public class SpringCloudRibbonResttemplateExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudRibbonResttemplateExampleApplication.class, args);
    }

}
 ```

- `@RibbonClient(name = "ribbon-server",configuration = RibbonConfig.class)`: 请求`ribbon-server`服务端，采用的`RibbonConfig`随机算法。

**全局配置**

```java
@RibbonClients(defaultConfiguration = RibbonConfig.class ) 
```

请求所有微服务都采用自定义配置。

**启动测试**

启动 `ribbon-server` 8081和 8080 端口，启动客户端 8083 ,访问 http://localhost:8083/test/serviceId ，通过服务端控制端日志查看。

## 配置项配置

**配置属性方式：**

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/132564d2-c8f2-7e5c-a569-b4cd138a9097.png)

在 `application.yml` 配置如下

```yaml
# 针对 ribbon-server ribbon配置
ribbon-server:
  ribbon:
    # 加载规则
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
```

同上面的 `java` 代码配置一样，不需要考虑上下文扫描问题，也更容易理解

## 首次加载过慢问题

在客户端请求 http://localhost:8083/test/serviceId 

```java
    @GetMapping("/serviceId")
    public String serviceId(){
        String result = restTemplate.getForObject("http://ribbon-server/user/sayHello", String.class);
        return result;
    }
```

在地址中通过 `serviceId` 请求首次加载相对比较慢，可以通过在`application.yml`配置如下解决：

```yaml
# 饥饿加载
ribbon:
  eager-load:
    enabled: true
    # 加载列表 多个 使用逗号隔开
    clients: ribbon-server
```



## 文章参考

- *https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-ribbon.html*

## 代码示例

本文示例代码访问下面查看仓库：

- *Github：* [https://github.com/mtcarpenter/spring-cloud-learning](https://github.com/mtcarpenter/spring-cloud-learning)
- *Gitee：* [https://gitee.com/mtcarpenter/spring-cloud-learning](https://gitee.com/mtcarpenter/spring-cloud-learning)

其中，本文示例代码名称： 

- `spring-cloud-ribbon-server-example`： Spring Cloud Ribbon 服务端
- `spring-cloud-ribbon-resttemplate-example`:  Spring Cloud Ribbon RestTemplate