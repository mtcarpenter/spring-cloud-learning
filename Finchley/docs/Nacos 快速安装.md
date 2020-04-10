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

- 单机模式支持 mysql

在0.7版本之前，在单机模式时nacos使用嵌入式数据库实现数据的存储，不方便观察数据存储的基本情况。0.7版本增加了支持mysql数据源能力，具体的操作步骤：

- 1.安装数据库，版本要求：5.6.5+
- 2.初始化mysql数据库，数据库初始化文件：nacos-mysql.sql
- 3.修改conf/application.properties文件，增加支持mysql数据源配置（目前只支持 mysql ），添加mysql数据源的url、用户名和密码。

![01a7a333-a347-fd0c-c8b5-d4f010843a03.png](http://mtcarpenter.oss-cn-beijing.aliyuncs.com/2020/01a7a333-a347-fd0c-c8b5-d4f010843a03.png)

再以单机模式启动nacos，nacos所有写嵌入式数据库的数据都写到了mysql。

## Nacos Docker 安装

接下通过 docker 进行 nacos 的安装。

 