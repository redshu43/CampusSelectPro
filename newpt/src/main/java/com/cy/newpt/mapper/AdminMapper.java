package com.cy.newpt.mapper;

import com.cy.newpt.pojo.entity.*;
import com.cy.newpt.pojo.entity.ClassInfo;
import org.springframework.stereotype.Component;

public interface AdminMapper {
    /**
     * 通过账号查询管理员信息
     * @param account 管理员账号
     * @return 管理员实体
     */
    Admin findByAccount(String account);

    /**
     * 跟新管理员信息
     * @param admin 管理员实体
     * @return 一个数字，是否更新成功
     */
    Integer updateByAccount(Admin admin);

    /**
     * 插入一条学生信息
     * @param stu 学生类
     * @return 是否插入成功
     */
    Integer insertStu (Stu stu);

    /**
     * 插入一条教师信息
     * @param tea 教师类
     * @return 是否插入成功
     */
    Integer insertTea (Tea tea);

    /**
     * 插入课程信息
     * @param cou
     * @return
     */
    Integer insertCourse (Course cou);

    /**
     * 创建系信息表
     * @param dept
     * @return
     */
    Integer insertDepartment (Department dept);

    /**
     * 创建专业信息
     * @param major
     * @return
     */
    Integer insertMajor (Major major);

    /**
     * 插入班级信息表
     * @param c
     * @return
     */
    Integer insertClass (ClassInfo c);




}
