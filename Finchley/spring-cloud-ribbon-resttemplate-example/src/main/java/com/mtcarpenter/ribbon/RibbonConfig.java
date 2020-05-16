package com.mtcarpenter.ribbon;

import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@Configuration
public class RibbonConfig {

    @Bean
    public IRule iRule(){
        return new RandomRule();
    }

    @Bean
    public IPing ribbonPing() {
        return new PingUrl();
    }
}
