# Sentinel  @SentinelResource 注解使用

## 应用环境搭建

### 1、创建应用

创建一个命名为： `sentinel-cloud-annotation-example` 的 Spring cloud 应用，

### 2、添加依赖

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.13.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.mtcarpenter</groupId>
    <artifactId>sentinel-cloud-annotation-example</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>sentinel-cloud</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
        <alibaba.version>2.1.2.RELEASE</alibaba.version>
        <spring.cloud.version>Greenwich.RELEASE</spring.cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
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

### 3、增加配置

在 `application.properties` 中配置 `sentinel  dashboard`  的地址：

````properties
# 服务名称
spring.application.name=sentinel-annotation-example
# 服务端口
server.port=8081
# sentinel dashboard
spring.cloud.sentinel.transport.dashboard=localhost:8080
````

- `port`:项目端口
- `spring.application.name`:服务名称
- `spring.cloud.sentinel.transport.dashboard`:  sentinel  dashboard 界面地址

### 4、加注解

在启动类上加入注解，这里暂无注解。

### 5、控制类

```java
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
    @Autowired
    private TestService service;

    @GetMapping(value = "/hello")
    @SentinelResource("hello")
    public String hello() {
        return "mtcarpenter:hello";
    }

}
```

- `@SentinelResource` 定义一个资源资源名称 `hello`。 

### 6 、启动程序

访问接口 `http://localhost:8081/test/hello`。

## **流控规则**

### 进入 sentinel  簇点链路

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/2b87c691-99dd-588f-bfec-52a25501e993.png)

进入`sentienl 控制台`，刚访问的接口`test/hello`，还在多出在接口上设置的资源名称`hello`，下面通过配置资源名称进行接口的限流。**流控中的资源名可以是接口路径也可以是资源名称**，新增之后通过页面测试访问测试，测试失败如下：

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/2496fb2f-45b6-8f32-da06-fb17663a6912.png)

在上一个章节接口限流返回是`Blocked by Sentinel (flow limiting)`。自定义资源名称默认是没有处理异常的。进入控制台会有如下错误信息：

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/7b1abd05-c399-f2a9-57d2-6f92ab01c9d4.png)

### 降低异常处理

- 修改控制类如下

```java

    @GetMapping(value = "/hello")
    @SentinelResource(value = "hello",blockHandler = "block")
    public String hello() {
        return "mtcarpenter:hello";
    }

    /**
     * 限流降级
     * @param ex
     * @return
     */
    public String block( BlockException  ex){
        log.warn("服务被限流或者降级了 block",ex.getMessage());
        return "服务被限流或者降级了 block";
    }
```

- `blockHandler`:被限流降级而抛出 `BlockException` 时只会进入 `blockHandler` 处理逻辑。值`block`对类中的方法，`block`除了可以增加一个异常其他和接口参数保持一直，返回类型也需要保持一致。再次访问就是如下结果：

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/2294d526-c1ba-5174-3855-aad335e31d74.png)

## **降级规则**

官方参考：[熔断降级](https://github.com/alibaba/Sentinel/wiki/熔断降级)

### 控制类新增限流

```java
   @GetMapping(value = "/degrade")
    @SentinelResource(value = "degrade",blockHandler = "degradeBlock",fallback = "degradeFallback")
    public String apiHello(@RequestParam(required = false) String name) {
        if (StringUtils.isEmpty(name)){
            throw new IllegalArgumentException("参数为空");
        }
        return "mtcarpenter:"+name;
    }

    public String degradeBlock(String name, BlockException  ex){
        log.warn("服务被限流或者降级了 block",name,ex.getMessage());
        return "服务被限流或者降级了 block";
    }

    /**
     * 限流降级 异常
     * @return
     */
    public String degradeFallback(String name){
        log.warn("服务被限流或者降级了 异常 fallback");
        return "服务被限流或者降级了 异常 fallback";
    }
```

- `fallback`:Fallback 函数，函数签名与原函数一致或加一个 Throwable 类型的参数。

### 降级规则配置

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/4df33e12-3faf-70dd-652b-a467e4201bc9.png)

配置 `异常比例`为 1 ，时间窗口为 1，表示在一秒请求量的 10% 将被降级。官方解释：超过当资源的每秒请求量 >= N（可配置），并且每秒异常总数占通过量的比值超过阈值（`DegradeRule` 中的 `count`）之后，资源进入降级状态。

没有传递参数异常触发如下：

![eaeb25e2-694e-9260-e9d8-998c42d5c170.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/eaeb25e2-694e-9260-e9d8-998c42d5c170.png)

## **热点参数限流规则**

**流控规则**和**降级规则**，异常处理在同一个类方法中，后期增加维护成本，通过对热点的测试，进一步优化`降级`和`限流`。

### 控制类

```java

    @GetMapping(value = "/hot")
    @SentinelResource(
            value = "hot",
            blockHandler = "block",
            blockHandlerClass = BlockHandlerClassException.class,
            fallback = "fallback",
            fallbackClass = FallbackClassException.class)
    public String hot(@RequestParam("productId") String productId, @RequestParam("key") String key) {
        return productId + "---" + key;
    }
```

- `blockHandlerClass`:将`blockHandler`的`block`方法，独立出去以便统一维护。
- `fallbackClass`:将`fallback`的`fallback`方法，同样独立出去。

### 异常类

```java
@Slf4j
public class FallbackClassException {

    /**
     * 限流降级 异常
     * @return
     */
    public static String fallback(String productId,  String key,Throwable ex){
        log.warn("热点数据被限流或者降级了",ex);
        return productId+"：热点数据被限流或者降级了";
    }
}

@Slf4j
public class BlockHandlerClassException {
    /**
     * 降级和限流
     * @param productId
     * @param key
     * @param ex
     * @return
     */
    public static String block(String productId,  String key, BlockException ex){
        log.warn("productId={}：热点数据被限流或者降级了 err={}",productId,ex.getMessage());
        return productId+"：热点数据被限流或者降级了";
    }
}
```

`blockHandler `和`fallback` 只能访问定义的静态(`static`)方法。

### 热点限流如下

 ![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/c9765463-84f6-f098-14ba-4d64cbd2bbad.png)

热点配置算是流控规则的一个延申，参数索引从 0 开始，控制类接口距离，这里的 0 代表 `productId`,参数额外项，参数值代表 `productId` 的值为 1 的时候限流阀值为 10000 被限流，其他单机阀值为 1 ，将被限流。

`productId `为 2 在窗口时长超过阀值 1，限流： 

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/a92c8a6e-bb9a-b410-6fc2-d86db952d335.png)



## @SentinelResource 注解方法参数说明

> 注意：注解方式埋点不支持 private 方法。

`@SentinelResource` 用于定义资源，并提供可选的异常处理和 fallback 配置项。 `@SentinelResource` 注解包含以下属性：

- `value`：资源名称，必需项（不能为空）

- `entryType`：entry 类型，可选项（默认为 `EntryType.OUT`）

- `blockHandler` / `blockHandlerClass`: `blockHandler `对应处理 `BlockException` 的函数名称，可选项。blockHandler 函数访问范围需要是 `public`，返回类型需要与原方法相匹配，参数类型需要和原方法相匹配并且最后加一个额外的参数，类型为 `BlockException`。blockHandler 函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 `blockHandlerClass` 为对应的类的 `Class` 对象，注意对应的函数必需为 static 函数，否则无法解析。

- `fallback`：fallback 函数名称，可选项，用于在抛出异常的时候提供 fallback 处理逻辑。fallback 函数可以针对所有类型的异常（除了`exceptionsToIgnore`里面排除掉的异常类型）进行处理。fallback 函数签名和位置要求：

  - 返回值类型必须与原函数返回值类型一致；
  - 方法参数列表需要和原函数一致，或者可以额外多一个 `Throwable` 类型的参数用于接收对应的异常。
  - fallback 函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 `fallbackClass` 为对应的类的 `Class` 对象，注意对应的函数必需为 static 函数，否则无法解析。

- `defaultFallback`（since 1.6.0）：默认的 fallback 函数名称，可选项，通常用于通用的 fallback 逻辑（即可以用于很多服务或方法）。默认 fallback 函数可以针对所有类型的异常（除了`exceptionsToIgnore` 

  里面排除掉的异常类型）进行处理。若同时配置了 fallback 和 defaultFallback，则只有 fallback 会生效。defaultFallback 函数签名要求：

  - 返回值类型必须与原函数返回值类型一致；
  - 方法参数列表需要为空，或者可以额外多一个 `Throwable` 类型的参数用于接收对应的异常。
  - defaultFallback 函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 `fallbackClass` 为对应的类的 `Class` 对象，注意对应的函数必需为 static 函数，否则无法解析。

- `exceptionsToIgnore`（since 1.6.0）：用于指定哪些异常被排除掉，不会计入异常统计中，也不会进入 fallback 逻辑中，而是会原样抛出。

> 注：1.6.0 之前的版本 fallback 函数只针对降级异常（`DegradeException`）进行处理，**不能针对业务异常进行处理**。

特别地，若 blockHandler 和 fallback 都进行了配置，则被限流降级而抛出 `BlockException` 时只会进入 `blockHandler` 处理逻辑。若未配置 `blockHandler`、`fallback` 和 `defaultFallback`，则被限流降级时会将 `BlockException` **直接抛出**（若方法本身未定义 throws BlockException 则会被 JVM 包装一层 `UndeclaredThrowableException`）。




## 总结

本章介绍了**流控规则**、**降级规则**、**热点参数**，如下链接直达官方：

- *注解支持:* [https://github.com/alibaba/Sentinel/wiki/注解支持](https://github.com/alibaba/Sentinel/wiki/注解支持)
- *流量控制:*[https://github.com/alibaba/Sentinel/wiki/流量控制](https://github.com/alibaba/Sentinel/wiki/流量控制)
- *熔断降级:*[https://github.com/alibaba/Sentinel/wiki/熔断降级](https://github.com/alibaba/Sentinel/wiki/熔断降级)
- *热点参数限流:*[https://github.com/alibaba/Sentinel/wiki/热点参数限流](https://github.com/alibaba/Sentinel/wiki/热点参数限流)

## 文章参考

- *https://github.com/alibaba/Sentinel*

## 代码示例

本文示例代码访问下面查看仓库：

- *Github：* [ https://github.com/mtcarpenter/spring-cloud-learning]( https://github.com/mtcarpenter/spring-cloud-learning)

其中，本文示例代码名称：

- `sentinel-cloud-annotation-example`：@SentinelResource 注解使用