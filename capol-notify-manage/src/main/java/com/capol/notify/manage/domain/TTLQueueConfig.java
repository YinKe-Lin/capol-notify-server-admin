package com.capol.notify.manage.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 列信队列配置信息
 *
 * @author heyong
 */
@Slf4j
@Configuration
public class TTLQueueConfig {
    /**
     * 死信交换机的名字
     */
    public final static String EXCHANGE_DEAD = "capol_notify_dead_exchange";
    /**
     * 死信队列名称
     */
    public final static String QUEUE_DEAD = "capol_notify_dead_queue";
    /**
     * 死信路由
     */
    public final static String DEAD_ROUTING = "capol.notify.dead.routing";

    /**
     * 获取死信的配置信息
     **/
    public static Map<String, Object> getDeadQueueArgs() {
        Map<String, Object> args = new HashMap<>(16);
        //死信交换器名称，过期或被删除（因队列长度超长或因空间超出阈值）的消息可指定发送到该交换器中；
        args.put("x-dead-letter-exchange", EXCHANGE_DEAD);
        //死信消息路由键，在消息发送到死信交换器时会使用该路由键，如果不设置，则使用消息的原来的路由键值
        args.put("x-dead-letter-routing-key", DEAD_ROUTING);
        return args;
    }

    /**
     * 创建死信队列
     *
     * @return
     */
    @Bean(QUEUE_DEAD)
    public Queue queueDead() {
        log.info("-->创建列信队列:{}", QUEUE_DEAD);
        return new Queue(QUEUE_DEAD, true, false, false);
    }

    /**
     * 创建死信交换机
     *
     * @return
     */
    @Bean(EXCHANGE_DEAD)
    public Exchange exchangeDead() {
        log.info("-->创建列信队列交换机:{}", EXCHANGE_DEAD);
        return ExchangeBuilder.topicExchange(EXCHANGE_DEAD)
                //durable持久化 mq重启后数据还在
                .durable(true)
                .build();
    }

    /**
     * 绑定死信队列和死信交换机
     *
     * @return
     */
    @Bean
    public Binding deadBinding() {
        log.info("-->绑定死信队列:{}和死信交换机:{}", QUEUE_DEAD, EXCHANGE_DEAD);
        return BindingBuilder.bind(queueDead())
                .to(exchangeDead())
                //路由规则 正常路由key
                .with(DEAD_ROUTING)
                .noargs();
    }
}
