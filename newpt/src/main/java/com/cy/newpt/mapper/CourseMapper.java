package com.cy.newpt.mapper;

import com.cy.newpt.pojo.entity.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CourseMapper {
    /**
     * 新建课程
     * @param course 课程实体类
     * @return
     */
    int insert(Course course);

    /**
     * 手动查重，根据名称查询课程
     * @param name 课程名
     * @return 课程实体
     */
    Course findByName(String name);

    /**
     * 查询所有课程
     * @return
     */
    List<Course> findAll();

    /**
     * 根据id删除课程
     * @param id
     * @return
     */
    int deleteById(Integer id);

    /**
     * 更新课程（动态SQL）
     * @param course
     * @return
     */
    int update(Course course);

    /**
     * 恢复库存（学生退课）
     * @param id
     * @return
     */
    int decreaseSelectedNum(Integer id);

    // 增加已选人数（返回受影响的行数）
    int increaseSelectedNum(@Param("id") Integer id);
}
