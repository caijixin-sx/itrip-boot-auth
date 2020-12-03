package com.boot.itripbootauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.controller","com.service","com.utils"})
@MapperScan(basePackages = {"com.mapper"})
public class ItripBootAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItripBootAuthApplication.class, args);
    }

}
