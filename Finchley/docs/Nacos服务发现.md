# Nacos 服务发现

##  服务提供者

### 1、创建应用

创建一个命名为： `nacos-cloud-discovery-provider-example` 的 Spring Boot 应用，

### 2、添加依赖

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.13.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <properties>
        <java.version>1.8</java.version>
        <alibaba.version>2.1.0.RELEASE</alibaba.version>
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
        <!--nacos 服务发现-->
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
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

```

- `dependencyManagement`: 主要用于主版本管理,后续所有子项目使用的依赖项为同一版本，无需在指定依赖。

**注意**：版本 [2.1.x.RELEASE](https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-starter-alibaba-nacos-discovery) 对应的是 Spring Boot 2.1.x 版本。版本 [2.0.x.RELEASE](https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-starter-alibaba-nacos-discovery) 对应的是 Spring Boot 2.0.x 版本，版本 [1.5.x.RELEASE](https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-starter-alibaba-nacos-discovery) 对应的是 Spring Boot 1.5.x 版本。

更多版本对应关系参考：[版本说明 Wiki](https://github.com/spring-cloud-incubator/spring-cloud-alibaba/wiki/版本说明)

### 3、增加配置

在 `application.properties` 中配置 Nacos server 的地址：

````properties
server.port=8081
spring.application.name=nacos-provider
spring.cloud.nacos.discovery.server-addr=192.168.0.145:8848
````

- `port`:项目端口（8081、8082、8083 用于服务提供者）
- `spring.application.name`:服务名称
- `spring.cloud.nacos.discovery.server-addr`: nacos 服务器的地址

### 4、加注解

在启动类上加入注解。

```java
@SpringBootApplication
@EnableDiscoveryClient
public class NacosProviderApplication {

    @Value("${server.port}")
    private String port;

    public static void main(String[] args) {
        SpringApplication.run(NacosProviderApplication.class, args);
    }


    @RestController
    class EchoController {
        @GetMapping(value = "/echo/{name}")
        public String echo(@PathVariable String name) {
            return port+": Hello Nacos Discovery " + name;
        }
    }

}
```

- `@EnableDiscoveryClient`:开启 Spring Cloud 的服务注册与发现。

### 5、启动项目

启动完成之后，访问 http://127.0.0.1:8848/nacos/  进入控制台查看服务列表，如下：

![5bc74a69-4ac2-3da5-a67d-4e69805bda4f.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/5bc74a69-4ac2-3da5-a67d-4e69805bda4f.png)

同一个服务使用不同的端口（8081、8083）启动，进入服务列表详情查看：

![29beb538-0124-3cb1-c5b3-ccf98b3182b3.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/29beb538-0124-3cb1-c5b3-ccf98b3182b3.png)

## 服务消费者 RestTemplate

### 1、创建应用

创建一个命名为： `nacos-cloud-discovery-consumer-example` 的 Spring Boot 应用，

### 2、添加依赖

依赖于 `nacos-cloud-discovery-provider-example` 保持一致。

### 3、增加配置

```properties
server.port=8085
spring.application.name=nacos-consumer
spring.cloud.nacos.discovery.server-addr=192.168.0.145:8848
```

### 4、加注解

```java
@SpringBootApplication
@EnableDiscoveryClient
public class NacosConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication.class, args);
    }

}
```

### 5、配置 RestTemplate

```java
@Configuration
public class RestTemplateConfig {


    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

- `@Configuration`:用于定义配置类
- `@Bean`:产生一个Bean对象，然后这个Bean对象交给Spring管理。产生这个Bean对象的方法Spring只会调用一次，随后这个Spring将会将这个Bean对象放在自己的 IOC 容器中。
- `@LoadBalanced`:增加客户端负载均衡功能。

### 6、控制器实现

```JAVA
@RestController
public class RestTemplateController {

    @Autowired
    private  RestTemplate restTemplate;


    @GetMapping(value = "/echo/{name}")
    public String echo(@PathVariable String name) {
        // 原始方式通过 ip:port 进行访问
        // restTemplate.getForObject("http://localhost:8081/msg"+name,String.class);

        //  通过 spring.application.name 名称发现
        return restTemplate.getForObject("http://nacos-provider/echo/" + name, String.class);
    }

}
```

- `nacos-provider`:表示服务提供者的`application.name`

### 7、启动项目

启动完成之后，访问 http://127.0.0.1:8848/nacos/  进入控制台查看服务列表 `nacos-consumer`是否注册，如下：

![301b9991-d537-aa4a-37b5-37834c8440bf.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/301b9991-d537-aa4a-37b5-37834c8440bf.png)

请求消费者 http://localhost:8085/echo/name ,会轮询请求健康的提供者,输出如下。

```sh
8081: Hello Nacos Discovery name
8082: Hello Nacos Discovery name
8083: Hello Nacos Discovery name
```

## 服务消费者 Feign

### 1、创建应用

创建一个命名为： `nacos-cloud-discovery-consumer-feign-example` 的 Spring Boot 应用，

### 2、添加依赖

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.13.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
   
    <properties>
        <java.version>1.8</java.version>
        <alibaba.version>2.1.0.RELEASE</alibaba.version>
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
        <!--nacos 注册服务-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <!--openfeign-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
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
                <version>${alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

        </dependencies>
    </dependencyManagement>

```

- `openfeign`: 在 `spring-cloud-dependencies`下，所以需要引入主版本。其他与之前服务提供者保持一致。

### 3、增加配置

```properties
server.port=8086
spring.application.name=nacos-consumer-feign
spring.cloud.nacos.discovery.server-addr=192.168.0.145:8848
```

### 4、加注解

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class NaocsConsumerFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(NaocsConsumerFeignApplication.class, args);
    }

}
```

- `@EnableFeignClients`: 启用`feign`客户端。

### 5、fegin 接口实现

```java
@FeignClient(name = "nacos-provider")
public interface Feign {
    /**
     * 请求服务提供方的 接口
     * @param name
     * @return
     */
    @GetMapping("/echo/{name}")
    String echo(@PathVariable String name);
}
```

- `@FeignClient(name = "nacos-provider")`:  `fegin` 的请求的服务提供方。

### 6、控制器

```java
@RestController
public class FeignClientController {

    @Autowired
    private Feign feign;

    @GetMapping(value = "/echo/{name}")
    public String echo(@PathVariable String name) {
        return feign.echo(name);
    }
}
```

### 7、启动项目

启动完成之后，访问 http://127.0.0.1:8848/nacos/   进入控制台查看服务列表 `nacos-consumer-feign`是否注册。

客户端请求消费者 http://localhost:8086/echo/name  ,会轮询请求健康的提供者,输出如下：

```sh
8081: Hello Nacos Discovery name
8082: Hello Nacos Discovery name
8083: Hello Nacos Discovery name
```

## 扩展

`主流微服务注册中心产品比较 Eureka、Consul、Nacos` ： https://developer.aliyun.com/article/738413?spm=a2c6h.13262185.0.0.17b8b8f5thwwcF

## 代码示例

本文示例代码访问下面查看仓库：

- *Github：* https://github.com/mtcarpenter/spring-cloud-learning

其中，本文的几种示例可查看下面的几个项目：

- `nacos-cloud-discovery-provider-example`：服务提供者，必须启动
- `nacos-cloud-discovery-consumer-example`：使用 RestTemplate 消费
- `nacos-cloud-discovery-consumer-feign-example`：使用 Feign 消费