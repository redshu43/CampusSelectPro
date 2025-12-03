package com.cy.newpt.pojo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 登录请求JSON
 */
@Data
public class LoginDTO {
    @NotEmpty(message = "账号不能为空")
    private String account;
    @NotEmpty(message = "密码不能为空")
    private String password;
}
