# ZooKeeper 单机、伪集群、集群

ZooKeeper （本书也简称ZK）是 Apache Hadoop 的正式子项目。为分布式应用提供高效、高可用的分布式协调服务，提供了诸如数据发布/订阅、负载均衡、命名服务、分布式协调/通知和分布式锁等分布式基础服务。提供的功能包括：配置维护、名字服务、分布式同步、组服务等

## 运行模式

Zookeeper 有三种运行模式：单机模式、伪集群模式和集群模式。

## 运行环境

ZooKeeper 需要在 java 环境下运行，本文安装使用的 java 1.8。

## 下载 ZooKeeper 安装包

- 下载地址1：[http://archive.apache.org/dist/zookeeper/](http://archive.apache.org/dist/zookeeper/)

## **单机模式**

适用于刚接触 zookeeper 学习。

### 创建数据目录和日志目录

解压文件 并创建文件夹 `data` 和 `log` 用于存放数据和日志。

### 配置 zoo.cfg

进入conf 文件夹，复制一份 zoo_sample.cfg 文件，命名为 zoo.cfg，配置如下

```bash
# zookeeper时间配置中的基本单位 (毫秒)，默认值为3000
tickTime=2000    
#Zookeeper 数据存储目录，必须配置
dataDir=F:/devtools/zookeeper-3.4.14/data
#Zookeeper 日志目录，默认为 dataDir   
dataLogDir=F:/devtools/zookeeper-3.4.14/log
# 客户端提供对外端口号一般设置为 2181
clientPort=2181    
# 允许follower初始化连接到leader最大时长，它表示tickTime时间倍数 即:initLimit*tickTime
initLimit=5    
# 允许follower与leader数据同步最大时长,它表示tickTime时间倍数  默认值5，表示tickTime的5倍
syncLimit=2    
#单个客户端与zookeeper最大并发连接数
maxClientCnxns=60
# 保存的数据快照数量，之外的将会被清除
autopurge.snapRetainCount=3
#自动触发清除任务时间间隔，小时为单位。默认为0，表示不自动清除。
# autopurge.purgeInterval=1
```

### 启动服务

- 进入文件夹 bin 目录下的点击 zkServer.cmd ( mac 和 Linux 使用后缀 sh) 脚本进行服务的启动。

- 进入文件夹 bin 目录下的点击 zkCli.cmd  脚本进行客户端启动，进行命令操作。

## zkCli 客户端命令清单

### create 创建 ZNode 路径节点

 ```bash
   create [-s] [-e] [-c] [-t ttl] path [data] [acl]
 ```

- s 表示是顺序节点
- e 标识是临时节点
- path 节点路径
- data 节点数据
- acl 节点权限

```bash
[zk: localhost:2181(CONNECTED) 10] create /node 1234
Created /node
[zk: localhost:2181(CONNECTED) 12] get /node
1234
....
```

### ls 查看路劲下的所有节点

```bash
ls [-s] [-w] [-R] path  
```

- -s状态
- -w 添加监听
- -R 递归查看所有子节点 

```bash
[zk: localhost:2181(CONNECTED) 28] ls /
[node, zookeeper, node_1, node_2, node_3]
```

### ls2

ls2 path :是ls 和 stat两个命令的结合

```bash
[zk: localhost:2181(CONNECTED) 9] ls2 /zookeeper
[quota]
cZxid = 0x0
ctime = Thu Jan 01 08:00:00 CST 1970
mZxid = 0x0
mtime = Thu Jan 01 08:00:00 CST 1970
pZxid = 0x0
cversion = -1
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 0
numChildren = 1
```

### get 获得节点上的值

```bash
get [-s] [-w] path
```

- -s 包含节点状态
- -w 添加监听 

```bash
[zk: localhost:2181(CONNECTED) 0] get /

cZxid = 0x0
ctime = Thu Jan 01 08:00:00 CST 1970
mZxid = 0x0
mtime = Thu Jan 01 08:00:00 CST 1970
pZxid = 0x0
cversion = -1
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 0
numChildren = 1
```

- cZxid : 创建节点的 Zxid 事务 id
- ctime ：创建节点时的时间戳
- mZxid : 最后修改节点时的时间戳
- mtime ： 最后修改节点时的时间戳
- pZxid ：表示该节点的子节点最后一次修改的事务 id ，添加子节点或删除子节点就会影响 pZxid 的值，但是修改字节点的数据内容则不影响 id
- cversion ： 子节点版本号，子节点每次修改，该版本号就加 1
- dataVersion ： 数据版本号，数据每次修改，该版本号就加 1
- aclVersion ： 权限版本号，权限每次修改，该版本号就加 1
- ephemeralOwner ：该节点如果是临时节点，该属性是临时节点的事物 ID
- dataLength ：该节点的数据长度
- numChildren ：该节点拥有子节点的数量

### set 修改节点上的值

修改当前节点的数据内容  如果指定版本，需要和当前节点的数据版本一致

```bash
set path data [version] 
```

```bash
[zk: localhost:2181(CONNECTED) 33] set /node 666
cZxid = 0x2
ctime = Thu Jun 11 11:05:19 CST 2020
mZxid = 0x8
......
[zk: localhost:2181(CONNECTED) 34] get /node
666
cZxid = 0x2
.......
```

### delete 删除节点

 删除指定路径的节点 如果有子节点要先删除子节点

```bash
delete path [version]
```

```bash
[zk: localhost:2181(CONNECTED) 38] create /node/nodechild data
Created /node/nodechild
###必须删除子节点
[zk: localhost:2181(CONNECTED) 47] delete /node
Node not empty: /node
```

### rmr 删除当前和字节点

 删除当前路径节点及其所有子节点

```bash
  rmr path
```

```bash
[zk: localhost:2181(CONNECTED) 49] rmr /node
[zk: localhost:2181(CONNECTED) 50]
```
### stat 
监听节点属性的变化
```bash
stat -w path 
```

## **伪集群**

- ZooKeeper 集群节点数必须是奇数

  为什么呢？在 ZooKeeper 集群中，需要一个主节点，也称为Leader节点。主节点是集群通过选举的规则从所有节点中选举出来的。在选举的规则中很重要的一条是：要求可用节点数量>总节点数量/2。如果是偶数个节点，则可能会出现不满足这个规则的情况。

- ZooKeeper集群至少是3个

  ZooKeeper可以通过一个节点，正常启动和提供服务。但是，一个节点的ZooKeeper服务不能叫作集群，其可靠性会大打折扣，仅仅作为学习使用尚可。在正常情况下，搭建ZooKeeper集群，至少需要3个节点。

### 伪节点日志目录和数据目录

#### 数据目录

在 data 目录下 创建 三个目录文件 data1，data2 ，data3

```bash
PS F:\devtools\zookeeper\data> ls
d-----        2020/6/11     11:46                data1
d-----        2020/6/11     11:47                data2
d-----        2020/6/11     11:47                data3

PS F:\devtools\zookeeper\data>
```

#### 在data1、data2、data3新建 myid 文件

- data1下的myid文件内容: 1

- data2下的myid文件内容: 2

- data3下的myid文件内容: 3

>  myid文件内容为一个数字，表示节点的编号，myid文件是一个文本文件，文件名称为 myid 无后缀，文件的内容为 "1"，表示第一个节点的编号为1，一次类推

  ### 修改配置文件

将单机模式的 zoo.cfg 复制三份，分别命名为zoo1.cfg、zoo2.cfg、zoo3.cfg，对应于3个节点。

**zoo1.cfg 配置修改**

```bash
# zookeeper时间配置中的基本单位 (毫秒)，默认值为3000
tickTime=2000    
# 允许follower初始化连接到leader最大时长，它表示tickTime时间倍数 即:initLimit*tickTime
initLimit=5    
# 允许follower与leader数据同步最大时长,它表示tickTime时间倍数  默认值5，表示tickTime的5倍
syncLimit=2    
#单个客户端与zookeeper最大并发连接数
maxClientCnxns=60
# 保存的数据快照数量，之外的将会被清除
autopurge.snapRetainCount=3
#自动触发清除任务时间间隔，小时为单位。默认为0，表示不自动清除。
# autopurge.purgeInterval=1
#Zookeeper 数据存储目录，必须配置
dataDir=F:/devtools/zookeeper/data/data1
#Zookeeper 日志目录，默认为 dataDir   
dataLogDir=F:/devtools/zookeeper/log/log1
# 客户端提供对外端口号一般设置为 2181
clientPort=2181    

server.1=127.0.0.1:2888:3888
server.2=127.0.0.1:2889:3889
server.3=127.0.0.1:2890:3890
```

**zoo2.cfg 配置修改**

 ```bash
# zookeeper时间配置中的基本单位 (毫秒)，默认值为3000
tickTime=2000    
# 允许follower初始化连接到leader最大时长，它表示tickTime时间倍数 即:initLimit*tickTime
initLimit=5    
# 允许follower与leader数据同步最大时长,它表示tickTime时间倍数  默认值5，表示tickTime的5倍
syncLimit=2    
#单个客户端与zookeeper最大并发连接数
maxClientCnxns=60
# 保存的数据快照数量，之外的将会被清除
autopurge.snapRetainCount=3
#自动触发清除任务时间间隔，小时为单位。默认为0，表示不自动清除。
# autopurge.purgeInterval=1
#Zookeeper 数据存储目录，必须配置
dataDir=F:/devtools/zookeeper/data/data2
#Zookeeper 日志目录，默认为 dataDir   
dataLogDir=F:/devtools/zookeeper/log/log2
# 客户端提供对外端口号一般设置为 2181
clientPort=2182    

server.1=127.0.0.1:2888:3888
server.2=127.0.0.1:2889:3889
server.3=127.0.0.1:2890:3890
 ```

> 修改暴露端口(2182 ) 和 文件数据日志目录

**zoo3.cfg 配置修改**

```bash
# zookeeper时间配置中的基本单位 (毫秒)，默认值为3000
tickTime=2000    
# 允许follower初始化连接到leader最大时长，它表示tickTime时间倍数 即:initLimit*tickTime
initLimit=5    
# 允许follower与leader数据同步最大时长,它表示tickTime时间倍数  默认值5，表示tickTime的5倍
syncLimit=2    
#单个客户端与zookeeper最大并发连接数
maxClientCnxns=60
# 保存的数据快照数量，之外的将会被清除
autopurge.snapRetainCount=3
#自动触发清除任务时间间隔，小时为单位。默认为0，表示不自动清除。
# autopurge.purgeInterval=1
#Zookeeper 数据存储目录，必须配置
dataDir=F:/devtools/zookeeper/data/data3
#Zookeeper 日志目录，默认为 dataDir   
dataLogDir=F:/devtools/zookeeper/log/log3
# 客户端提供对外端口号一般设置为 2181
clientPort=2183    

server.1=127.0.0.1:2888:3888
server.2=127.0.0.1:2889:3889
server.3=127.0.0.1:2890:3890
```

> 修改暴露端口(2183) 和 文件数据日志目录

### 启动文件

进入 bin 目录下，复制 zkServer.cmd ，为每一个伪节点创建一个启动的命令文件，分别命令为 zkServer1.cmd、zkServer2.cmd、zkServer3.cmd。

主要增加配置文件 

**zkServer1.cmd**

```bash
@echo off
REM Licensed to the Apache Software Foundation (ASF) under one or more
REM contributor license agreements.  See the NOTICE file distributed with
REM this work for additional information regarding copyright ownership.
REM The ASF licenses this file to You under the Apache License, Version 2.0
REM (the "License"); you may not use this file except in compliance with
REM the License.  You may obtain a copy of the License at
REM
REM     http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.

setlocal
call "%~dp0zkEnv.cmd"

set ZOOMAIN=org.apache.zookeeper.server.quorum.QuorumPeerMain
set ZOOCFG=..\conf\zoo1.cfg
echo on
call %JAVA% "-Dzookeeper.log.dir=%ZOO_LOG_DIR%" "-Dzookeeper.root.logger=%ZOO_LOG4J_PROP%" -cp "%CLASSPATH%" %ZOOMAIN% "%ZOOCFG%" %*

endlocal

```

**zkServer2.cmd**

```bash
setlocal
call "%~dp0zkEnv.cmd"

set ZOOMAIN=org.apache.zookeeper.server.quorum.QuorumPeerMain
set ZOOCFG=..\conf\zoo2.cfg
echo on
call %JAVA% "-Dzookeeper.log.dir=%ZOO_LOG_DIR%" "-Dzookeeper.root.logger=%ZOO_LOG4J_PROP%" -cp "%CLASSPATH%" %ZOOMAIN% "%ZOOCFG%" %*

endlocal
```

> 加入 set ZOOCFG=..\conf\zoo2.cfg

**zkServer3.cmd**

```bash
setlocal
call "%~dp0zkEnv.cmd"

set ZOOMAIN=org.apache.zookeeper.server.quorum.QuorumPeerMain
set ZOOCFG=..\conf\zoo3.cfg
echo on
call %JAVA% "-Dzookeeper.log.dir=%ZOO_LOG_DIR%" "-Dzookeeper.root.logger=%ZOO_LOG4J_PROP%" -cp "%CLASSPATH%" %ZOOMAIN% "%ZOOCFG%" %*

endlocal
```

> 加入 set ZOOCFG=..\conf\zoo3.cfg

分别启动三个 cmd 文件，默认没有启动完会出现连接错误异常

## **集群模式**

集群模式将前面的单机模式复制三份。

```java
PS F:\devtools> ls
Mode                LastWriteTime         Length Name
----                -------------         ------ ----
d-----        2020/6/11     11:34                zookeeper-1
d-----        2020/6/11     11:34                zookeeper-2
d-----        2020/6/11     11:34                zookeeper-3
```

### 进入 zookeeper 的 data 文件夹 新建 myid 文件

-  zookeeper-1 下的 myid 文件内容: 1

- zookeeper-2下的 myid 文件内容: 2

- zookeeper-3 下的 myid 文件内容: 3

### zookeeper-1 中的 zoo.cfg 配置修改

```bash
# zookeeper时间配置中的基本单位 (毫秒)，默认值为3000
tickTime=2000    
# 允许follower初始化连接到leader最大时长，它表示tickTime时间倍数 即:initLimit*tickTime
initLimit=5    
# 允许follower与leader数据同步最大时长,它表示tickTime时间倍数  默认值5，表示tickTime的5倍
syncLimit=2    
#单个客户端与zookeeper最大并发连接数
maxClientCnxns=60
# 保存的数据快照数量，之外的将会被清除
autopurge.snapRetainCount=3
#自动触发清除任务时间间隔，小时为单位。默认为0，表示不自动清除。
# autopurge.purgeInterval=1
#Zookeeper 数据存储目录，必须配置
dataDir=F:/devtools/zookeeper-1/data
#Zookeeper 日志目录，默认为 dataDir   
dataLogDir=F:/devtools/zookeeper-1/log
# 客户端提供对外端口号一般设置为 2181
clientPort=2181    

server.1=127.0.0.1:2888:3888
server.2=127.0.0.1:2889:3889
server.3=127.0.0.1:2890:3890
```

### zookeeper-2 中的 zoo.cfg 配置修改

```bash
#
# 省略与 zookeepe-1 重复的配置
#
#Zookeeper 数据存储目录，必须配置
dataDir=F:/devtools/zookeeper-2/data
#Zookeeper 日志目录，默认为 dataDir   
dataLogDir=F:/devtools/zookeeper-2/log
# 客户端提供对外端口号一般设置为 2181
clientPort=2182  

server.1=127.0.0.1:2888:3888
server.2=127.0.0.1:2889:3889
server.3=127.0.0.1:2890:3890
```

### zookeeper-3 中的 zoo.cfg 配置修改

```bash
#
# 省略与 zookeepe-1 重复的配置
#
#Zookeeper 数据存储目录，必须配置
dataDir=F:/devtools/zookeeper-3/data
#Zookeeper 日志目录，默认为 dataDir   
dataLogDir=F:/devtools/zookeeper-3/log
# 客户端提供对外端口号一般设置为 2181
clientPort=2183  

server.1=127.0.0.1:2888:3888
server.2=127.0.0.1:2889:3889
server.3=127.0.0.1:2890:3890
```

