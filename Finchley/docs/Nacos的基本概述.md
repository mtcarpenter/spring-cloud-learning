# Nacos 的基本概述

##  Nacos 概述

Nacos 致力于帮助您发现、配置和管理微服务。Nacos 提供了一组简单易用的特性集，帮助您快速实现动态服务发现、服务配置、服务元数据及流量管理。

Nacos 帮助您更敏捷和容易地构建、交付和管理微服务平台。 Nacos 是构建以“服务”为中心的现代应用架构 (例如微服务范式、云原生范式) 的服务基础设施。

## Consul、Eureka、nacos对比

### 配置中心

- eureka 不支持
- consul 支持 但用起来偏麻烦，不太符合 springBoot 框架的命名风格，支持动态刷新
- nacos 支持 用起来简单，符合springBoot的命名风格，支持动态刷新

### 注册中心

- eureka

  - 依赖：不依赖ZooKeeper
  - 应用内/外：直接集成到应用中，依赖于应用自身完成服务的注册与发现，
  - ACP原则：遵循AP（可用性+分离容忍）原则，有较强的可用性，服务注册快，但牺牲了一定的一致性。
  - 版本迭代：目前已经不进行升级
  - 集成支持：只支持SpringCloud集成
  - 访问协议：HTTP
  - 雪崩保护：支持雪崩保护
  - 界面：英文界面，不符合国人习惯
  - 上手：容易

- consul

  - 依赖：不依赖其他组件
  - 应用内/外：属于外部应用，侵入性小
  - ACP原则：遵循CP原则（一致性+分离容忍） 服务注册稍慢，由于其一致性导致了在Leader挂掉时重新选举期间真个consul不可用。
  - 版本迭代：目前仍然进行版本迭代
  - 集成支持：支持SpringCloud K8S集成
  - 访问协议：HTTP/DNS
  - 雪崩保护：不支持雪崩保护
  - 界面：英文界面，不符合国人习惯
  - 上手：复杂一点

- nacos

  - 依赖：不依赖其他组件
- 应用内/外：属于外部应用，侵入性小
  - ACP原则：通知遵循CP原则（一致性+分离容忍） 和AP原则（可用性+分离容忍）
- 版本迭代：目前仍然进行版本迭代
  - 集成支持：支持Dubbo 、SpringCloud、K8S集成
- 访问协议：HTTP/动态DNS/UDP
  - 雪崩保护：支持雪崩保护
- 界面：中文界面，符合国人习惯
  - 上手：极易，中文文档，案例，社区活跃
-  Nacos 1.2.0 支持权限控制
  


## 什么是 Nacos？

服务（Service）是 Nacos 世界的一等公民。Nacos 支持几乎所有主流类型的“服务”的发现、配置和管理：

Nacos 的关键特性包括:

- **服务发现和服务健康监测**

  Nacos 支持基于 DNS 和基于 RPC 的服务发现。服务提供者使用 [原生SDK](https://nacos.io/zh-cn/docs/sdk.html)、[OpenAPI](https://nacos.io/zh-cn/docs/open-API.html)、或一个[独立的Agent TODO](https://nacos.io/zh-cn/docs/other-language.html)注册 Service 后，服务消费者可以使用[DNS TODO](https://nacos.io/zh-cn/docs/xx) 或[HTTP&API](https://nacos.io/zh-cn/docs/open-API.html)查找和发现服务。

  Nacos 提供对服务的实时的健康检查，阻止向不健康的主机或服务实例发送请求。Nacos 支持传输层 (PING 或 TCP)和应用层 (如 HTTP、MySQL、用户自定义）的健康检查。 对于复杂的云环境和网络拓扑环境中（如 VPC、边缘网络等）服务的健康检查，Nacos 提供了 agent 上报模式和服务端主动检测2种健康检查模式。Nacos 还提供了统一的健康检查仪表盘，帮助您根据健康状态管理服务的可用性及流量。

- **动态配置服务**

  动态配置服务可以让您以中心化、外部化和动态化的方式管理所有环境的应用配置和服务配置。

  动态配置消除了配置变更时重新部署应用和服务的需要，让配置管理变得更加高效和敏捷。

  配置中心化管理让实现无状态服务变得更简单，让服务按需弹性扩展变得更容易。

  Nacos 提供了一个简洁易用的UI ([控制台样例 Demo](http://console.nacos.io/nacos/index.html)) 帮助您管理所有的服务和应用的配置。Nacos 还提供包括配置版本跟踪、金丝雀发布、一键回滚配置以及客户端配置更新状态跟踪在内的一系列开箱即用的配置管理特性，帮助您更安全地在生产环境中管理配置变更和降低配置变更带来的风险。

- **动态 DNS 服务**

  动态 DNS 服务支持权重路由，让您更容易地实现中间层负载均衡、更灵活的路由策略、流量控制以及数据中心内网的简单DNS解析服务。动态DNS服务还能让您更容易地实现以 DNS 协议为基础的服务发现，以帮助您消除耦合到厂商私有服务发现 API 上的风险。

  Nacos 提供了一些简单的 [DNS APIs TODO](https://nacos.io/zh-cn/docs/xx) 帮助您管理服务的关联域名和可用的 IP:PORT 列表.

- **服务及其元数据管理**

  Nacos 能让您从微服务平台建设的视角管理数据中心的所有服务及元数据，包括管理服务的描述、生命周期、服务的静态依赖分析、服务的健康状态、服务的流量管理、路由及安全策略、服务的 SLA 以及最首要的 metrics 统计数据。

- [更多的特性列表 ...](https://nacos.io/zh-cn/docs/roadmap.html)

## Nacos 地图

![nacosMap.jpg](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/nacosMap.jpg)

- 特性大图：要从功能特性，非功能特性，全面介绍我们要解的问题域的特性诉求
- 架构大图：通过清晰架构，让您快速进入 Nacos 世界
- 业务大图：利用当前特性可以支持的业务场景，及其最佳实践
- 生态大图：系统梳理 Nacos 和主流技术生态的关系
- 优势大图：展示 Nacos 核心竞争力
- 战略大图：要从战略到战术层面讲 Nacos 的宏观优势

## Nacos 生态图

![1533045871534-e64b8031-008c-4dfc-b6e8-12a597a003fb.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/1533045871534-e64b8031-008c-4dfc-b6e8-12a597a003fb.png)
