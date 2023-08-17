package com.capol.notify.producer.port.adapter.enviroment;

import com.capol.notify.manage.application.queue.QueueMQService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 自动批量创建消息队列
 */
@Slf4j
@Component
@Order(1)
public class QueueRunner implements CommandLineRunner {

    private final QueueMQService queueMQService;
    private final RabbitProperties rabbitProperties;

    public QueueRunner(QueueMQService queueMQService, RabbitProperties rabbitProperties) {
        this.queueMQService = queueMQService;
        this.rabbitProperties = rabbitProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("-------自动创建队列到消息服务中--------");
            log.debug("-->消息服务器:{} ,端口号：{} ,虚拟主机:{}", rabbitProperties.getHost(), rabbitProperties.getPort(), rabbitProperties.getVirtualHost());
        }
        queueMQService.registrationQueue();
        queueMQService.deleteQueue();
        if (log.isDebugEnabled()) {
            log.debug("-------自动创建队列到消息服务完成--------");
        }
    }
}
