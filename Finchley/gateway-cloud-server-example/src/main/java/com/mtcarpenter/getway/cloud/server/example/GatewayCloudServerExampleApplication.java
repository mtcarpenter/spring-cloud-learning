package com.mtcarpenter.getway.cloud.server.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayCloudServerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayCloudServerExampleApplication.class, args);
    }

}
