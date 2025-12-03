package com.cy.newpt.service;

import com.cy.newpt.pojo.dto.ChangePasswordDTO;
import com.cy.newpt.pojo.entity.Role;
import com.cy.newpt.pojo.vo.StuVO;

/**
 * 认证服务接口
 * 专门处理登录，注册等安全相关的操作
 */
public interface IAuthService {

    /**
     * 登录
     * @param account 登录账号
     * @param password 登录密码
     * @return JWT token 字符串
     * @throws Exception 登录失败（用户不存在或者密码错误）
     */
    String login(String account, String password) throws Exception;

    /**
     * 修改密码
     * @param account 账户
     * @param dto 后端视图
     * @throws Exception
     */
    void changePassword(String account, String role, ChangePasswordDTO dto) throws Exception;
}
