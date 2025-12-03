package com.cy.newpt.pojo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 管理员创建学生时从前端需求的数据
 */
@Data
public class StuDTO {
    @NotEmpty(message = "姓名不能为空")
    private String name;
    private int gender;
    private int age;
    private int year;
    @NotEmpty(message = "账户不能为空")
    private String account;
    @NotEmpty(message = "密码不能为空")
    private String password;
    private int classIdt;
    private String majorId;
    private String depaId;
}
