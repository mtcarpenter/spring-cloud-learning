package com.mtcarpenter.gateway.cloud.client.filter.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayCloudClientFilterExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayCloudClientFilterExampleApplication.class, args);
    }

}
