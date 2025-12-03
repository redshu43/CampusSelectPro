package com.cy.newpt.pojo.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class StuVO {
    private String name;
    private Integer gender;
    private Integer age;
    private Integer classIdt;
    private String majorId;
    private String depaId;
    private String account;
}
