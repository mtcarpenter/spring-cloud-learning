# spring boot RbbitMq 

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

创建一个命名为： `spring-boot-rabbitmq-example` 的 Spring boot应用。

```xml
      <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.5</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

```

### 配置

在 `application.yml`中配置 

````properties
server:
  port: 8080
spring:
  application:
    name: rabbitmq-example
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /
````

### 配置消息转换器

```java
@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
```

默认情况下，消息转换是通过SimpleMessageConverter来实现的，它能够将简单类型（如String）和Serializable对象转换成Message对象。但是，Spring为RabbitTemplate提供了多个消息转换器，包括下面内容。

- Jackson2JsonMessageConverter：使用Jackson 2 JSON实现对象和JSON的相互转换。
- MarshallingMessageConverter：使用Spring的Marshaller和Unmarshaller进行转换。
- SerializerMessageConverter：使用Spring的Serializer和Deserializer转换String和任意种类的原生对象。
- SimpleMessageConverter：转换String、字节数组和Serializable类型。
- ContentTypeDelegatingMessageConverter：基于contentType头信息，将转换功能委托给另外一个MessageConverter
- MessagingMessageConverter：将消息转换功能委托给另外一个MessageConverter，并将头信息的转换委托给AmqpHeaderConverter。

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

### 简单消息接收器

```java
@Component
@Slf4j
public class DirectReceiver {

    /**
     * 简单直连消息接收
     * @param message
     */
    @RabbitListener(queuesToDeclare=@Queue("direct.queue"))
    @RabbitHandler
    public void message(Message message) {
        log.info("message result = {}",message);
    }


}
```

### 启动服务

启动服务

### 运行测试类

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootRabbitmqExampleApplicationTests {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void contextLoads() {
        Book book = new Book(1,"spring cloud","mtcarpenter");
        amqpTemplate.convertAndSend("direct.queue",book);
    }

}
```

### 运行结果

```java
2020-05-25 15:00:09.444  INFO 1456 --- [ntContainer#0-1] c.m.r.example.direct.DirectReceiver      : message result = (Body:'{"id":1,"name":"spring cloud","author":"mtcarpenter"}' MessageProperties [headers={__TypeId__=com.mtcarpenter.rabbitmq.example.Book}, contentType=application/json, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=, receivedRoutingKey=direct.queue, deliveryTag=1, consumerTag=amq.ctag-5LNFFWiSMM7C338m1SSRvg, consumerQueue=direct.queue])

```

在主应用控制类控制台会接受如下消息。