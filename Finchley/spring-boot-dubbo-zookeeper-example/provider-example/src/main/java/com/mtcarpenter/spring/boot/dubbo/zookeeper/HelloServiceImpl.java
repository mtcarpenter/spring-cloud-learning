package com.mtcarpenter.spring.boot.dubbo.zookeeper;

import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/mall-cloud-alibaba
 * @desc 微信公众号：山间木匠
 */

@DubboService(version = "1.0.0")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String username) {
        return username +"  say hello provider";
    }
}
