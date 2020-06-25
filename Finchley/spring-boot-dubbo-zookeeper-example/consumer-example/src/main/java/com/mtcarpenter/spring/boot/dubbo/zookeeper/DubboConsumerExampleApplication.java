package com.mtcarpenter.spring.boot.dubbo.zookeeper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@SpringBootApplication
@RestController
public class DubboConsumerExampleApplication {

    @Autowired
    private ConsumerHelloServiceImpl consumerHelloService;

    @GetMapping("/consumer/{name}")
    public String say(@PathVariable("name") String name) {
        return consumerHelloService.sayHello(name);
    }

    public static void main(String[] args) {
        SpringApplication.run(DubboConsumerExampleApplication.class, args);
    }


}
