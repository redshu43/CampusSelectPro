package com.cy.newpt.service;

import com.cy.newpt.pojo.dto.TeaDTO;
import com.cy.newpt.pojo.vo.TeaVO;

public interface TeaService {
    /**
     * 创建教师类
     * @param dto 前端返回的教师类基本信息
     * @return VO，教师类创建成功的视图
     * @throws Exception 异常，教师已经存在以及其他
     */
    TeaVO createTea(TeaDTO dto) throws Exception;
}
