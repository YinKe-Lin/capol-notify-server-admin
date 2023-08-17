package com.capol.notify.consumer.domain.model.message;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.capol.notify.consumer.domain.model.dtalk.DTalkSendMsgService;
import com.capol.notify.consumer.domain.model.email.EmailSendService;
import com.capol.notify.consumer.domain.model.sms.SMSSendMsgService;
import com.capol.notify.consumer.domain.model.wechat.WeChatSendMsgService;
import com.capol.notify.manage.domain.DomainException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.manage.domain.model.IdGenerator;
import com.capol.notify.manage.domain.model.message.MQMessageEdit;
import com.capol.notify.sdk.EnumMessageType;
import com.capol.notify.sdk.MessageReceiveConfirmCallback;
import com.capol.notify.sdk.command.*;
import com.dingtalk.api.response.OapiChatSendResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageReceiveListener {

    private RabbitTemplate rabbitTemplate;
    private RabbitProperties rabbitProperties;
    private DTalkSendMsgService dTalkSendMsgService;
    private EmailSendService emailSendService;
    private WeChatSendMsgService weChatSendMsgService;
    private SMSSendMsgService smsSendMsgService;
    private MessageReceiveConfirmCallback messageReceiveConfirmCallback;

    public MessageReceiveListener(RabbitTemplate rabbitTemplate,
                                  RabbitProperties rabbitProperties,
                                  DTalkSendMsgService dTalkSendMsgService,
                                  EmailSendService emailSendService,
                                  WeChatSendMsgService weChatSendMsgService,
                                  SMSSendMsgService smsSendMsgService,
                                  MessageReceiveConfirmCallback messageReceiveConfirmCallback) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitProperties = rabbitProperties;
        this.dTalkSendMsgService = dTalkSendMsgService;
        this.emailSendService = emailSendService;
        this.weChatSendMsgService = weChatSendMsgService;
        this.smsSendMsgService = smsSendMsgService;
        this.messageReceiveConfirmCallback = messageReceiveConfirmCallback;
    }

    /**
     * 指定队列创建监听
     *
     * @param queueName
     */
    public void startListening(String queueName) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(rabbitTemplate.getConnectionFactory());
        container.setQueueNames(queueName);
        // 设置当前消费者的数量
        container.setConcurrentConsumers(rabbitProperties.getListener().getSimple().getConcurrency());
        // 设置最大的消费者的数量
        container.setMaxConcurrentConsumers(rabbitProperties.getListener().getSimple().getMaxConcurrency());
        // 设置每次从队列中拿取的消息数量
        container.setPrefetchCount(rabbitProperties.getListener().getSimple().getPrefetch());
        // 设置 SimpleMessageListenerContainer 是否自动启动
        container.setAutoStartup(rabbitProperties.getListener().getSimple().isAutoStartup());
        // 是否重回队列，一般都不允许重回队里
        container.setDefaultRequeueRejected(rabbitProperties.getListener().getSimple().getDefaultRequeueRejected());
        // 可以设置签收模式, 比如设置为 自动签收
        container.setAcknowledgeMode(rabbitProperties.getListener().getSimple().getAcknowledgeMode());
        // 设置消费端的标签，生成策略，自定义消费端的标签生成策略
        // 标签 可以标识这个消息的唯一性
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + IdGenerator.generateId();
            }
        });
        // 监听消息
        container.setMessageListener(new ChannelAwareMessageListener() {
            /**
             * 如果有消息传递过来，就会进入这个 onMessage 方法
             * @param message 消息
             * @param channel 消息管道
             * @throws Exception
             */
            @MQMessageEdit(argsIndex = 0)
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                String messageContent = new String(message.getBody(), "UTF-8");
                String messageId = message.getMessageProperties().getMessageId();
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("消费者:{} ", message.getMessageProperties().getConsumerTag());
                        log.debug("接收到消息：{}", messageContent);
                        log.debug("消息优先级为：" + message.getMessageProperties().getPriority());
                        log.debug("消息交换机为：" + message.getMessageProperties().getReceivedExchange());
                        log.debug("消息路由为：" + message.getMessageProperties().getReceivedRoutingKey());
                        log.debug("消息队列为:{}", message.getMessageProperties().getConsumerQueue());
                        log.debug("消费者集群ID：{}", message.getMessageProperties().getClusterId());
                        log.debug("消息ID为：" + message.getMessageProperties().getMessageId());
                    }
                    //钉钉普通消息
                    DingDingNormalMsgCommand dingNormalMsgCommand = null;
                    //钉钉群组消息
                    DingDingGroupMsgCommand dingDingGroupMsgCommand = null;
                    //Email消息
                    EmailContentCommand emailContentCommand = null;
                    //微信消息
                    WeChatMsgCommand weChatMsgCommand = null;
                    //SMS消息
                    SMSMsgCommand smsMsgCommand = null;

                    JSONObject jsonMessageContent = JSON.parseObject(messageContent);
                    Object messageType = jsonMessageContent.get("messageType");

                    switch (EnumMessageType.valueOf(String.valueOf(messageType))) {
                        case DING_NORMAL_MESSAGE: {
                            dingNormalMsgCommand = JSON.parseObject(messageContent, new TypeReference<DingDingNormalMsgCommand>() {
                            });
                            if (dingNormalMsgCommand != null) {
                                log.info("-->开始调用钉钉SDK发送<普通消息>,开始时间:{}", DateUtil.dateSecond());
                                OapiMessageCorpconversationAsyncsendV2Response response = dTalkSendMsgService.sendNormalMsg(dingNormalMsgCommand);
                                log.info("-->调用钉钉SDK发送<普通消息>完成,完成时间:{}", DateUtil.dateSecond());
                                if (!response.isSuccess()) {
                                    StringBuilder resSB = new StringBuilder("DingTalk Send Response: ")
                                            .append("[Error Code]:")
                                            .append(response.getErrcode())
                                            .append(",[Error Message]:")
                                            .append(response.getErrmsg());
                                    log.error(String.format("-->调用钉钉 SDK 发送<普通消息>返回失败, 具体原因: %s", resSB));
                                    throw new DomainException(String.format("调用钉钉 SDK 发送<普通消息>返回失败, 具体原因: %s", resSB), EnumExceptionCode.InternalServerError);
                                } else {
                                    log.info("-->调用钉钉 SDK 发送<普通消息>返回成功, Response内容:{}", JSON.toJSONString(response));
                                }
                            } else {
                                log.error("-->发送<普通消息>中断, dingNormalMsgCommand<普通消息>对象为空,请检查JSON数据是否正确,以及是否能正常序列化!");
                                throw new DomainException(String.format("发送<普通消息>中断, dingNormalMsgCommand<普通消息>对象为空,请检查JSON数据是否正确,以及是否能正常序列化, JSON数据:%s", messageContent), EnumExceptionCode.InternalServerError);
                            }
                            break;
                        }
                        case DING_GROUP_MESSAGE: {
                            dingDingGroupMsgCommand = JSON.parseObject(messageContent, new TypeReference<DingDingGroupMsgCommand>() {
                            });
                            if (dingDingGroupMsgCommand != null) {
                                log.info("-->开始调用钉钉SDK发送<群组消息>,开始时间:{}", DateUtil.dateSecond());
                                OapiChatSendResponse response = dTalkSendMsgService.sendGroupMsg(dingDingGroupMsgCommand);
                                log.info("-->调用钉钉SDK发送<群组消息>完成,完成时间:{}", DateUtil.dateSecond());
                                if (!response.isSuccess()) {
                                    StringBuilder resSB = new StringBuilder("DingTalk Send Response: ")
                                            .append("[Error Code]:")
                                            .append(response.getErrcode())
                                            .append(",[Error Message]:")
                                            .append(response.getErrmsg());
                                    log.error(String.format("-->调用钉钉 SDK 发送<群组消息>返回失败, 具体原因: %s", resSB));
                                    throw new DomainException(String.format("调用钉钉 SDK 发送<群组消息>返回失败, 具体原因: %s", resSB), EnumExceptionCode.InternalServerError);
                                } else {
                                    log.info("-->调用钉钉 SDK 发送<群组消息>返回成功, Response内容:{}", JSON.toJSONString(response));
                                }
                            } else {
                                log.error("-->发送<群组消息>中断, DingDingGroupMsgCommand<群组消息>对象为空,请检查JSON数据是否正确,以及是否能正常序列化!");
                                throw new DomainException(String.format("发送<群组消息>中断, DingDingGroupMsgCommand<群组消息>对象为空,请检查JSON数据是否正确,以及是否能正常序列化, JSON数据:%s", messageContent), EnumExceptionCode.InternalServerError);
                            }
                            break;
                        }
                        case WECHAT_MESSAGE: {
                            weChatMsgCommand = JSON.parseObject(messageContent, new TypeReference<WeChatMsgCommand>() {
                            });
                            if (weChatMsgCommand != null) {
                                weChatMsgCommand.setRunStartTime(System.currentTimeMillis());
                                log.info("-->开始调用微信消息发送服务,开始时间:{}", DateUtil.dateSecond());
                                String response = weChatSendMsgService.sendWeChatMsg(weChatMsgCommand);
                                log.info("-->调用微信消息发送服务完成,完成时间:{}", DateUtil.dateSecond());
                                if (response != null) {
                                    log.error(String.format("-->调用微信消息发送服务返回失败, 具体原因: %s", response));
                                    throw new DomainException(String.format("调用邮件发送服务返回失败, 具体原因: %s", response), EnumExceptionCode.InternalServerError);
                                } else {
                                    log.info("-->调用微信消息发送服务返回成功!");
                                }
                            } else {
                                log.error("-->发送微信消息中断, weChatMsgCommand<微信消息>对象为空,请检查JSON数据是否正确,以及是否能正常序列化!");
                                throw new DomainException(String.format("发送微信消息中断, weChatMsgCommand<微信消息>对象为空,请检查JSON数据是否正确,以及是否能正常序列化, JSON数据:%s", messageContent), EnumExceptionCode.InternalServerError);
                            }
                            break;
                        }
                        case EMAIL_MESSAGE: {
                            emailContentCommand = JSON.parseObject(messageContent, new TypeReference<EmailContentCommand>() {
                            });
                            if (emailContentCommand != null) {
                                log.info("-->开始调用邮件发送服务,开始时间:{}", DateUtil.dateSecond());
                                boolean response = emailSendService.sendEmail(emailContentCommand);
                                log.info("-->调用邮件发送服务完成,完成时间:{}", DateUtil.dateSecond());
                                if (!response) {
                                    StringBuilder resSB = new StringBuilder("邮件发送失败响应结果")
                                            .append(", 发件人:")
                                            .append(emailContentCommand.getSender())
                                            .append(", 收件人:")
                                            .append(JSONObject.toJSONString(emailContentCommand.getTo()))
                                            .append(", 抄送人:")
                                            .append(emailContentCommand.getCc());
                                    log.error(String.format("-->调用邮件发送服务返回失败, 具体原因: %s", resSB));
                                    throw new DomainException(String.format("调用邮件发送服务返回失败, 具体原因: %s", resSB), EnumExceptionCode.InternalServerError);
                                } else {
                                    log.info("-->调用邮件发送服务返回成功!");
                                }
                            } else {
                                log.warn("-->发送邮件中断, emailContentCommand<邮件消息>对象为空,请检查JSON数据是否正确,以及是否能正常序列化!");
                                throw new DomainException(String.format("发送邮件中断, emailContentCommand<邮件消息>对象为空,请检查JSON数据是否正确,以及是否能正常序列化, JSON数据:%s", messageContent), EnumExceptionCode.InternalServerError);
                            }
                            break;
                        }
                        case SMS_MESSAGE: {
                            smsMsgCommand = JSON.parseObject(messageContent, new TypeReference<SMSMsgCommand>() {
                            });
                            if (smsMsgCommand != null) {
                                log.info("-->开始调用SMS短信发送服务,开始时间:{}", DateUtil.dateSecond());
                                boolean response = smsSendMsgService.sendSms(smsMsgCommand);
                                log.info("-->调用SMS短信发送服务完成,完成时间:{}", DateUtil.dateSecond());
                                if (!response) {
                                    StringBuilder resSB = new StringBuilder("SMS短信发送失败响应结果")
                                            .append("SMS短信收件人:")
                                            .append(StringUtils.isBlank(
                                                    smsMsgCommand.getPhoneNumbers())
                                                    ? smsMsgCommand.getPhoneNumberJson()
                                                    : smsMsgCommand.getPhoneNumbers());
                                    log.error(String.format("-->调用SMS短信发送服务返回失败, 具体原因: %s", resSB));
                                    throw new DomainException(String.format("调用SMS短信发送服务返回失败, 具体原因: %s", resSB), EnumExceptionCode.InternalServerError);
                                } else {
                                    log.info("-->调用SMS短信发送服务返回成功!");
                                }
                            } else {
                                log.warn("-->发送SMS短信中断, smsMsgCommand<SMS短信消息>对象为空,请检查JSON数据是否正确,以及是否能正常序列化!");
                                throw new DomainException(String.format("发送SMS短信中断, smsMsgCommand<SMS短信消息>对象为空,请检查JSON数据是否正确,以及是否能正常序列化, JSON数据:%s", messageContent), EnumExceptionCode.InternalServerError);
                            }
                            break;
                        }
                        case SMS_BATCH_MESSAGE: {
                            smsMsgCommand = JSON.parseObject(messageContent, new TypeReference<SMSMsgCommand>() {
                            });
                            if (smsMsgCommand != null) {
                                log.info("-->开始调用SMS短信批量发送服务,开始时间:{}", DateUtil.dateSecond());
                                boolean response = smsSendMsgService.sendBatchSms(smsMsgCommand.getPhoneNumberJson(),
                                        smsMsgCommand.getSignNameJson(),
                                        smsMsgCommand.getTemplateCode(),
                                        smsMsgCommand.getTemplateParamJson());
                                log.info("-->调用SMS短信批量发送服务完成,完成时间:{}", DateUtil.dateSecond());
                                if (!response) {
                                    StringBuilder resSB = new StringBuilder("SMS短信批量发送失败响应结果")
                                            .append("SMS短信批量收件人:")
                                            .append(StringUtils.isBlank(
                                                    smsMsgCommand.getPhoneNumberJson())
                                                    ? smsMsgCommand.getPhoneNumbers()
                                                    : smsMsgCommand.getPhoneNumberJson());
                                    log.error(String.format("-->调用SMS短信批量发送服务返回失败, 具体原因: %s", resSB));
                                    throw new DomainException(String.format("调用SMS短信批量发送服务返回失败, 具体原因: %s", resSB), EnumExceptionCode.InternalServerError);
                                } else {
                                    log.info("-->调用SMS短信批量发送服务返回成功!");
                                }
                            } else {
                                log.warn("-->批量发送SMS短信中断, smsMsgCommand<SMS短信消息>对象为空,请检查JSON数据是否正确,以及是否能正常序列化!");
                                throw new DomainException(String.format("批量发送SMS短信中断, smsMsgCommand<SMS短信消息>对象为空,请检查JSON数据是否正确,以及是否能正常序列化, JSON数据:%s", messageContent), EnumExceptionCode.InternalServerError);
                            }
                            break;
                        }
                    }
                    if (messageReceiveConfirmCallback != null) {
                        messageReceiveConfirmCallback.receiveConfirmCallback(true, Long.valueOf(messageId), null);
                    }
                } catch (Exception exception) {
                    log.error("-->消息消费处理异常：", exception);
                    if (messageReceiveConfirmCallback != null) {
                        messageReceiveConfirmCallback.receiveConfirmCallback(false, Long.valueOf(messageId), exception);
                    }
                } finally {
                    //手动ACK处理消息
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 确认消息已经消费
                }
            }
        });
        container.start();
        log.info("-->MessageReceiveListener创建队列:{}监听成功!", queueName);
    }
}
