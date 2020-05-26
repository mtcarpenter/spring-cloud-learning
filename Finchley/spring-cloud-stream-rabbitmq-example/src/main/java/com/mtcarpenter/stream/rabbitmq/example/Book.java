package com.mtcarpenter.stream.rabbitmq.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book implements Serializable {


    private static final long serialVersionUID = 3679570604506079906L;
    private int id;
    private String name;
    private String author;

}