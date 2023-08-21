package com.capol.notify.producer.domain.model.message;

import com.capol.notify.manage.application.queue.QueueService;
import com.capol.notify.manage.application.user.querystack.UserQueueDTO;
import com.capol.notify.manage.domain.DomainException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.manage.domain.model.message.MQMessageSave;
import com.capol.notify.sdk.command.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 消息生产者
 *
 * @author heyong
 * @since 2023-08-02
 */
@Slf4j
@Component
public class MessageProducer {

    private MessagePublisher messagePublisher;
    private QueueService queueService;

    public MessageProducer(MessagePublisher messagePublisher, QueueService queueService) {
        this.messagePublisher = messagePublisher;
        this.queueService = queueService;
    }

    /**
     * 发送微信消息
     *
     * @param weChatMsgCommand
     */
    @MQMessageSave(argsIndex = 0)
    public void sendWeChatMsg(WeChatMsgCommand weChatMsgCommand) {
        UserQueueDTO userQueueDTO = queueService.getUserQueueByIdAndType(weChatMsgCommand.getUserId(),
                weChatMsgCommand.getBusinessType());
        if (userQueueDTO != null) {
            messagePublisher.messageSender(weChatMsgCommand,
                    userQueueDTO.getQueue(),
                    weChatMsgCommand.getPriority(),
                    weChatMsgCommand.getMessageId().toString(),
                    (weChatMsgCommand.getTtl() != null && weChatMsgCommand.getTtl() > 0) ? weChatMsgCommand.getTtl() : userQueueDTO.getTtl());
        } else {
            log.error("微信消息发送失败, 用户:{} 业务类型:{} 配置的队列不存在或已经被禁用!",
                    weChatMsgCommand.getUserId(),
                    weChatMsgCommand.getBusinessType());

            throw new DomainException(String.format("微信消息发送失败, 用户:%s 业务类型:%s 配置的队列不存在!",
                    weChatMsgCommand.getUserId(),
                    weChatMsgCommand.getBusinessType()),
                    EnumExceptionCode.BadRequest);
        }
    }

    /**
     * 发送钉钉普通消息
     *
     * @param normalMsgCommand
     */
    @MQMessageSave(argsIndex = 0)
    public void sendDingDingNormalMsg(DingDingNormalMsgCommand normalMsgCommand) {
        UserQueueDTO userQueueDTO = queueService.getUserQueueByIdAndType(normalMsgCommand.getUserId(), normalMsgCommand.getBusinessType());
        if (userQueueDTO != null) {
            messagePublisher.messageSender(
                    normalMsgCommand,
                    userQueueDTO.getQueue(),
                    normalMsgCommand.getPriority(),
                    normalMsgCommand.getMessageId().toString(),
                    (normalMsgCommand.getTtl() != null && normalMsgCommand.getTtl() > 0) ? normalMsgCommand.getTtl() : userQueueDTO.getTtl());
        } else {
            log.error("钉钉普通消息发送失败, 用户:{} 业务类型:{} 配置的队列不存在或已经被禁用!", normalMsgCommand.getUserId(), normalMsgCommand.getBusinessType());
            throw new DomainException(String.format("钉钉普通消息发送失败, 用户:%s 业务类型:%s 配置的队列不存在!", normalMsgCommand.getUserId(), normalMsgCommand.getBusinessType()), EnumExceptionCode.BadRequest);
        }
    }

    /**
     * 发送钉钉群组消息
     *
     * @param groupMsgCommand
     */
    @MQMessageSave(argsIndex = 0)
    public void sendDingDingGroupMsg(DingDingGroupMsgCommand groupMsgCommand) {
        UserQueueDTO userQueueDTO = queueService.getUserQueueByIdAndType(groupMsgCommand.getUserId(), groupMsgCommand.getBusinessType());
        if (userQueueDTO != null) {
            messagePublisher.messageSender(
                    groupMsgCommand,
                    userQueueDTO.getQueue(),
                    groupMsgCommand.getPriority(),
                    groupMsgCommand.getMessageId().toString(),
                    (groupMsgCommand.getTtl() != null && groupMsgCommand.getTtl() > 0) ? groupMsgCommand.getTtl() : userQueueDTO.getTtl());
        } else {
            log.error("钉钉群组消息发送失败, 用户:{} 业务类型:{} 配置的队列不存在或已经被禁用!",
                    groupMsgCommand.getUserId(),
                    groupMsgCommand.getBusinessType());

            throw new DomainException(String.format("钉钉群组消息发送失败, 用户:%s 业务类型:%s 配置的队列不存在!",
                    groupMsgCommand.getUserId(),
                    groupMsgCommand.getBusinessType()),
                    EnumExceptionCode.BadRequest);
        }
    }

    /**
     * 发送邮件消息
     *
     * @param emailContentCommand
     */
    @MQMessageSave(argsIndex = 0)
    public void sendEmailMsg(EmailContentCommand emailContentCommand) {
        UserQueueDTO userQueueDTO = queueService.getUserQueueByIdAndType(
                emailContentCommand.getUserId(),
                emailContentCommand.getBusinessType());
        if (userQueueDTO != null) {
            messagePublisher.messageSender(
                    emailContentCommand,
                    userQueueDTO.getQueue(),
                    emailContentCommand.getPriority(),
                    emailContentCommand.getMessageId().toString(),
                    (emailContentCommand.getTtl() != null && emailContentCommand.getTtl() > 0) ? emailContentCommand.getTtl() : userQueueDTO.getTtl());
        } else {
            log.error("邮件消息发送失败, 用户:{} 业务类型:{} 配置的队列不存在或已经被禁用!",
                    emailContentCommand.getUserId(),
                    emailContentCommand.getBusinessType());

            throw new DomainException(String.format("邮件消息发送失败, 用户:%s 业务类型:%s 配置的队列不存在!",
                    emailContentCommand.getUserId(),
                    emailContentCommand.getBusinessType()),
                    EnumExceptionCode.BadRequest);
        }
    }

    /**
     * 发送SMS消息
     *
     * @param smsMsgCommand
     */
    @MQMessageSave(argsIndex = 0)
    public void sendSMSMsg(SMSMsgCommand smsMsgCommand) {
        UserQueueDTO userQueueDTO = queueService.getUserQueueByIdAndType(smsMsgCommand.getUserId(), smsMsgCommand.getBusinessType());
        if (userQueueDTO != null) {
            messagePublisher.messageSender(
                    smsMsgCommand,
                    userQueueDTO.getQueue(),
                    smsMsgCommand.getPriority(),
                    smsMsgCommand.getMessageId().toString(),
                    (smsMsgCommand.getTtl() != null && smsMsgCommand.getTtl() > 0) ? smsMsgCommand.getTtl() : userQueueDTO.getTtl());
        } else {
            log.error("SMS消息发送失败, 用户:{} 业务类型:{} 配置的队列不存在或已经被禁用!",
                    smsMsgCommand.getUserId(),
                    smsMsgCommand.getBusinessType());

            throw new DomainException(String.format("SMS件消息发送失败, 用户:%s 业务类型:%s 配置的队列不存在!",
                    smsMsgCommand.getUserId(),
                    smsMsgCommand.getBusinessType()),
                    EnumExceptionCode.BadRequest);
        }
    }
}
