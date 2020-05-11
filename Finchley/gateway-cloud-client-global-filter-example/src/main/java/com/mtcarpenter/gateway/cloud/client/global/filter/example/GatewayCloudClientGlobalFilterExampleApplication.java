package com.mtcarpenter.gateway.cloud.client.global.filter.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayCloudClientGlobalFilterExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayCloudClientGlobalFilterExampleApplication.class, args);
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes().route(r ->
                r.path("/route")
                        //转发路由
                        .uri("https://example.org")
                        //注册自定义过滤器
                        .filters(new RequestTimeFilter())
                        //给定id
                        .id("filter_route"))
                .build();
    }




}
