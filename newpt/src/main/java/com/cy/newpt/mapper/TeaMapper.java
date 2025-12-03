package com.cy.newpt.mapper;

import com.cy.newpt.pojo.entity.Tea;
import org.springframework.stereotype.Component;

public interface TeaMapper {

    /**
     * 教师是否已经存在
     * @param name 教师类
     * @return 存在则1不存在则0
     */
    Integer ifTeaExist(String name);

    /**
     * 根据账号查询教师
     * @param account 账号
     * @return 返回教师类
     */
    Tea findByAccount(String account);

    /**
     * 跟新教师信息
     * @param tea
     * @return
     */
    Integer updateByAccount(Tea tea);
}
