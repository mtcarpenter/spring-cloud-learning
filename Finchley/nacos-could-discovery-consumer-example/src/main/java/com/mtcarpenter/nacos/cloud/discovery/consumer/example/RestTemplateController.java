
package com.mtcarpenter.nacos.cloud.discovery.consumer.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class RestTemplateController {

    @Autowired
    private  RestTemplate restTemplate;


    @GetMapping(value = "/echo/{name}")
    public String echo(@PathVariable String name) {
        // 原始方式通过 ip:port 进行访问
        // restTemplate.getForObject("http://localhost:8081/msg"+name,String.class);

        //  通过 spring.application.name 名称发现
        return restTemplate.getForObject("http://nacos-provider/echo/" + name, String.class);
    }

}