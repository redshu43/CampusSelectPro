package com.cy.newpt.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateCourseDTO {
    @NotEmpty(message = "课程名称不能为空")
    private String name;

    @NotNull(message = "学分不能为空")
    @Min(value = 1, message = "学分至少为1")
    private Integer score;

    @NotNull(message = "容量不能为空")
    @Min(value =  1, message = "容量至少为1")
    private Integer capacity;

    // 可以为空， 为空代表立即开始/管理员未定
    // GMT+8: 格林威治标准时间+8h，也即标准北京时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
    @NotEmpty(message = "上课周次不能为空")
    private String weekDay;
    @NotEmpty(message = "上课节次不能为空")
    private String sectionTime;
}
