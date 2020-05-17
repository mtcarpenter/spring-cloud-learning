package com.mtcarpenter.spring.cloud.feign.basic.example.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;


/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
//如果使用添加了 @Configuration  需要将此配置类移到启动类之外的包（避免@ComponentScan） 避免父子上下文重复扫描
public class FeignServerConfig {

    @Bean
    public Logger.Level logger() {
        // 请求的详细信息
        return Logger.Level.FULL;
    }
}
