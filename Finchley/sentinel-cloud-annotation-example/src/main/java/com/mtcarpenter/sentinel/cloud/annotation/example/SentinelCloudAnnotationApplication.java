package com.mtcarpenter.sentinel.cloud.annotation.example;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@SpringBootApplication
public class SentinelCloudAnnotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelCloudAnnotationApplication.class, args);
    }

}
