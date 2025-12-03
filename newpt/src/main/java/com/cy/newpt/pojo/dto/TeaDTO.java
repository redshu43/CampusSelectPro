package com.cy.newpt.pojo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员创建教师时前端需要返回的数据
 */
@Data
public class TeaDTO {
    @NotEmpty(message = "姓名不能为空")
    private String name;
    private int age;
    private int gender;
    private String edu;
    private String title;
    @NotEmpty(message = "账户不能为空")
    private String account;
    @NotEmpty(message = "密码不能为空")
    private String password;
}
