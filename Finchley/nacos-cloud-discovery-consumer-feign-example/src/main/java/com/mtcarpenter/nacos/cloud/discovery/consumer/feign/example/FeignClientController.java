
package com.mtcarpenter.nacos.cloud.discovery.consumer.feign.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignClientController {

    @Autowired
    private Feign feign;

    @GetMapping(value = "/echo/{name}")
    public String echo(@PathVariable String name) {
        return feign.echo(name);
    }
}