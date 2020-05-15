package com.mtcarpenter.spring.cloud.ribbon.server.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Log log = LogFactory.getLog(UserController.class);

    @GetMapping("/sayHello")
    public String sayHello(){
        log.info("say hello");
        return "sayHello";
    }

}
