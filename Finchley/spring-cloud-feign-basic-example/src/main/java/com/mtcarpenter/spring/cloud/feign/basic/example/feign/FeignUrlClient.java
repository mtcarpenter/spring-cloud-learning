package com.mtcarpenter.spring.cloud.feign.basic.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@FeignClient(name = "url-client", url = "https://blog.lixc.top/spring-cloud.html")
public interface FeignUrlClient {

    /**
     * 地址请求
     *
     * @return
     */
    @GetMapping("")
    String cloud();
}
