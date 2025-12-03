package com.cy.newpt.service.impl;

import com.cy.newpt.mapper.CourseMapper;
import com.cy.newpt.pojo.dto.CreateCourseDTO;
import com.cy.newpt.pojo.entity.Course;
import com.cy.newpt.pojo.vo.PageVO;
import com.cy.newpt.service.ICourseService;
import com.cy.newpt.utils.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class CourseServiceImpl implements ICourseService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public void createCourse(CreateCourseDTO dto) throws Exception {
        // 1. 第一道防线： java预检查，可以给前端返回一个友好的提示
        Course exist = courseMapper.findByName(dto.getName());
        if (exist != null) {
            throw new Exception("此课程已经存在");
        }

        // 2. 时间校验
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            if (dto.getStartTime().isAfter(dto.getEndTime())){
                throw new Exception("课程结束时间不能早于开始时间");
            }
        }

        // 3. 转换并保存
        Course course = new Course();
        BeanUtils.copyProperties(dto, course);
        course.setSelectedNum(0);
        course.setVersion(1);

        try{
            // 4. 第二道防线： 数据库检查
            // 也就是说，如果并发太快，两个线程都超过的第一步，数据库会在这里抛出 DuplicateKeyException， 阻止第二条数据的插入
            courseMapper.insert(course);
        } catch (DuplicateKeyException e) {
            throw new Exception("课程已经存在(DB拦截)");
        }
    }

    @Override
    public PageVO<Course> getCoursePage(int pageNum, int pageSize) {
        // 1. 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 2. 查询
        List<Course> list = courseMapper.findAll();

        // 3. 包装
        PageInfo<Course> pageInfo = new PageInfo<>(list);

        // 4. 转vo视图
        PageVO<Course> pageVO = new PageVO<>();
        pageVO.setPageNum(pageInfo.getPageNum());
        pageVO.setPageSize(pageInfo.getPageSize());
        pageVO.setTotal(pageInfo.getTotal());
        pageVO.setPages(pageInfo.getPages());
        pageVO.setList(pageInfo.getList());

        return pageVO;
    }

    @Override
    public void deleteCourse(Integer id) {
        courseMapper.deleteById(id);
    }

    @Override
    public void updateCourse(Integer id, CreateCourseDTO dto) {
        // 1. 准备实体
        Course course = new Course();
        BeanUtils.copyProperties(dto, course);
        course.setId(id);

        // 2. 更新
        courseMapper.update(course);
    }
}
