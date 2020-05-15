package com.mtcarpenter.spring.cloud.ribbon.resttemplate.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@RestController
@RequestMapping("/test")
public class TestController {
    private static final Log log = LogFactory.getLog(TestController.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/instances")
    public  List<ServiceInstance> instances(){
        List<ServiceInstance> instances = discoveryClient.getInstances("ribbon-server");
        return instances;
    }

    @GetMapping("/randomServerUrl")
    public String randomServerUrl(){
        List<ServiceInstance> instances = discoveryClient.getInstances("ribbon-server");
        List<String> urls = instances.stream().map(s->s.getUri().toString()).collect(Collectors.toList());
        // 随机获取
        int i = ThreadLocalRandom.current().nextInt(urls.size());
        String requestUrl = urls.get(i).concat("/user/sayHello");
        log.info("request url = "+requestUrl);
        String result = restTemplate.getForObject(requestUrl, String.class);
        return result;
    }

    @GetMapping("/serviceId")
    public String serviceId(){
        String result = restTemplate.getForObject("http://ribbon-server/user/sayHello", String.class);
        return result;
    }

}
