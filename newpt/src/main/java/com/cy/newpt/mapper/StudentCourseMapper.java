package com.cy.newpt.mapper;

import com.cy.newpt.pojo.entity.Course;
import com.cy.newpt.pojo.entity.StudentCourse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentCourseMapper {
    //  插入选课记录
    int insert(StudentCourse sc);

    // 检车某个学生是否已经选某门课(用于java代码校验)
    int countByStudentIdAndCourseId(
            @Param("studentId") Integer studentId,
            @Param("courseId") Integer courseId
    );




    /**
     * 学生退课，删除学生和课程的联系
     * @param studentId
     * @param courseId
     * @return
     */
    int deleteByStudentIdAndCourseId(
            @Param("studentId") Integer studentId,
            @Param("courseId") Integer courseId
    );

    /**
     * 查询学生选课表
     * @param studentId
     * @return 返回学生选课表
     */
    List<Course> selectMyCourses(Integer studentId);

    /**
     * 获取某门课的所有选课学生账号
     * @param courseId
     * @return
     */
    List<String> findStudentAccountsByCourseId(Integer courseId);
}
