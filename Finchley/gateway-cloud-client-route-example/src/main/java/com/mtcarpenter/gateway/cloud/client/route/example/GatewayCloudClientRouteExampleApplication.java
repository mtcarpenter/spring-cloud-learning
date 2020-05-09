package com.mtcarpenter.gateway.cloud.client.route.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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

/*
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        ZonedDateTime datetime = LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault());
        return builder.routes()
                .route("path_route_before", r -> r.before(datetime)
                        .uri("http://blog.lixc.top"))
                .build();
    }

    */


}
