package com.cy.newpt.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SelectCourseDTO {
    @NotNull(message = "课程id不能为空")
    private Integer courseId;
}
