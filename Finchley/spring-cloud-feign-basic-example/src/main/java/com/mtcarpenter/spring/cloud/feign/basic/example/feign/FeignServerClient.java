package com.mtcarpenter.spring.cloud.feign.basic.example.feign;

import com.mtcarpenter.spring.cloud.feign.basic.example.config.FeignServerConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */

//@FeignClient(name="feign-server",path = "feign",configuration = FeignServerConfig.class)
@FeignClient(name = "feign-server", path = "feign")
public interface FeignServerClient {
    /**
     * 请求服务端接口 http://feign-server/feign/say/{name}
     *
     * @param name
     * @return
     */
    @GetMapping("/say/{name}")
    String sayHello(@PathVariable("name") String name);
}
