package com.mtcarpenter.getway.cloud.server.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayCloudServerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayCloudServerExampleApplication.class, args);
    }

}
