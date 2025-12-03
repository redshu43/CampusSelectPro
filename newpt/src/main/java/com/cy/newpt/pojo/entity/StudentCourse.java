package com.cy.newpt.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentCourse {
    private Integer id;
    private Integer studentId; // 学生id
    private Integer courseId; // 选课id
    private LocalDateTime createTime; // 选课时间
}
