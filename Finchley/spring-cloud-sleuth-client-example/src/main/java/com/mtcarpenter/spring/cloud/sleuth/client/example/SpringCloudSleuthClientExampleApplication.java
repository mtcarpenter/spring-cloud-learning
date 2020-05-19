package com.mtcarpenter.spring.cloud.sleuth.client.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SpringCloudSleuthClientExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudSleuthClientExampleApplication.class, args);
    }

}
