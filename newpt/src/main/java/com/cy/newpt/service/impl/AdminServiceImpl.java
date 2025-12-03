package com.cy.newpt.service.impl;

import com.cy.newpt.mapper.AdminMapper;
import com.cy.newpt.mapper.StuMapper;
import com.cy.newpt.mapper.TeaMapper;
import com.cy.newpt.pojo.entity.Stu;
import com.cy.newpt.pojo.entity.Tea;
import com.cy.newpt.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private StuMapper stuMapper;

    @Autowired
    private TeaMapper teaMapper;

    // 这一块查询都可以简化，用同一个类表示，用Role限定
    @Override
    public void regStu(Stu stu) {
        String name = stu.getName();
        int ret = stuMapper.ifStuExist(name);
        if (ret == 0) {
            int af = adminMapper.insertStu(stu);
            if (af == 0) {
                throw new RuntimeException("学生信息创建失败");
            }
        } else {
            throw new RuntimeException("学生信息已经存在");
        }
    }

    @Override
    public void regTea(Tea tea) {
        String name = tea.getName();
        int ret = teaMapper.ifTeaExist(name);
        if (ret == 0) {
            int af = adminMapper.insertTea(tea);
            if (af == 0) {
                throw new RuntimeException("教师信息创建失败");
            }
        } else {
            throw new RuntimeException("教师信息已经存在");
        }
    }
}
