
package com.mtcarpenter.nacos.config.example;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

    @Value("${mtcarpenter.title:none}")
    private String mtcarpenter;


    @RequestMapping("/get")
    public String get() {
        return mtcarpenter;
    }


}