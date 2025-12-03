package com.cy.newpt.service.impl;

import com.cy.newpt.mapper.AdminMapper;
import com.cy.newpt.mapper.StuMapper;
import com.cy.newpt.mapper.TeaMapper;
import com.cy.newpt.pojo.dto.ChangePasswordDTO;
import com.cy.newpt.pojo.dto.StuDTO;
import com.cy.newpt.pojo.entity.Admin;
import com.cy.newpt.pojo.entity.Role;
import com.cy.newpt.pojo.entity.Stu;
import com.cy.newpt.pojo.entity.Tea;
import com.cy.newpt.pojo.vo.StuVO;
import com.cy.newpt.service.IAuthService;
import com.cy.newpt.utils.JwtUtil;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {
    @Autowired
    private StuMapper stuMapper; // 学生数据库连接
    @Autowired
    private TeaMapper teaMapper; // 教师数据库连接
    @Autowired
    private AdminMapper adminMapper; // 管理员数据库连接
    @Autowired
    private PasswordEncoder passwordEncoder; // 密码校验器
    @Autowired
    private JwtUtil jwtUtil; // Token制造商

    @Override
    public String login(String account, String password) throws Exception {
        // （核心逻辑） 1. 查找用户
        String storedHash = null; // 数据库中存储的加密密码
        String userRole = null; // 用户角色 (例如 "ROLE_STUDENT")
        String userName = null;

// 以后肯定要优化成三个界面，学生，教师，管理员登录窗口。但是现在体量很小，不需要那么麻烦，一个个找就好了

        // 尝试在学生表中找
        Stu student = stuMapper.findByAccount(account);
        if (student != null) {
            storedHash = student.getPassword();
            userRole = student.getRole();
            userName= student.getName();
        } else {
            Tea teacher = teaMapper.findByAccount(account);
            if (teacher != null) {
                storedHash = teacher.getPassword();
                userRole = teacher.getRole();
                userName = teacher.getName();
            } else {
                Admin admin = adminMapper.findByAccount(account);
                if(admin != null) {
                    storedHash = admin.getPassword();
                    userRole = admin.getRole();
                    userName = "";
                } else {
                    // 三张表都没有找到
                    throw new Exception("用户不存在");
                }
            }
        }
        // 2. 校验密码
        if(!passwordEncoder.matches(password, storedHash)) {
            throw new Exception("密码错误");
        }
        // 3. 生成token
        return jwtUtil.generateToken(account, userRole, userName);
    }

    @Override
    public void changePassword(String account, String role, ChangePasswordDTO dto) throws Exception {
        // 1. 验证两次新密码是否一致
        if(!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new Exception("两次输入密码不一致");
        }

        // 2. 查找用户
        Object userEntity = null;
        String storeHash = null;
        if ("ROLE_STUDENT".equals(role)) {
            Stu student = stuMapper.findByAccount(account);
            if (student != null) {
                userEntity = student;
                storeHash = student.getPassword();
            }
        }else if ("ROLE_TEACHER".equals(role)) {
            Tea teacher = teaMapper.findByAccount(account);
            if (teacher != null) {
                userEntity = teacher;
                storeHash = teacher.getPassword();
            }
        } else if ("ROLE_ADMIN".equals(role)) {
            Admin admin = adminMapper.findByAccount(account);
            if (admin != null) {
                userEntity = admin;
                storeHash = admin.getPassword();
            }
        }

        if (userEntity == null) {
            throw new Exception("用户不存在,但是token有效，有点诡异啊");
        }

        // 3. 校验旧密码
        if(!passwordEncoder.matches(dto.getOldPassword(), storeHash)) {
            throw new Exception("旧密码错误");
        }
        // 4. 加密新密码
        String newHash = passwordEncoder.encode(dto.getNewPassword());

        if(userEntity instanceof Stu) {
            Stu stu = (Stu) userEntity;
            stu.setPassword(newHash);
            stuMapper.updateByAccount(stu);
        } else if (userEntity instanceof Tea) {
            Tea tea = (Tea) userEntity;
            tea.setPassword(newHash);
            teaMapper.updateByAccount(tea);
        } else if (userEntity instanceof Admin) {
            Admin admin = (Admin) userEntity;
            admin.setPassword(newHash);
            adminMapper.updateByAccount(admin);
        }
    }
}
