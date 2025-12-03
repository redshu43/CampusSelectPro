package com.cy.newpt.service.impl;

import com.cy.newpt.mapper.AdminMapper;
import com.cy.newpt.mapper.TeaMapper;
import com.cy.newpt.pojo.dto.TeaDTO;
import com.cy.newpt.pojo.entity.Role;
import com.cy.newpt.pojo.entity.Tea;
import com.cy.newpt.pojo.vo.TeaVO;
import com.cy.newpt.service.TeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.time.temporal.TemporalAccessor;

@Service
public class TeaServiceImpl implements TeaService {

    @Autowired
    private TeaMapper teaMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public TeaVO createTea(TeaDTO dto) throws Exception {
        //1.校验
        if (teaMapper.ifTeaExist(dto.getName()) != 0) {
            throw new Exception("教师已经存在");
        }
        //2. 持久层转化
        Tea tea = new Tea();
        tea.setName(dto.getName());
        tea.setGender(dto.getGender());
        tea.setEdu(dto.getEdu());
        tea.setTitle(dto.getTitle());
        tea.setHireDate(Year.now().getValue());
        tea.setAccount(dto.getAccount());

        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        tea.setPassword(hashedPassword);

        tea.setRole("ROLE_TEACHER");

        System.out.println(tea);
        adminMapper.insertTea(tea);

        //3. 返回VO视图
        TeaVO teaVO = new TeaVO();
        teaVO.setName(tea.getName());
        teaVO.setGender(tea.getGender());
        teaVO.setAge(tea.getAge());
        teaVO.setEdu(tea.getEdu());
        teaVO.setTitle(tea.getTitle());
        teaVO.setAccount(tea.getAccount());

        return teaVO;
    }
}
