package com.mtcarpenter.spring.cloud.feign.basic.example.controller;

import com.mtcarpenter.spring.cloud.feign.basic.example.feign.FeignServerClient;
import com.mtcarpenter.spring.cloud.feign.basic.example.feign.FeignUrlClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */

@RestController
@RequestMapping("/basic")
public class BasicController {

    @Autowired
    private FeignServerClient feignServerClient;

    @Autowired
    private FeignUrlClient feignUrlClient;

    @GetMapping("/say/{name}")
    public String sayHello(@PathVariable("name") String name) {
        return feignServerClient.sayHello(name);
    }

    @GetMapping("/cloud")
    public String cloud() {
        return feignUrlClient.cloud();
    }

}
