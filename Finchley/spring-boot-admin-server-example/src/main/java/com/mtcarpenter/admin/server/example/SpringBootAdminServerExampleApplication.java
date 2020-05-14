package com.mtcarpenter.admin.server.example;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class SpringBootAdminServerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAdminServerExampleApplication.class, args);
    }

}
