
package com.mtcarpenter.nacos.cloud.discovery.consumer.feign.example;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "nacos-provider")
public interface Feign {

    @GetMapping("/echo/{name}")
    String echo(@PathVariable String name);
}