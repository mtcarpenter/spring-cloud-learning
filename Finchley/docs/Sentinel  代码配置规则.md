# Sentinel  代码配置规则

## 应用环境搭建

### 1、创建应用

创建一个命名为： `sentinel-cloud-code-example` 的 Spring cloud 应用，

### 2、添加依赖

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.13.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.mtcarpenter</groupId>
    <artifactId>sentinel-cloud-code-example</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>sentinel-cloud-code</name>
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
            <optional>true</optional>
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

    @GetMapping(value = "/initFlowRules")
    public String init() {
         // 流控规则 初始化
         initFlowRules();
        return "mtcarpenter:init";
    }

    /**
     * 流控规则初始化
     */
    private static void initFlowRules(){
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("/test/hello");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // Set limit QPS to 20.
        rule.setCount(1);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

    @GetMapping(value = "/hello")
    public String hello(@RequestParam(value = "name",required = false) String name) {
        return "mtcarpenter:"+name;
    }

}
```

在前面的几个章节，我们通过 `sentinel dashboard` 配置规则，本章节通过代码来定义规则。这里通过限流接口`/test/hello`举例。

**initFlowRules **方法说明，一条限流规则主要由下面几个因素组成，我们可以组合这些元素来实现不同的限流效果：

- `resource`：资源名，即限流规则的作用对象
- `count`: 限流阈值
- `grade`: 限流阈值类型（QPS 或并发线程数）
- `limitApp`: 流控针对的调用来源，若为 `default` 则不区分调用来源
- `strategy`: 调用关系限流策略
- `controlBehavior`: 流量控制效果（直接拒绝、Warm Up、匀速排队）

### 6 、启动程序

- 访问接口 `http://localhost:8081/test/initFlowRules`配置规则，进入 `sentinel dashboard`界面进行查看：

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/f1464e1b-0c57-9b88-c252-6cce0cc00620.png)

- 访问接口 `http://localhost:8081/test/hello?name=hello`,限流如下：

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/71dbfdba-50b5-0987-5f68-fb5e6baf7928.png)

跟我之前手动配置的限流返回结果一样，在**Sentinel  @SentinelResource 注解使用 **我们异常处理，通过代码实现异常的处理，没有使用注解情况下，对异常处理。



本章介绍了**如何规则**、**动态规则扩展**，如下链接直达官方：

- *如何使用:* [https://github.com/alibaba/Sentinel/wiki/如何使用](https://github.com/alibaba/Sentinel/wiki/如何使用)
- *动态规则扩展：*[https://github.com/alibaba/Sentinel/wiki/动态规则扩展](https://github.com/alibaba/Sentinel/wiki/动态规则扩展)

## 文章参考

- *https://github.com/alibaba/Sentinel*

## 代码示例

本文示例代码访问下面查看仓库：

- *Github：* [ https://github.com/mtcarpenter/spring-cloud-learning]( https://github.com/mtcarpenter/spring-cloud-learning)

其中，本文示例代码名称：

- `sentinel-cloud-code-example`：sentinel 代码配置规则