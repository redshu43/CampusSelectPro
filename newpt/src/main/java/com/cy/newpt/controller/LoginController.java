package com.cy.newpt.controller;

import com.cy.newpt.pojo.dto.ChangePasswordDTO;
import com.cy.newpt.pojo.dto.LoginDTO;
import com.cy.newpt.service.IAuthService;
import com.cy.newpt.service.impl.AuthServiceImpl;
import com.cy.newpt.utils.JsonResult;
import jakarta.validation.Valid;
import jdk.security.jarsigner.JarSigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @Autowired
    private IAuthService authService;

    @PostMapping("/login")
    public JsonResult<?> login(@Valid @RequestBody LoginDTO dto) {
        // ？是通配符，表示不关心它里面具体是什么类型，返回的时候指定数据类型。
        // 但是这时不能set(写入)，只能get(读取)

        // @Valid 会校验 LoginDTO 里的 @NotEmpty
        // @RequestBody 会把 JSON 转为 LoginDTO 对象
        try {
            // 调用service
            // service 会调查三张表，校验密码，生成token
            String token = authService.login(dto.getAccount(), dto.getPassword());

            // 登录成功，我们把生成的token作为data返回给前端
            // 前端会存储这个token
            return JsonResult.success("登录成功",token);
        } catch (Exception e) {
            // 登录失败
            // 返回401未授权状态码,并附带信息
            return JsonResult.failure(401, e.getMessage());
        }
    }

    @PostMapping("/changePassword")
    public JsonResult<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        try {
            // 1. 读取 Authentication 对象
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // 2. 获取账号
            String currentAccount = (String) auth.getPrincipal();

            // 3. 获取角色
            String currentRole = auth.getAuthorities().iterator().next().getAuthority();

            // 4. 传给 Service 层
            authService.changePassword(currentAccount, currentRole, dto);

            return JsonResult.success("密码修改成功");
        } catch (Exception e) {
            return JsonResult.failure(400, e.getMessage());
        }
    }
}
