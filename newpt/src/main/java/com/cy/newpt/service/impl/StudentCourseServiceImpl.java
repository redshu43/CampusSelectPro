package com.cy.newpt.service.impl;

import com.cy.newpt.config.RabbitConfig;
import com.cy.newpt.mapper.CourseMapper;
import com.cy.newpt.mapper.StuMapper;
import com.cy.newpt.mapper.StudentCourseMapper;
import com.cy.newpt.pojo.dto.CourseSelectionMessage;
import com.cy.newpt.pojo.entity.Course;
import com.cy.newpt.pojo.entity.Stu;
import com.cy.newpt.pojo.entity.StudentCourse;
import com.cy.newpt.service.IStudentCourseService;
import com.cy.newpt.utils.RedisUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentCourseServiceImpl implements IStudentCourseService {
    @Autowired
    private StuMapper stuMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private StudentCourseMapper studentCourseMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 选课方法，但是这里并不选课，而是将选课信息打包，发送给消息队列，然后返回前端，实现异步
     * @param account 学生账户
     * @param courseId 课程id
     * @throws Exception
     */
    @Override
    public void selectCourse(String account, Integer courseId) throws Exception {

        // 定义 Redis Key
        String stockKey = "course:stock:" + courseId;
        String setKey = "course:selected:" + courseId;

        if (redisUtil.sIsMember(setKey, account)) {
            throw new Exception("你已经选过这门课了(Redis拦截)");
        }

        if (!redisUtil.hasKey(stockKey)) {
            throw new Exception("当前课程未开放选课(未预热)");
        }
        // 扣减库存
        Long currentStock = redisUtil.decrement(stockKey);
        if (currentStock < 0) {
            // 库存不足
            redisUtil.increment(stockKey);
            throw new Exception("手慢了，课程已抢光");
        }

        // 发送消息给 MQ(异步)
        CourseSelectionMessage msg = new CourseSelectionMessage();
        msg.setAccount(account);
        msg.setCourseId(courseId);

        // 发送
        rabbitTemplate.convertAndSend(RabbitConfig.SELECTION_QUEUE, msg);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dropCourse(String account, Integer courseId) throws Exception {
        // 1. 获取id
        Stu student = stuMapper.findByAccount(account);
        if (student == null) throw new Exception("账号异常");
        Integer studentId = student.getId();

        // 2. 删除选课记录
        int rows = studentCourseMapper.deleteByStudentIdAndCourseId(studentId, courseId);
        if (rows == 0) {
            throw new Exception("你并未选这门课，无法退课");
        }

        // 3. 恢复选课库存
        courseMapper.decreaseSelectedNum(courseId);

        String setKey = "course:selected:" + courseId;
        String stockKey = "course:stock:" + courseId;

        // A. 把学生从“已选名单”里踢出去
        redisUtil.sRemove(setKey, account);

        // B. 把“剩余库存”加回来 (+1)
        redisUtil.increment(stockKey);
    }

    @Override
    public List<Course> getMyCourseList(String account) {
        Stu student = stuMapper.findByAccount(account);
        if (student == null) return new ArrayList<>();

        return studentCourseMapper.selectMyCourses(student.getId());
    }
}
