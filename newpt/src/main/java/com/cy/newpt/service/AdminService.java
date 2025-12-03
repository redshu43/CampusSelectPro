package com.cy.newpt.service;

import com.cy.newpt.pojo.entity.Stu;
import com.cy.newpt.pojo.entity.Tea;
import com.cy.newpt.pojo.vo.StuVO;

public interface AdminService {
    /**
     * 注册学生
     * @param stu
     */
    void regStu(Stu stu);

    /**
     * 注册教师
     * @param tea
     */
    void regTea(Tea tea);

}
