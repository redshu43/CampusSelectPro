package com.cy.newpt.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter; // 引入这个
import org.springframework.amqp.support.converter.MessageConverter; // 引入这个
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    // 定义队列名称
    public static final String SELECTION_QUEUE = "course_selection_queue";

    // 创建一个持久化队列
    @Bean
    public Queue selectionQueue() {

        return new Queue(SELECTION_QUEUE, true);
    }

    // (新增) 配置 JSON 转换器
    // 加上这个 Bean，Spring 就会自动把对象转成 JSON 发送，把 JSON 转回对象接收
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
