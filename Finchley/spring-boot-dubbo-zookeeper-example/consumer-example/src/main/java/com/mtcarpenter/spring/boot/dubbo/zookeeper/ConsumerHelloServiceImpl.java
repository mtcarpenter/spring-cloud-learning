package com.mtcarpenter.spring.boot.dubbo.zookeeper;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/mall-cloud-alibaba
 * @desc 微信公众号：山间木匠
 */
@Service
public class ConsumerHelloServiceImpl {

    @DubboReference(version = "1.0.0")
    private HelloService helloService;

    public String sayHello(String username){
        return helloService.sayHello(username);
    }

}
