package com.mtcarpenter.sentinel.cloud.view.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SentinelCloudApplicationTests {

    @Test
    public void contextLoads() throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 50000; i > 0; i--) {
            String forObject = restTemplate.getForObject("http://localhost:8081/test/echo/13", String.class);
            log.info("result = {}", forObject);
            TimeUnit.MILLISECONDS.sleep(600);
        }

    }

}
