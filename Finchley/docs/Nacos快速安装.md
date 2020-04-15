# Nacos 快速安装

##  Nacos 本地安装

- 下载地址：https://github.com/alibaba/nacos/releases

> 本系列采用 nacos 版本为 1.2.0 

- 解压

```sh
 nacos-server-1.2.0.zip
 cd nacos/bin # 进入 nacos/bin 
```

- 单机版启动

  Linux/Unix/Mac

```sh
sh startup.sh -m standalone
```

 Windows 

```sh
cmd startup.cmd -m standalone
```

- 启动界面如下

 访问：`http://127.0.0.1:8848/nacos/`

![867996c1-8da7-cfa1-05e3-5b0dd5598db1.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/867996c1-8da7-cfa1-05e3-5b0dd5598db1.png)

- 单机模式支持 mysql (将数据持久化)

在0.7版本之前，在单机模式时nacos使用嵌入式数据库实现数据的存储，不方便观察数据存储的基本情况。0.7版本增加了支持mysql数据源能力，具体的操作步骤：

- 1.安装数据库，版本要求：5.6.5+
- 2.初始化mysql数据库，数据库初始化文件：nacos-mysql.sql
- 3.修改conf/application.properties文件，增加支持mysql数据源配置（目前只支持 mysql ），添加mysql数据源的url、用户名和密码。

![01a7a333-a347-fd0c-c8b5-d4f010843a03.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/01a7a333-a347-fd0c-c8b5-d4f010843a03.png)

- 配置 `application.properties`

```properties
spring.datasource.platform=mysql

### Count of DB:
db.num=1

### Connect URL of DB:
db.url.0=jdbc:mysql://192.168.0.145:3306/nacos_devtest?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
db.user=nacos
db.password=nacos
```

再以单机模式启动nacos，nacos所有写嵌入式数据库的数据都写到了mysql。

## 集群模式部署

### 集群部署架构图

因此开源的时候推荐用户把所有服务列表放到一个vip下面，然后挂到一个域名下面

[http://ip1](http://ip1/):port/openAPI 直连ip模式，机器挂则需要修改ip才可以使用。

[http://VIP](http://vip/):port/openAPI 挂载VIP模式，直连vip即可，下面挂server真实ip，可读性不好。

[http://nacos.com](http://nacos.com/):port/openAPI 域名 + VIP模式，可读性好，而且换ip方便，推荐模式

![1](https://cdn.nlark.com/yuque/0/2019/jpeg/338441/1561258986171-4ddec33c-a632-4ec3-bfff-7ef4ffc33fb9.jpeg)

### Nacos 配置集群配置文件

在 nacos 的解压目录 nacos/ 的 conf 目录下，有配置文件cluster.conf，请每行配置成 ip:port。（请配置3个或3个以上节点）

```sh
# ip:port
192.168.0.156:8848
192.168.0.156:8847
192.168.0.156:8846
```

### 配置 `nginx.conf`

```sh
    upstream nacoscluster {
	 server 192.168.0.156:8848;
	 server 192.168.0.156:8847;
	 server 192.168.0.156:8846;
	}
	 
	server {
		listen       80;
		server_name  localhost;
		location /nacos/ {
			#代理
			proxy_pass http://nacoscluster/nacos/;
		}
	}
```

访问：`http://localhost/nacos/`, 可看到Nacos的登录页，登录后即可正常使用Nacos。

### 配置 `application.properties`

```properties
spring.cloud.nacos.discovery.server-addr=nginx路径：端口
#									例如: 192.168.0.156：80	
```

## Nacos Docker 安装

接下通过 docker 进行 nacos 的安装。

- nacos docker github 地址：https://github.com/nacos-group/nacos-docker

![703df4e1-cf49-553e-97ad-1d441afa83ba.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/703df4e1-cf49-553e-97ad-1d441afa83ba.png)

### Docker 运行 Nacos

```sh
# 拉取镜像
[root@localhost nacos-docker]# docker pull nacos/nacos-server:latest
# 单机运行 MODE=standalone 单机
[root@localhost nacos-docker]# docker run --name nacos-standalone -e MODE=standalone -p 8848:8848 -d nacos/nacos-server:latest

```

### Docker-compose 运行

- Clone 项目并且进入项目根目录

```sh
[root@localhost home]#git clone https://github.com/paderlol/nacos-docker.git
[root@localhost home]#cd nacos-docker
```

- 单机

```sh
[root@localhost home]#docker-compose -f example/standalone.yaml up

```

- 单机配置数据库

```sh
[root@localhost home]#docker-compose -f example/standalone-mysql-5.7.yaml   up 
```

![dff663a3-b48f-5d6c-9bf4-d2d8862e7237.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/dff663a3-b48f-5d6c-9bf4-d2d8862e7237.png)

> 注：env 中存放着 docekr-compose 启动时参数， 可以参看**属性配置列表**进行配置，如下新增登录验证

```sh
PREFER_HOST_MODE=hostname
MODE=standalone
SPRING_DATASOURCE_PLATFORM=mysql
MYSQL_SERVICE_HOST=mysql
MYSQL_SERVICE_DB_NAME=nacos_devtest
MYSQL_SERVICE_PORT=3306
MYSQL_SERVICE_USER=nacos
MYSQL_SERVICE_PASSWORD=nacos
NACOS_AUTH_ENABLE=true # 新增授权，启动之后 需要账号和密码登录 才能，默认：nacos/nacos 
```

 `NACOS_AUTH_ENABLE=true `开启权限之后菜单，新增菜单栏权限控制

![180cffac-5ddb-1373-501f-b9e8def56e5a.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/180cffac-5ddb-1373-501f-b9e8def56e5a.png)

- 集群

```sh
[root@localhost home]#docker-compose -f example/cluster-hostname.yaml up  
```

## 属性配置列表

| 属性名称                          | 描述                                                         | 选项                                                         |
| --------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| MODE                              | 系统启动方式: 集群/单机                                      | cluster/standalone默认 **cluster**                           |
| NACOS_SERVERS                     | nacos cluster address                                        | p1:port1空格ip2:port2 空格ip3:port3                          |
| PREFER_HOST_MODE                  | 支持IP还是域名模式                                           | hostname/ip 默认 **ip**                                      |
| NACOS_SERVER_PORT                 | Nacos 运行端口                                               | 默认 **8848**                                                |
| NACOS_SERVER_IP                   | 多网卡模式下可以指定IP                                       |                                                              |
| SPRING_DATASOURCE_PLATFORM        | standalone support mysql                                     | mysql / 空 默认:空                                           |
| MYSQL_SERVICE_HOST                | mysql host                                                   |                                                              |
| MYSQL_SERVICE_PORT                | mysql database port                                          | 默认 : **3306**                                              |
| MYSQL_SERVICE_DB_NAME             | mysql database name                                          |                                                              |
| MYSQL_SERVICE_USER                | username of database                                         |                                                              |
| MYSQL_SERVICE_PASSWORD            | password of database                                         |                                                              |
| ~~MYSQL_MASTER_SERVICE_HOST~~     | **latest(目前latest 是1.1.4)以后**版本镜像移除, 使用 MYSQL_SERVICE_HOST |                                                              |
| ~~MYSQL_MASTER_SERVICE_PORT~~     | **latest(目前latest 是1.1.4)以后**版本镜像移除, 使用 using MYSQL_SERVICE_PORT | 默认 : **3306**                                              |
| ~~MYSQL_MASTER_SERVICE_DB_NAME~~  | **latest(目前latest 是1.1.4)以后**版本镜像移除, 使用 MYSQL_SERVICE_DB_NAME |                                                              |
| ~~MYSQL_MASTER_SERVICE_USER~~     | **latest(目前latest 是1.1.4)以后**版本镜像移除, 使用 MYSQL_SERVICE_USER |                                                              |
| ~~MYSQL_MASTER_SERVICE_PASSWORD~~ | **latest(目前latest 是1.1.4)以后**版本镜像移除, 使用, using MYSQL_SERVICE_PASSWORD |                                                              |
| ~~MYSQL_SLAVE_SERVICE_HOST~~      | **latest(目前latest 是1.1.4)以后**版本镜像移除               |                                                              |
| ~~MYSQL_SLAVE_SERVICE_PORT~~      | **latest(目前latest 是1.1.4)以后**版本镜像移除               | 默认 :3306                                                   |
| MYSQL_DATABASE_NUM                | It indicates the number of database                          | 默认 :**1**                                                  |
| JVM_XMS                           | -Xms                                                         | 默认 :2g                                                     |
| JVM_XMX                           | -Xmx                                                         | 默认 :2g                                                     |
| JVM_XMN                           | -Xmn                                                         | 默认 :1g                                                     |
| JVM_MS                            | -XX:MetaspaceSize                                            | 默认 :128m                                                   |
| JVM_MMS                           | -XX:MaxMetaspaceSize                                         | 默认 :320m                                                   |
| NACOS_DEBUG                       | enable remote debug                                          | y/n 默认 :n                                                  |
| TOMCAT_ACCESSLOG_ENABLED          | server.tomcat.accesslog.enabled                              | 默认 :false                                                  |
| NACOS_AUTH_SYSTEM_TYPE            | 权限系统类型选择,目前只支持nacos类型                         | 默认 :nacos                                                  |
| NACOS_AUTH_ENABLE                 | 是否开启权限系统                                             | 默认 :false                                                  |
| NACOS_AUTH_TOKEN_EXPIRE_SECONDS   | token 失效时间                                               | 默认 :18000                                                  |
| NACOS_AUTH_TOKEN                  | token                                                        | 默认 :SecretKey012345678901234567890123456789012345678901234567890123456789 |
| NACOS_AUTH_CACHE_ENABLE           | 权限缓存开关 ,开启后权限缓存的更新默认有15秒的延迟           | 默认 : false                                                 |

## Nacos + Grafana + Prometheus

使用参考：[Nacos monitor-guide](https://nacos.io/zh-cn/docs/monitor-guide.html)

**Note**: 当使用Grafana创建数据源的时候地址必须是: **[http://prometheus:9090](http://prometheus:9090/)**