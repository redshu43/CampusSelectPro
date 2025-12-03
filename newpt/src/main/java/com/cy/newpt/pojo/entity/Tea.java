package com.cy.newpt.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.StringReader;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tea {
    private int id;
    private String name;
    private int gender;
    private int age;
    private String edu;
    private String title;
    private Integer hireDate;
    private String account;
    private String password;
    private String role;
}
