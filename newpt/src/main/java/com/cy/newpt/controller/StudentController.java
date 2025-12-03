package com.cy.newpt.controller;

import com.cy.newpt.pojo.vo.StuVO;
import com.cy.newpt.service.StuService;
import com.cy.newpt.service.impl.StuServiceImpl;
import com.cy.newpt.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private StuService stuService;

    @GetMapping("/me")
    public JsonResult<StuVO> getMyInfo() {
        // 从安全上下文获取当前登录用户的 account
        // (这是 JwtRequestFilter 放进去的)
        String cuttentAccount = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 拿这个account去service查询信息
        try {
            StuVO myInfo = stuService.getStudentInfoByAccount(cuttentAccount);

            return JsonResult.success("获取成功", myInfo);
        } catch (Exception e) {
            return JsonResult.failure(500, "获取信息失败" + e.getMessage());
        }
    }
}
