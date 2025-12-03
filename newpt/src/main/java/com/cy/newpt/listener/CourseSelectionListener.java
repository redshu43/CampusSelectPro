package com.cy.newpt.listener;

import com.cy.newpt.config.RabbitConfig;
import com.cy.newpt.mapper.CourseMapper;
import com.cy.newpt.mapper.StudentCourseMapper;
import com.cy.newpt.mapper.StuMapper;
import com.cy.newpt.pojo.dto.CourseSelectionMessage;
import com.cy.newpt.pojo.entity.Stu;
import com.cy.newpt.pojo.entity.StudentCourse;
import com.cy.newpt.utils.RedisUtil;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CourseSelectionListener {
    @Autowired
    private StuMapper stuMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private StudentCourseMapper studentCourseMapper;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 消费者，监听选课队列，有一个订单就拿一个，一直做
     * 这是一个独立运行的线程，和主线程不在同一个线程中，始终监听队列
     * @param msg
     * @param channel
     * @param tag
     */
    @RabbitListener(queues = RabbitConfig.SELECTION_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void handleMessage(CourseSelectionMessage msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        String account = msg.getAccount();
        Integer courseId = msg.getCourseId();

        try {
            System.out.println("收到选课消息：" + account + " 选课程 " + courseId);

            // 1. 获取学生ID
            Stu student = stuMapper.findByAccount(account);
            if (student == null) {
                // 账号异常，直接确认消息(消费掉)，避免死循环
                channel.basicAck(tag, false);
                return;
            }

            // 2. 【DB查重】双重保险
            int count = studentCourseMapper.countByStudentIdAndCourseId(student.getId(), courseId);
            if (count > 0) {
                // 已经选过了（可能是Redis数据丢失导致的重复消息），直接确认
                channel.basicAck(tag, false);
                return;
            }

            // 3. 【DB扣库存】
            int rows = courseMapper.increaseSelectedNum(courseId);
            if (rows == 0) {
                // 库存不足（理论上Redis挡住了不该发生，但为了兜底），把Redis库存加回去
                redisUtil.increment("course:stock:" + courseId);
                channel.basicAck(tag, false);
                return;
            }

            // 4. 【DB写记录】
            StudentCourse sc = new StudentCourse();
            sc.setStudentId(student.getId());
            sc.setCourseId(courseId);
            sc.setCreateTime(LocalDateTime.now());
            studentCourseMapper.insert(sc);

            // 5. 【Redis Set】写入已选名单
            redisUtil.sAdd("course:selected:" + courseId, account);

            // 并且告诉前端成功（1）
            String resultKey = "course:result:" + account + ":" + courseId;
            redisUtil.set(resultKey, "1", 300); // 1 代表成功

            // 6. 手动确认消息 (告诉 MQ：我处理完了，这条消息可以删了)
            channel.basicAck(tag, false);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                // 处理失败，回滚 Redis 库存
                redisUtil.increment("course:stock:" + courseId);
                // 拒绝消息 (requeue=false: 不重回队列，丢弃或进死信队列；true: 重试)
                // 这里为了简单，我们选择不重试，避免死循环阻塞队列
                // 在redis下留下失败的字条
                if (msg != null) {
                    String resultKey = "course:result:" + msg.getAccount() + ":" + msg.getCourseId();
                    redisUtil.set(resultKey, "-1", 300); // -1 代表失败
                }

                channel.basicNack(tag, false, false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
