## Spring Cloud Feign 基本使用

## 概述

Feign是一个声明式的REST客户端，它能让REST调用更加简单。Feign提供了HTTP请求的模板，通过编写简单的接口和插入注解，就可以定义好HTTP请求的参数、格式、地址等信息。

而Feign则会完全代理HTTP请求，我们只需要像调用方法一样调用它就可以完成服务请求及相关处理。Spring Cloud对Feign进行了封装，使其支持 SpringMVC 标准注解和 HttpMessageConverters。

## 服务端

### 创建应用

创建一个命名为： `spring-cloud-feign-server-example` 的 Spring cloud 应用。

```xml
    <dependencies>
        <!-- nacos -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
```

### 配置

在 `application.yml`中配置 

````properties
server:
  port: 8080
spring:
  application:
    name: feign-server
  cloud:
    nacos:
      server-addr: 127.0.0.1:8848
````

### 控制类实现

```java
@RestController
@RequestMapping("/feign")
public class FeignServerController {

    @GetMapping("/say/{name}")
    public String sayHello(@PathVariable("name") String name){
        return name + " say hello";
    }
}

```

## 客户端

### 创建应用

创建一个命名为： `spring-cloud-feign-basic-example` 的 Spring cloud 应用。

```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- nacos -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <!-- feign-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

    </dependencies>
   
```

### 配置

在 `application.yml`中配置 

````properties
server:
  port: 8081
spring:
  application:
    name: feign-basic
  cloud:
    nacos:
      server-addr: 127.0.0.1:8848
````

### 启动类

```java
@SpringBootApplication
@EnableFeignClients
public class SpringCloudFeignBasicExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudFeignBasicExampleApplication.class, args);
    }

}

```

- `@EnableFeignClients`: 启用`feign`客户端

### Feign 接口配置

```java
@FeignClient(name="feign-server",path = "feign")
public interface FeignServerClient {
    /**
     * 请求服务端接口 http://feign-server/feign/say/{name}
     * @param name
     * @return
     */
    @GetMapping("/say/{name}")
    String sayHello(@PathVariable("name") String name);
}

```

- `@FeignClient`: `name`指微服务名称，`path`请求的统一路径前缀。 

### 控制类

```java
@RestController
@RequestMapping("/basic")
public class BasicController {

    @Autowired
    private FeignServerClient feignServerClient;

    @GetMapping("/say/{name}")
    public String sayHello(@PathVariable("name") String name){
        return feignServerClient.sayHello(name);
    }
}
```

### 启动程序

分别服务端和客户端，访问客户端 http://localhost:8081/basic/say/mtcarpenter，返回结果如下：

```json
mtcarpenter say hello
```

### Feign的组成

| 接口                 | 作用                                   | 默认值                                                       |
| -------------------- | -------------------------------------- | ------------------------------------------------------------ |
| `Feign.Builder`      | Feign的入口                            | `Feign.Builder`                                              |
| `Client`             | Feign底层用什么去请求                  | **和Ribbon配合时：**`LoadBalancerFeignClient`  **不和Ribbon配合时：**`Fgien.Client.Default` |
| `Contract`           | 契约，注解支持                         | `SpringMVCContract`                                          |
| `Encoder`            | 解码器，用于将对象转换成HTTP请求消息体 | `SpringEncoder`                                              |
| `Decoder`            | 编码器，将响应消息体转成对象           | `ResponseEntityDecoder`                                      |
| `Logger`             | 日志管理器                             | `Slf4jLogger`                                                |
| `RequestInterceptor` | 用于为每个请求添加通用逻辑             | 无                                                           |

### Feign的日记级别

| 日志级别     | 打印内容                                               |
| ------------ | ------------------------------------------------------ |
| NONE（默认） | 不记录任何日志                                         |
| BASIC        | 仅记录请求方法，URL，响应状态代码以及执行时间          |
| HEADERS      | 记录BASIC级别的基础上，记录请求和响应的header （生产） |
| FULL         | 记录请求和响应的header、body 和元数据                  |

### Feign 日志级别代码实现

**日志级别**

```java
//如果使用添加了 @Configuration  需要将此配置类移到启动类之外的包（避免@ComponentScan） 避免父子上下文重复扫描
public class FeignServerConfig {

    @Bean
    public Logger.Level logger(){
        // 请求的详细信息
        return Logger.Level.FULL;
    }
}

```

**@FeignClient 配置**

```jav
@FeignClient(name="feign-server",path = "feign",configuration = FeignServerConfig.class)
```

**application.yml 配置**

```yaml
logging:
  level:
    com.mtcarpenter.spring.cloud.feign.basic.example.feign.FeignServerClient: debug
```

> debug 级别以下配置才会生效

### Feign 日志级别属性配置

```yml
logging:
  level:
    com.mtcarpenter.spring.cloud.feign.basic.example.feign.FeignServerClient: debug
# feign 日志级别属性配置
feign:
  client:
    config:
      # 微服务实例名称
      feign-server:
        loggerLevel: FULL

```

### Feign 日志级别全局属性配置

```java
logging:
  level:
    com.mtcarpenter.spring.cloud.feign.basic.example.feign.FeignServerClient: debug
# feign 日志级别属性配置
feign:
  client:
    config:
      # 全局配置
      default:
        loggerLevel: FULL

```

### Feign支持的配置项

**代码方式**

| 配置项            | 作用                                              |
| ----------------- | ------------------------------------------------- |
| `Logger.Level`    | 指定日志级别                                      |
| `Retryer`         | 指定重试策略                                      |
| `ErrorDecoder`    | 指定错误解码器                                    |
| `Request.Options` | 超时时间                                          |
| `Collection`      | 拦截器                                            |
| `SetterFactory`   | 用于设置Hystrix的配置属性，Fgien整合Hystrix才会用 |

**配置属性**

```yaml
feign:
  client:
    config:
      # 微服务名称
      feignName:
        connectTimeout: 5000  # 相当于Request.Optionsn 连接超时时间
        readTimeout: 5000     # 相当于Request.Options 读取超时时间
        loggerLevel: full     # 配置Feign的日志级别，相当于代码配置方式中的Logger
        errorDecoder: com.example.SimpleErrorDecoder  # Feign的错误解码器，相当于代码配置方式中的ErrorDecoder
        retryer: com.example.SimpleRetryer  # 配置重试，相当于代码配置方式中的Retryer
        requestInterceptors: # 配置拦截器，相当于代码配置方式中的RequestInterceptor
          - com.example.FooRequestInterceptor
          - com.example.BarRequestInterceptor
        # 是否对404错误解码
        decode404: false
        encode: com.example.SimpleEncoder
        decoder: com.example.SimpleDecoder
        contract: com.example.SimpleContract
```

### Ribbon配置 VS Feign配置

| 粒度         | Ribbon                                                       | Feign                                                        |
| ------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 代码局部     | X` @RibbonClient(configuration=X.class)`，`RibbonConfig`类必须加`@Configuration`,且必须放在父上下文无法扫到的包下 | `@FeignClient(configuration=X.class)`，`FeignConfig`类的`@Configuration`可以X不加（可选），如果有，必须放在父上下文无法扫到的包下 |
| 代码全局     | `@RibbonClients(defaultConfigurtion)`                        | `@EnableFeignClients(defaultConfiguration )`                 |
| 配置属性局部 | <clientName>.ribbon.NFLoadBalancerClassName ...              | feign.client.config.<client>.loggerLevel  ...                |
| 配置属性全局 | 无                                                           | feign.client.config.default.loggerLevel                      |

### Feign 代码方式 VS 配置属性方式

| 配置方式 | 有点                                                         | 缺点                                                         |
| -------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 代码配置 | 基于代码，更加灵活                                           | 如果Feign的配置类加了`@Configuration`注解，需注意父子上下文，线上修改需要重打包，发布 |
| 属性配置 | 易上手  配置更加直观  线上修改无需重新打包，发布  **优先级更高** | 极端场景下没有代码配置更加灵活                               |

> 优先级：细粒度属性配置 > 细粒度代码配置 > 全局属性配置 > 全局代码配置

### Fegin 请求具体地址

```java
@FeignClient(name = "url-client",url = "https://blog.lixc.top/spring-cloud.html")
public interface FeignUrlClient {

    /**
     * 地址请求
     * @return
     */
    @GetMapping("")
    String cloud();
}

```

- `url`: 请求的具体地址

## 性能优化

**引入依赖**

```XML
        <!-- okhttp -->
        <dependency>
            <groupId>io.github.openfeign</groupId>
            <artifactId>feign-okhttp</artifactId>
        </dependency>

```

**使用 okhttp **

```yaml
# feign 日志级别属性配置
feign:
  client:
    config:
      # 微服务实例名称
      feign-server:
        loggerLevel: FULL
  # 启用 okhttp 关闭默认 httpclient
  httpclient:
    enabled: false #关闭httpclient
    # 配置连接池
    max-connections: 200 #feign的最大连接数
    max-connections-per-route: 50 #fegin单个路径的最大连接数
  okhttp:
    enabled: true
  # 请求与响应的压缩以提高通信效率
  compression:
    request:
      enabled: true
      min-request-size: 2048
      mime-types: text/xml,application/xml,application/json
    response:
      enabled: true

```

**参数配置**

```java
@Configuration
@ConditionalOnClass(Feign.class)
@AutoConfigureBefore(FeignAutoConfiguration.class) //SpringBoot自动配置
public class OkHttpConfig {

    /**
     * 中文乱码
     * @return
     */
    @Bean
    public Encoder encoder() {
        return new FormEncoder();
    }

    /**
     * 配置 okhttp 与连接池
     * ConnectionPool 默认创建5个线程，保持5分钟长连接
     */
    @Bean
    public okhttp3.OkHttpClient okHttpClient() {
        return new okhttp3.OkHttpClient.Builder()
                //设置连接超时
                .connectTimeout(10, TimeUnit.SECONDS)
                //设置读超时
                .readTimeout(10, TimeUnit.SECONDS)
                //设置写超时
                .writeTimeout(10, TimeUnit.SECONDS)
                //是否自动重连
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(10, 5L, TimeUnit.MINUTES))
                .build();
    }
}
```

## 文章参考

- *https://cloud.spring.io/spring-cloud-openfeign/2.1.x/single/spring-cloud-openfeign.html*

## 代码示例

本文示例代码访问下面查看仓库：

- *Github：* [https://github.com/mtcarpenter/spring-cloud-learning](https://github.com/mtcarpenter/spring-cloud-learning)
- *Gitee：* [https://gitee.com/mtcarpenter/spring-cloud-learning](https://gitee.com/mtcarpenter/spring-cloud-learning)

其中，本文示例代码名称： 

- `spring-cloud-feign-server-example`： Spring Cloud Feign 服务端 
- `spring-cloud-feign-basic-example`:   Spring Cloud Feign 客户端