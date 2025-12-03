package com.cy.newpt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cy.newpt.mapper")
public class  NewptApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewptApplication.class, args);
    }

}
