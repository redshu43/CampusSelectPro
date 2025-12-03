package com.cy.newpt.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Data
public class Stu implements Serializable {
    private int id;
    private String name;
    private Integer gender;
    private Integer age;
    private Integer year;
    private Integer classIdt;
    private String majorId;
    private String depaId;
    private String role;
    private String account;
    private String password;
}
