package com.mtcarpenter.gateway.cloud.client.route.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayCloudClientRouteExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayCloudClientRouteExampleApplication.class, args);
    }

/*
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder){
        // 1 、简单路由
        return routeLocatorBuilder.routes()
                .route(r-> r.path("/route")
                        .uri("http://localhost:8090")
                        .id("path_route"))
                .build();
    }
*/

}
