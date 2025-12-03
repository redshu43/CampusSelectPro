package com.cy.newpt.pojo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDTO {
    @NotEmpty(message = "旧密码不能为空")
    private String oldPassword;
    @NotEmpty(message = "新密码不能为空")
    @Size(min = 6, message = "新密码至少为6位")
    private String newPassword;
    @NotEmpty(message = "确认密码不能为空")
    private String confirmPassword;
}

