package com.mtcarpenter.gateway.cloud.client.route.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GatewayCloudClientRouteExampleApplicationTests {

    @Test
    public void contextLoads() {
        ZonedDateTime zonedDateTime = LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault());
        System.out.println(zonedDateTime);
    }

}
