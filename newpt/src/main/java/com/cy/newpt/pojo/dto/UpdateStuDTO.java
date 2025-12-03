package com.cy.newpt.pojo.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
/**
 * (DTO) 管理员修改学生信息时，用于接收前端请求的数据模型
 */
@Data
public class UpdateStuDTO {
    @Size(min = 0, max = 5, message = "名称长度不合法")
    private String name;
    private Integer gender;
    private Integer age;
    private Integer year;
    private Integer classIdt;
    private String majorId;
    private String depaId;
    private String password;
}
