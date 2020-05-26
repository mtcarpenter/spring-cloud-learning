package com.mtcarpenter.stream.rabbitmq.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringCloudStreamRabbitmqExampleApplicationTests {

    @Autowired
    private StreamBinding streamBinding;

    @Test
    public void contextLoads() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("book", new Book(3, "百科图书", "mtcarpenter"));
        MessageHeaders mhs = new MessageHeaders(properties);
        Message msg = MessageBuilder.createMessage("book order ", mhs);
        streamBinding.output().send(msg);
    }

}
