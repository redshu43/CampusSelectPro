package com.cy.newpt.pojo.vo;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TeaVO {
    private String name;
    private int gender;
    private int age;
    private String edu;
    private String title;
    private String account;
}
