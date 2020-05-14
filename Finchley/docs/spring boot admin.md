## Gateway 端点监控(actuator)

## 概述

Spring Boot Admin是一个开源社区项目，用于管理和监控SpringBoot应用程序。 应用程序作为Spring Boot Admin Client向为Spring Boot Admin Server注册（通过HTTP）或使用SpringCloud注册中心（例如Eureka，Consul,nacos）发现。 UI是的AngularJs应用程序，展示Spring Boot Admin Client的Actuator端点上的一些监控。

**常见的功能或者监控如下：**

- 显示健康状况
- 显示详细信息，例如
  - JVM和内存指标
  - micrometer.io 指标
  - 数据源指标
  - 缓存指标

- 显示构建信息编号
- 关注并下载日志文件
- 查看jvm系统和环境属性
- 查看Spring Boot配置属性   
- 支持Spring Cloud的postable / env-和/ refresh-endpoint
- 轻松的日志级管理
- 与JMX-beans交互
- 查看线程转储
- 查看http跟踪
- 查看auditevents
- 查看http-endpoints
- 查看计划任务
- 查看和删除活动会话（使用spring-session）
- 查看Flyway / Liquibase数据库迁移
- 下载heapdump
- 状态变更通知（通过电子邮件，Slack，Hipchat，……）
-   状态更改的事件日志（非持久性）
  

## 服务端

### 创建应用

创建一个命名为： `spring-boot-admin-server-example` 的 Spring cloud 应用。

```xml
   <properties>
        <java.version>1.8</java.version>
        <spring.cloud.alibaba.version>2.1.2.RELEASE</spring.cloud.alibaba.version>
        <spring.cloud.version>Greenwich.RELEASE</spring.cloud.version>
        <spring.boot.admin.version>2.1.5</spring.boot.admin.version>
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
        <!-- spring boot admin server -->
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-starter-server</artifactId>
            <version>${spring.boot.admin.version}</version>
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
  port: 8095
spring:
  application:
    name: admin-server
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
````

### 启动类注解

```java
@SpringBootApplication
@EnableAdminServer
public class SpringBootAdminServerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAdminServerExampleApplication.class, args);
    }

}

```

Admin server 端 ，启动需要 添加注解`@EnableAdminServer`

## 客户端

### 创建应用

创建一个命名为： `spring-boot-admin-client-example` 的 Spring cloud 应用。

```xml
 <properties>
        <java.version>1.8</java.version>
        <spring.cloud.alibaba.version>2.1.2.RELEASE</spring.cloud.alibaba.version>
        <spring.cloud.version>Greenwich.RELEASE</spring.cloud.version>
        <spring.boot.admin.version>2.1.5</spring.boot.admin.version>
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
        <!--端点监控-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <!-- nacos -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <!-- spring boot admin client -->
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-starter-client</artifactId>
            <version>${spring.boot.admin.version}</version>
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
  port: 8096
spring:
  application:
    name: admin-client
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
  boot:
    admin:
      client:
        # admin 服务端的地址
        url: http://localhost:8095

management:
  endpoints:
    web:
      exposure:
        include: '*'
  endpoint:
    health:
      # 展示详情
      show-details: always
````

### 启动程序

分别启动程序服务端(8095)和客户端(8096)，访问地址：http://localhost:8095 ，出现如下界面

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/83f40d92-9110-f99b-f5b2-5f398e91feb3.png)

进入客户端`admin-client`，在菜单栏 Details 有线程、内存等显示

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/08aaf03e-bf0f-ac15-912b-d1e0574439e7.png)



## 服务端整合 security

###  依赖

需要在  admin-server 工程的文件引入以下的依赖：

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

```

### 配置

在 `application.yml`中配置 security 账号和密码

```yaml
  security:
    user:
      name: admin
      password: admin
```

### 配置类

权限配置类修改

```java
@Configuration
public class SecuritySecureConfig extends WebSecurityConfigurerAdapter {

    private final String adminContextPath;

    public SecuritySecureConfig(AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter( "redirectTo" );

        http.authorizeRequests()
                // 配置签名后放行路径
                .antMatchers( adminContextPath + "/assets/**" ).permitAll()
                .antMatchers( adminContextPath + "/login" ).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage( adminContextPath + "/login" ).successHandler( successHandler ).and()
                .logout().logoutUrl( adminContextPath + "/logout" ).and()
                .httpBasic().and()
                .csrf().disable();
    }
}
```

### 重启测试

请求 http://192.168.2.105:8095，便会来到登录界面

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/8b50a4e7-329e-3341-295d-3fb3378bd7d4.png)



## 文章参考

- *https://github.com/codecentric/spring-boot-admin*
- *https://segmentfault.com/a/1190000017816452*

## 代码示例

本文示例代码访问下面查看仓库：

- *Github：* [https://github.com/mtcarpenter/spring-cloud-learning](https://github.com/mtcarpenter/spring-cloud-learning)
- *Gitee：* [https://gitee.com/mtcarpenter/spring-cloud-learning](https://gitee.com/mtcarpenter/spring-cloud-learning)

其中，本文示例代码名称： 

- `spring-boot-admin-server-example`：spring boot admin 服务端
- `spring-boot-admin-client-example`:  spring boot admin 客户端