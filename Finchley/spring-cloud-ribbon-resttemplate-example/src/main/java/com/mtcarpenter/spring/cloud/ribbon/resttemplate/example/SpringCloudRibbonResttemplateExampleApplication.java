package com.mtcarpenter.spring.cloud.ribbon.resttemplate.example;

import com.mtcarpenter.ribbon.RibbonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;

@SpringBootApplication
// 针对某一个服务
//@RibbonClient(name = "ribbon-server",configuration = RibbonConfig.class)
// 全局配置
//@RibbonClients(defaultConfiguration = RibbonConfig.class )
public class SpringCloudRibbonResttemplateExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudRibbonResttemplateExampleApplication.class, args);
    }

}
