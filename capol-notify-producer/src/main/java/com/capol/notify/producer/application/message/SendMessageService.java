package com.capol.notify.producer.application.message;

import com.capol.notify.manage.application.ApplicationException;
import com.capol.notify.manage.application.user.UserService;
import com.capol.notify.manage.application.user.querystack.UserDTO;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.manage.domain.model.IdGenerator;
import com.capol.notify.manage.domain.model.permission.CurrentUserService;
import com.capol.notify.producer.domain.model.message.MessageProducer;
import com.capol.notify.sdk.command.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 消息发送服务
 *
 * @author heyong
 * @since 2023-08-02
 */
@Slf4j
@Service
public class SendMessageService {

    private MessageProducer messageProducer;
    private CurrentUserService currentUserService;
    private UserService userService;

    public SendMessageService(MessageProducer messageProducer, CurrentUserService currentUserService, UserService userService) {
        this.messageProducer = messageProducer;
        this.currentUserService = currentUserService;
        this.userService = userService;
    }

    /**
     * 检测ServiceID是否有被禁用
     *
     * @param userId
     */
    private void checkServiceId(String userId) {
        UserDTO userDTO = userService.userBaseInfo(userId);
        if (userDTO == null) {
            throw new ApplicationException(String.format("账户:<%s>不存在!", userId), EnumExceptionCode.UserNotExists);
        }
        if (userDTO.getDisabled()) {
            throw new ApplicationException(String.format("账户:<%s>, ServiceID:<%s>已经被禁用!", userId, userDTO.getServiceId()), EnumExceptionCode.Forbidden);
        }
    }

    /**
     * 微信消息发送方法
     *
     * @param weChatMsgCommand
     */
    public void sendWeiXinMsg(WeChatMsgCommand weChatMsgCommand) {
        if (weChatMsgCommand.getUserId() == null) {
            Long userId = currentUserService.getCurrentUserId() != null ? Long.valueOf(currentUserService.getCurrentUserId()) : null;
            weChatMsgCommand.setUserId(userId);
        }
        Long messageId = IdGenerator.generateId();
        if (weChatMsgCommand.getMessageId() == null) {
            weChatMsgCommand.setMessageId(messageId);
        }
        if (weChatMsgCommand.getId() == null) {
            weChatMsgCommand.setId(messageId);
        }
        this.checkServiceId(String.valueOf(weChatMsgCommand.getUserId()));
        log.info("-->发送微信消息ID:{}", weChatMsgCommand.getMessageId());
        messageProducer.sendWeChatMsg(weChatMsgCommand);
    }

    /**
     * 钉钉普通消息发送方法
     *
     * @param normalMsgCommand
     */
    public void sendDingDingNormalMsg(DingDingNormalMsgCommand normalMsgCommand) {
        if (normalMsgCommand.getUserId() == null) {
            Long userId = currentUserService.getCurrentUserId() != null ? Long.valueOf(currentUserService.getCurrentUserId()) : null;
            normalMsgCommand.setUserId(userId);
        }
        if (normalMsgCommand.getMessageId() == null) {
            normalMsgCommand.setMessageId(IdGenerator.generateId());
        }
        this.checkServiceId(String.valueOf(normalMsgCommand.getUserId()));
        log.info("-->发送钉钉普通消息ID:{}", normalMsgCommand.getMessageId());
        messageProducer.sendDingDingNormalMsg(normalMsgCommand);
    }

    /**
     * 钉钉群组消息发送方法
     *
     * @param groupMsgCommand
     */
    public void sendDingDingGroupMsg(DingDingGroupMsgCommand groupMsgCommand) {
        if (groupMsgCommand.getUserId() == null) {
            Long userId = currentUserService.getCurrentUserId() != null ? Long.valueOf(currentUserService.getCurrentUserId()) : null;
            groupMsgCommand.setUserId(userId);
        }
        if (groupMsgCommand.getMessageId() == null) {
            groupMsgCommand.setMessageId(IdGenerator.generateId());
        }
        //检测ServiceId是否有被禁用
        this.checkServiceId(String.valueOf(groupMsgCommand.getUserId()));
        log.info("-->发送钉钉群组消息ID:{}", groupMsgCommand.getMessageId());
        messageProducer.sendDingDingGroupMsg(groupMsgCommand);
    }

    /**
     * 邮件发送方法
     *
     * @param emailContentCommand
     */
    public void sendEmailMsg(EmailContentCommand emailContentCommand) {
        if (emailContentCommand.getUserId() == null) {
            Long userId = currentUserService.getCurrentUserId() != null ? Long.valueOf(currentUserService.getCurrentUserId()) : null;
            emailContentCommand.setUserId(userId);
        }
        if (emailContentCommand.getMessageId() == null) {
            emailContentCommand.setMessageId(IdGenerator.generateId());
        }
        this.checkServiceId(String.valueOf(emailContentCommand.getUserId()));
        log.info("-->发送邮件消息ID:{}", emailContentCommand.getMessageId());
        messageProducer.sendEmailMsg(emailContentCommand);
    }

    /**
     * 发送短信消息
     *
     * @param smsMsgCommand
     * @return
     * @throws Exception
     */
    public void sendSMSMsg(SMSMsgCommand smsMsgCommand) {
        if (smsMsgCommand.getUserId() == null) {
            Long userId = currentUserService.getCurrentUserId() != null ? Long.valueOf(currentUserService.getCurrentUserId()) : null;
            smsMsgCommand.setUserId(userId);
        }
        Long messageId = IdGenerator.generateId();
        if (smsMsgCommand.getMessageId() == null) {
            smsMsgCommand.setMessageId(messageId);
        }
        if (smsMsgCommand.getId() == null) {
            smsMsgCommand.setId(messageId);
        }
        this.checkServiceId(String.valueOf(smsMsgCommand.getUserId()));
        log.info("-->发送短信消息ID:{}", smsMsgCommand.getMessageId());
        messageProducer.sendSMSMsg(smsMsgCommand);
    }
}
