package com.capol.notify.consumer.domain.model.message;

import com.capol.notify.manage.domain.EnumProcessStatusType;
import com.capol.notify.manage.domain.TTLQueueConfig;
import com.capol.notify.sdk.MessageReceiveConfirmCallback;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 过期消息队列
 */
@Slf4j
@Component
public class TTLMessageConsumer {
    private MessageReceiveConfirmCallback messageReceiveConfirmCallback;

    public TTLMessageConsumer(MessageReceiveConfirmCallback messageReceiveConfirmCallback) {
        this.messageReceiveConfirmCallback = messageReceiveConfirmCallback;
    }

    /**
     * 监听死信队列名称
     *
     * @param payload
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = {TTLQueueConfig.QUEUE_DEAD})
    public void dead(String payload, Message message, Channel channel) throws IOException {
        log.warn("-->死信队列收到数据:{}", payload);
        //消息消费回调修改消息状态
        if (messageReceiveConfirmCallback != null) {
            messageReceiveConfirmCallback.receiveConfirmCallback(false, Long.valueOf(message.getMessageProperties().getMessageId()), new Exception("消息已经过期,已经进入死信队列后处理的消息!"));
        }
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        // deliveryTag：一个整数，表示消息的投递标识。每个消息都有一个唯一的投递标识，在消费者接收消息时会从消息的属性中获取。
        // multiple：一个布尔值，表示是否批量确认。如果设置为true，将确认所有小于等于deliveryTag的未确认消息。
        // 如果设置为false，只确认当前deliveryTag指定的消息。
        channel.basicAck(deliveryTag, true);
        log.info("-->死信消息状态更改为:{}({})", EnumProcessStatusType.FAILURE.getCode(), EnumProcessStatusType.FAILURE.getDesc());
    }
}
