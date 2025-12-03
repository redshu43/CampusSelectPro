package com.cy.newpt.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Course implements Serializable {
    private Integer id;
    private String name; // 课程名
    private Integer teacherId; // 授课老师id
    private Integer score; // 学分
    private Integer capacity; // 容量（一堂课人数）
    private Integer selectedNum; // 已选人数
    private Integer version; // 乐观锁版本号

    // 开课-结课时间
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // 每周上课时间安排
    private String weekDay; // "周三" 或 "3"
    private String sectionTime; // "14:00-16:00" 或 "5-6节"
}
