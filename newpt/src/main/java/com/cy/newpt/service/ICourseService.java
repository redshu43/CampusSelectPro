package com.cy.newpt.service;

import com.cy.newpt.pojo.dto.CreateCourseDTO;
import com.cy.newpt.pojo.entity.Course;
import com.cy.newpt.pojo.vo.PageVO;

public interface ICourseService {
    /**
     * 创建课程
     * @param dto 课程的后端视图
     * @throws Exception
     */
    void createCourse(CreateCourseDTO dto) throws Exception;

    /**
     * 分页查询/返回课程信息
     * @param pageNum 页码
     * @param pageSize 容量/大小
     * @return
     */
    PageVO<Course> getCoursePage(int pageNum, int pageSize);

    /**
     * 根据id删除课程
     * @param id
     */
    void deleteCourse(Integer id);

    /**
     * 更新/修改课程元素
     * @param id
     * @param dto
     */
    void updateCourse(Integer id, CreateCourseDTO dto);
}
