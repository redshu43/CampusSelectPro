package com.cy.newpt.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 选课消息对象，放入MQ
 */
@Data
public class CourseSelectionMessage implements Serializable {
    private String account; // 是谁
    private Integer courseId; // 课程
}
