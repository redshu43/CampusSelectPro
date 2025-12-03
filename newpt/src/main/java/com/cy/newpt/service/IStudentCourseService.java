package com.cy.newpt.service;

import com.cy.newpt.pojo.entity.Course;

import java.util.Collections;
import java.util.List;

public interface IStudentCourseService {
    /**
     * 学生选课
     * @param account 学生账户
     * @param courseId 课程id
     * @throws Exception 异常
     */
    void selectCourse(String account, Integer courseId) throws Exception;

    /**
     * 学生退课
     * @param account
     * @param courseId
     * @throws Exception
     */
    void dropCourse(String account, Integer courseId) throws Exception;

    /**
     * 返回学生已选课列表
     * @param account
     * @return
     */
    List<Course> getMyCourseList(String account);
}
