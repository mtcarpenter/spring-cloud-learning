package com.mtcarpenter.nacos.cloud.discovery.server.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@EnableDiscoveryClient
public class NacosProviderApplication {

    @Value("${server.port}")
    private String port;

    public static void main(String[] args) {
        SpringApplication.run(NacosProviderApplication.class, args);
    }


    @RestController
    class EchoController {
        @GetMapping(value = "/echo/{name}")
        public String echo(@PathVariable String name) {
            return port+": Hello Nacos Discovery " + name;
        }
    }

}
