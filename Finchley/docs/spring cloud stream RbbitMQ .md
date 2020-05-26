# Spring Cloud Stream RbbitMQ 

## Spring Cloud Stream：消息驱动框架

​		关于事件驱动架构的实现有两种解决方案：一种是直接使用诸如RabbitMQ、Kafka等消息中间件来实现消息传递，这种解决方案需要开发人员掌握各种工具的特性并采用不同的方式进行系统集成；另一种是提供一个整体的平台型解决方案，从而屏蔽各个消息中间件在技术实现上的差异。显然，我们倾向于后者，Spring Cloud Stream正是这样一种平台型解决方案。本节将首先介绍Spring Cloud Stream的基本架构，并给出它与目前主流的各种消息中间件之间的整合机制。

## Spring Cloud Stream工作流程

​		Spring Cloud Stream中有3种角色，即消息的发布者、消费者以及消息传递系统本身，以消息传递系统为中心，整个工作流程表现为一种对称结构

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/1e0fa6f4-d472-d01b-56fe-2435f2da521b.png)

​		消息发布者根据业务需要产生消息发送的需求，而 Spring Cloud Stream 中的 Source 组件是真正生成消息的组件，然后消息通过 Channel 传送到 Binder ，这里的 Binder 是一个抽象组件，通过 Binder 可以与特定的消息传递系统进行通信。在 Spring Cloud Stream 中，目前已经内置集成的消息传递系统包括 RabbitMQ 和 Kafka。

​		消息消费者同样可以通过Binder从消息传递系统中获取消息，消息通过Channel将流转到Sink组件。这里的Sink组件是服务级别的，即每个微服务可能会实现不同的Sink组件，分别对消息进行业务上的处理。

## Spring Cloud Stream 核心组件

​		Spring Cloud Stream 具备	4 个核心组件，分别是 Binder、Channel、Source和Sink。其中，Binder 和 Channel 成对出现，而 Source 和 Sink 分别面向消息的发布者和消费者。

### Binder

​		Binder是Spring Cloud Stream的一个重要抽象概念，是服务与消息传递系统之间的黏合剂。目前Spring Cloud Stream实现了面向Kafka和RabbitMQ这两种消息中间件的Binder。通过Binder，可以很方便地连接消息中间件，可以动态地改变消息的目标地址、发送方式，而不需要了解其背后的各种消息中间件在实现上的差异。

### Channel

​		Binder是Spring Cloud Stream的一个重要抽象概念，是服务与消息传递系统之间的黏合剂。目前Spring Cloud Stream实现了面向Kafka和RabbitMQ这两种消息中间件的Binder。通过Binder，可以很方便地连接消息中间件，可以动态地改变消息的目标地址、发送方式，而不需要了解其背后的各种消息中间件在实现上的差异。

### Source和Sink

​		在介绍Source和Sink之前，需要明确两个在消息传递系统和企业服务总线领域中经常碰到的概念，即输入（Inbound）和输出（Outbound）。这里输入/输出的参照对象是Spring CloudStream自身，即从Spring Cloud Stream发布消息就是输出，而通过Spring Cloud Stream接收消息就是输入。因此，Source组件用于面向单个输出通道的应用，而Sink组件则用于有单个输入通道的应用。

​	  在Spring Cloud Stream中，Source组件就是使用一个POJO（Plain Old Java Object）对象来作为需要发布的消息，将该对象进行序列化（默认的序列化方式是JSON）后发布到通道中。而Sink组件用于监听通道并等待消息的到来，一旦有可用消息，Sink就会该消息反序列化为一个POJO对象并用于处理业务逻辑。

​	

## RabbitMQ 介绍

​		RabbitMQ 是一个由 Erlang 开发的 AMQP（Advanced Message Queuing Protocol）开源实现。很多人可能并不知道什么是 AMQP。AMQP 是一个提供统一服务的应用层标准高级消息队列协议，是应用层协议的一个开放标准，为面向消息中间件设计。基于此协议的客户端与消息中间件可以传递消息，并不受客户端／中间件不同产品、不同开发语言等条件的限制。RabbitMQ 是由 RabbitMQ Technologies Ltd 开发并且提供商业支持的。该公司在 2010 年 4 月被SpringSource（VMWare 的一个部门）收购。在2013年5月被并入 Pivotal。其实 VMWare、Pivotal 和 EMC 本质上是一家的。不同的是，VMWare 是独立上市子公司，而 Pivotal 整合了 EMC 的某些资源，现在并没有上市。

​		RabbitMQ 支持多种客户端，如 Python、Ruby、.NET、Java、JMS、C、PHP、ActionScript、XMPP、STOMP 等，支持 Ajax。用于分布式系统中存储转发消息，在易用性、扩展性、高可用性等方面都有不错的表现。并且，正如RabbitMQ官网（官网地址：http://www.rabbitmq.com/）介绍的，RabbitMQ 在全球范围内在小型初创公司和大型企业中进行了超过 35 000 次 RabbitMQ 生产部署，是最受欢迎的开源消息代理。RabbitMQ 很轻量级，易于在内部和云中部署。它支持多种消息传递协议。RabbitMQ 可以部署在分布式和联合配置中，以满足高规模、高可用性的要求。

**RabbitMQ有 四个重要概念，分别是：虚拟主机（vritual host），交换机（exchange），队列（queue），和绑定（binding）。**

- Broker：虚拟主机，一个broker里可以开设多个 vhost，用作不同用户的权限分离。
- Virtual Host ：为什么需要多个虚拟主机呢？很简单，RabbitMQ 当中，用户只能在虚拟主机的粒度进行权限控制。 因此，如果需要禁止 A 组访问 B 组的交换机/队列/绑定，必须为 A 和 B 分别创建一个虚拟主机。每一个 RabbitMQ 服务器都有一个默认的虚拟主机“/”。
- Exchange：Exchange 负责将它路由到一个或多个队列中，这个过程会根据 Exchange 的类型、Exchange 和队列之间的 binding 以及消息的 routing key 进行路由。
- Binding：交换机需要和绑定队列
- Quene：队列
- Connection：连接，建立一个tcp连接，使用多路复用方式提升性能，节约资源
- Channel：信道，一个连接可以多路复用打开多个信道，获取队列中的消息

**不同类型的 Exchange，包括以下内容。**

- Default：这是代理创建的特殊 Exchange。它会将消息路由至名字与消息 routing key 相同的队列。所有的队列都会自动绑定至 Default Exchange。
- Direct：如果消息的 routing key 与队列的 binding key 相同，那么消息将会路由到该队列上。
- Topic：如果消息的 routing key 与队列 binding key（可能会包含通配符）匹配，那么消息将会路由到一个或多个这样的队列上。
- Fanout：不管 routing key 和 binding key 是什么，消息都将会路由到所有绑定队列上。
- Headers：与 Topic Exchange 类似，只不过要基于消息的头信息进行路由，而不是 routing key。
- Dead letter：捕获所有无法投递（也就是它们无法匹配所有已定义的 Exchange 和队列的 binding 关系）的消息。

## RabbitMQ 特点

**可靠性**：RabbitMQ 使用一些机制来保证可靠性，如持久化、传输确认、发布确认
**消息集群**：多个 RabbitMQ 服务器可以组成一个集群，形成一个逻辑 Broker
**高可用**：队列可以在集群中的机器上进行镜像，使得在部分节点出问题的情况下队列仍然可用。
**多语言客户端**：RabbitMQ 几乎支持所有常用语言，比如 Java、.NET、Ruby 等
**管理界面**：RabbitMQ 提供了一个易用的用户界面，使得用户可以监控和管理消息 Broker 的许多方面

## Spring Boot使用RabbitMQ

### 创建应用

创建一个命名为： `spring-cloud-stream-rabbitmq-example` 的 Spring boot应用。

```xml
 <properties>
        <java.version>1.8</java.version>
        <spring.cloud.version>Greenwich.RELEASE</spring.cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
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
    name: stream-rabbitmq-example
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
  cloud:
    stream:
      bindings:
        input:
          destination: stream-channel
        output:
          destination: stream-channel
````

### 自定义通道

 stream 内置的简单消息通道（消息通道也就是指消息的来源和去向）接口定义，一个 Source 和 一个 Sink 

```java
public interface StreamBinding {

    String INPUT = "input";

    String OUTPUT = "output";

    @Input(StreamBinding.INPUT)
    SubscribableChannel input();

    @Output(StreamBinding.OUTPUT)
    MessageChannel output();
}
```

### 消息接收器

```java
@Component
@EnableBinding(StreamBinding.class)
@Slf4j
public class StreamReceiver {
    @StreamListener(StreamBinding.INPUT)
    @SendTo(StreamBinding.OUTPUT)
    public int inputMessage(Message message) {
        log.info("StreamReceiver:{}", message);
        return 0;
    }


}
```

### 测试实体类

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book implements Serializable {

    private static final long serialVersionUID = -2647663884508887444L;
    private int id;
    private String name;
    private String author;

}
```

### 启动服务

启动服务

### 运行测试类

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringCloudStreamRabbitmqExampleApplicationTests {

    @Autowired
    private StreamBinding streamBinding;

    @Test
    public void contextLoads() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("book", new Book(3,"百科图书","mtcarpenter"));
        MessageHeaders mhs = new MessageHeaders(properties);
        Message msg = MessageBuilder.createMessage("book order ", mhs);
        streamBinding.output().send(msg);
    }

}
```

### 运行结果

```java
2020-05-26 14:15:16.711  INFO 13952 --- [Uy9j2IbVokTMA-1] c.m.s.rabbitmq.example.StreamReceiver    : StreamReceiver:GenericMessage [payload=0, headers={amqp_receivedDeliveryMode=PERSISTENT, amqp_receivedExchange=stream-channel, amqp_deliveryTag=1101, book=Book(id=3, name=百科图书, author=mtcarpenter), deliveryAttempt=1, amqp_consumerQueue=stream-channel.anonymous.OpxQNKZtTUy9j2IbVokTMA, amqp_redelivered=false, amqp_receivedRoutingKey=stream-channel, amqp_timestamp=Tue May 26 14:15:14 CST 2020, amqp_messageId=b79c34f3-7138-90b6-1dbe-41567cc44460, id=f7e49a85-62c8-b80a-6357-5aa218b0776d, amqp_consumerTag=amq.ctag-5Q0-6e4KA27eNuKdQwiVFQ, contentType=application/json, timestamp=1590473716711}]

```

在主应用控制类控制台会接收如下消息。 

## 文章参考

- *微服务架构实战：基于Spring Boot、Spring Cloud、Docker*

## 代码示例

本文示例代码访问下面查看仓库：

- *Github：* [https://github.com/mtcarpenter/spring-cloud-learning](https://github.com/mtcarpenter/spring-cloud-learning)
- *Gitee：* [https://gitee.com/mtcarpenter/spring-cloud-learning](https://gitee.com/mtcarpenter/spring-cloud-learning)

其中，本文示例代码名称： 

- `spring-cloud-stream-rabbitmq-example`： spring cloud stream rabbitMQ