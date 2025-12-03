package com.cy.newpt.mapper;

import com.cy.newpt.pojo.entity.Stu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;

public interface StuMapper {
    /**
     * 根据name确认学生是否已经在数据库中
     * 其实这个nae没有索引，如果数量大了效率会很低，后期肯定要优化
     * @param name
     * @return
     */
    Integer ifStuExist(String name);

    /**
     * 查询所有的学生类，提供分页查询
     * @return
     */
    List<Stu> findAll();

    /**
     * 删除学生，根据account唯一索引来删除
     * (@Param) 将方法参数 account 映射到 SQL 中的 #{account} 占位符
     * @param account 学生账户
     * @return 返回一个证书，是否删除成功
     */
    Integer deleteByAccount(@Param("account") String account);

    /**
     * 更新学生信息
     * @param stuEntity 传入学生实体
     * @return
     */
    Integer updateByAccount(Stu stuEntity);

    /**
     * 根据账户信息查询学生类
     * @param account 学生账户
     * @return Stu实体对象
     */
    Stu findByAccount(String account);
}
