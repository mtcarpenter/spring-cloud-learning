package com.mtcarpenter.spring.cloud.feign.basic.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SpringCloudFeignBasicExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudFeignBasicExampleApplication.class, args);
    }

}
