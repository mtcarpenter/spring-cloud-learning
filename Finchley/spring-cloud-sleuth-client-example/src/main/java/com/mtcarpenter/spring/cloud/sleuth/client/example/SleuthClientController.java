package com.mtcarpenter.spring.cloud.sleuth.client.example;

import com.mtcarpenter.spring.cloud.sleuth.client.example.feign.SleuthServerFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@RestController
@RequestMapping("/client")
public class SleuthClientController {
    @Autowired
    private SleuthServerFeign sleuthServerFeign;

    @GetMapping("/sayHello")
    public String sayHello() {
        return sleuthServerFeign.sayHello();
    }

    @GetMapping("/error")
    public String error() {
        return sleuthServerFeign.error();
    }
}