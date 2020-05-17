package com.mtcarpenter.spring.cloud.feign.server.example;

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
@RequestMapping("/feign")
public class FeignServerController {

    @GetMapping("/say/{name}")
    public String sayHello(@PathVariable("name") String name) {
        return name + " say hello";
    }
}
