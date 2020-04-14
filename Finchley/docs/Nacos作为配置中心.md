# Nacos 作为配置中心



##  入门

### 1、创建应用

创建一个命名为： `nacos-cloud-config-example` 的 Spring Boot 应用，

### 2、添加依赖

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.13.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.mtcarpenter</groupId>
    <artifactId>nacos-could-config-example</artifactId>
    <version>1.0.0</version>
    <name>nacos-config</name>

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

        <!-- nacos config -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
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

在 `bootstrap.properties` 中配置 Nacos server 的地址：

````properties
spring.application.name=nacos-config
server.port=8090
spring.cloud.nacos.config.server-addr=192.168.0.145:8848
````

- `port`:项目端口
- `spring.application.name`:服务名称
- `spring.cloud.nacos.discovery.server-addr`: nacos 服务器的地址

> 说明：之所以需要配置 `spring.application.name` ，是因为它是构成 Nacos 配置管理 `dataId`字段的一部分。默认采用 `spring.application.name.properties ` 

### 4、加注解

在启动类上加入注解，这里暂无注解。

### 5、控制类

```java
@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

    @Value("${mtcarpenter.title:none}")
    private String mtcarpenter;


    @RequestMapping("/get")
    public String get() {
        return mtcarpenter;
    }


}
```

- ` @Value`:获取`properties`中的属性
- `@RefreshScope`:动态刷新服务器上的配置。

### 6 、nacos config 配置管理

访问 `nacos server`地址： http://127.0.0.1:8848/nacos/，进入配置管理。

**创建配置**

![75483604-0b28-4c3a-ae12-d488555f5c45.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/75483604-0b28-4c3a-ae12-d488555f5c45.png)

其中：

-  `Data ID`：填入`nacos-config.properties`，与`spring.application.name`名字保持一致。  

- `Group`： 暂不做修改默认值`DEFAULT_GROUP`

- `配置格式`：选择`Properties`

- `配置内容`：应用要加载的配置内容，这里仅作为示例，做简单配置

  ```properties
  mtcarpenter.title=nacos-config
  ```

### 7、启动项目

启动完成之后，访问 http://localhsot:8090/config/get ，返回如下：

```sh
nacos-config
```

在前面控制台加入了`@RefreshScope`,可以动态刷新配置。

- `配置内容`：应用要加载的配置内容，这里仅作为示例，做简单配置

  ```properties
  mtcarpenter.title=nacos-config-title
  ```

输出如下：

```sh
nacos-config-title
```

## 进阶

在 Nacos Spring Cloud 中，`dataId` 的完整格式如下：

```plain
${prefix}-${spring.profile.active}.${file-extension}
```

- `prefix` 默认为 `spring.application.name` 的值，也可以通过配置项 `spring.cloud.nacos.config.prefix`来配置。
- `spring.profile.active` 即为当前环境对应的 profile，详情可以参考 [Spring Boot文档](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html#boot-features-profiles)。 **注意：当 `spring.profile.active` 为空时，对应的连接符 `-` 也将不存在，dataId 的拼接格式变成 `${prefix}.${file-extension}`**
- `file-exetension` 为配置内容的数据格式，可以通过配置项 `spring.cloud.nacos.config.file-extension` 来配置。目前只支持 `properties` 和 `yaml` 类型。

### 1、自定义配置

在实际中项目中，我们的需求 `data-id`有格式要求，和`spring.application.name`并不一致，数据格式为`yaml`, 配置如下：

```properties
spring.application.name=nacos-config
server.port=8090
spring.cloud.nacos.config.server-addr=192.168.0.145:8848

# 自定义 name ，file-extension ：默认 properties 可以修改为 yaml
spring.cloud.nacos.config.prefix=nacos-config
spring.cloud.nacos.config.file-extension=yaml
```

对应`data-id`:`nacos-config.yaml`，`nacos server`服务端新增如下：

![b2f1e46f-a323-86a2-e18f-119f6a32a268.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/b2f1e46f-a323-86a2-e18f-119f6a32a268.png)



### 2、多环境

实际开发中一般环境有`test`、`dev`、`prod` 等。

```properties
spring.application.name=nacos-config
server.port=8090
spring.cloud.nacos.config.server-addr=192.168.0.145:8848

# 自定义 name ，file-extension ：默认 properties 可以修改为 yaml
spring.cloud.nacos.config.prefix=nacos-config
spring.cloud.nacos.config.file-extension=yaml
# 环境 dev test prod
spring.profiles.active=dev
```
启动查找`data-id`环境如下：
```properties

# ${prefix}-${spring.profile.active}.${file-extension}
nacos-config-dev.yaml
```

### 3、服务端创建多个命名空间

- **创建命名 ：dev**

![7c019e22-e8f0-3648-7f40-b828a28de837.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/7c019e22-e8f0-3648-7f40-b828a28de837.png)

- 进入配置列表：切换 `dev` ,创建配置

![19088c4c-978a-d2f0-0770-bb04cfcd98e6.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/19088c4c-978a-d2f0-0770-bb04cfcd98e6.png)

- 修改在启动类增加

```properties
spring.application.name=nacos-config
server.port=8090
spring.cloud.nacos.config.server-addr=192.168.0.145:8848

spring.cloud.nacos.config.namespace=67f66b31-d97a-403f-988e-d56aebb4ccd8
```

- `namespace`: 创建命名空间`dev`自动生成的。

### 4、加载多个配置

```properties
spring.cloud.nacos.config.ext-config[0].data-id=common.properties
spring.cloud.nacos.config.ext-config[0].group=DEFAULT_GROUP
spring.cloud.nacos.config.ext-config[0].refresh=true
spring.cloud.nacos.config.ext-config[1].data-id=database.properties
```

- `ext-config`:数组，目前有 `dataId`、`group`、`refresh`。	

### 5、加载多个共享配置

在实际开发中，数据库，日志配置文件大部分相同在实际开发中，就可以放在一个公共的文件进行引用，如下：

```properties
spring.cloud.nacos.config.shared-dataids=common.properties,base-common.properties
spring.cloud.nacos.config.refreshable-dataids=common.properties,base-common.properties

```

## 代码示例

本文示例代码访问下面查看仓库：

- *Github：* https://github.com/mtcarpenter/spring-cloud-learning

其中，本文示例代码名称：

- `nacos-cloud-config-example`：nacos 配置中心