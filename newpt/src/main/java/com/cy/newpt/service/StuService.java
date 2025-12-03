package com.cy.newpt.service;

import com.cy.newpt.pojo.dto.StuDTO;
import com.cy.newpt.pojo.dto.UpdateStuDTO;
import com.cy.newpt.pojo.entity.Stu;
import com.cy.newpt.pojo.vo.PageVO;
import com.cy.newpt.pojo.vo.StuVO;

/**
 * 学生模块业务层接口
 */
public interface StuService {
    /**
     * 创建学生
     * @param dto 前端返回的学生信息
     * @return VO，创建成功后学生试图
     * @throws Exception 异常，学生已经存在以及其他
     */
    StuVO createStu(StuDTO dto) throws Exception;

    /**
     * 获取学生页列表
     * @param pageNum 当前页码
     * @param pageSize 每条页数
     * @return 学生列表视图
     * @throws Exception
     */
    PageVO<StuVO> getStudentPage(int pageNum, int pageSize) throws Exception;

    /**
     * 根据学生账户删除学生信息
     * @param count 学生账户
     * @throws Exception 异常处理，包括次学生不存在
     */
    void deleteStudentByAccount(String count) throws Exception;

    /**
     * 根据 Account 更新学生的信息
     * @param account 需要更新的学生账号
     * @param dto 包含新信息的dto层
     * @return 更新后的vo，前端立刻刷新
     * @throws Exception 异常处理
     */
    StuVO updateStudentByAccount(String account, UpdateStuDTO dto) throws Exception;

    /**
     * 获取学生信息
     * @param account 学生账户
     * @return 返回给前端信息
     */
    StuVO getStudentInfoByAccount(String account);
}
