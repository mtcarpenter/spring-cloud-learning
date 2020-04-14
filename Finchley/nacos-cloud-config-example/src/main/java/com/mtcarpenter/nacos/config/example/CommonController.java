
package com.mtcarpenter.nacos.config.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
@RefreshScope
public class CommonController {

    @Value("${mtcarpenter.comon:}")
    private String common;

    @RequestMapping("/common")
    public String common() {
        return common;
    }

}