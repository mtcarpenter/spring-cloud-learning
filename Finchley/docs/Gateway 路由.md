## Gateway 路由篇

## 概述

Spring cloud gateway是spring官方基于Spring 5.0、Spring Boot2.0和Project Reactor等技术开发的网关，Spring Cloud Gateway旨在为微服务架构提供简单、有效和统一的API路由管理方式，Spring Cloud Gateway作为Spring Cloud生态系统中的网关，目标是替代Netflix Zuul，其不仅提供统一的路由方式，并且还基于Filer链的方式提供了网关基本的功能，例如：安全、监控/埋点、限流等。

## 核心概念

网关提供 API 全托管服务，丰富的API管理功能，辅助企业管理大规模的API，以降低管理成本和安全风险，包括协议适配、协议转发、安全策略、防刷、流量、监控日志等贡呢。一般来说网关对外暴露的URL或者接口信息，我们统称为路由信息。如果研发过网关中间件或者使用过Zuul的人，会知道网关的核心是Filter以及Filter Chain（Filter责任链）。Sprig Cloud Gateway也具有路由和Filter的概念。下面介绍一下Spring Cloud Gateway中几个重要的概念。

![](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/385d5b2c-cafe-c0ef-f227-6481ab477c45.png)

- 路由。路由是网关最基础的部分，路由信息有一个ID、一个目的URL、一组断言和一组Filter组成。如果断言路由为真，则说明请求的URL和配置匹配
- 断言。Java8中的断言函数。Spring Cloud Gateway中的断言函数输入类型是Spring5.0框架中的ServerWebExchange。Spring Cloud Gateway中的断言函数允许开发者去定义匹配来自于http request中的任何信息，比如请求头和参数等。
- 过滤器。一个标准的Spring webFilter。Spring cloud gateway中的filter分为两种类型的Filter，分别是Gateway Filter和Global Filter。过滤器Filter将会对请求和响应进行修改处理。

## 工作原理图





![img](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/0b9fcab1-4a15-671f-5d9f-26c08f091e5e.png)



Gateway 的客户端会向 Spring Cloud Gateway 发起请求，请求首先会被 HtpWebHandlerAdapter 进行提取组装成网关的上下文，然后网关的上下文会传递到DispatcherHandler. DispatcherHandler 是所有请求的分发处理器，DispatcherHandler主要负责分发请求对应的处理器，比如将请求分发到对应RoutePredicate-
HandlerMapping (路由断言处理映射器)。路由断言处理映射器主要用于路由的查找，以及找到路由后返回对应的FilteringWebHandler。FilteringWebHandler 主要负责组装Filter链表并调用 Filter 执行- - 系列的Filter处理，然后把请求转到后端对应的代理服务处理，处理完毕之后，将 Response 返回到Gateway客户端。