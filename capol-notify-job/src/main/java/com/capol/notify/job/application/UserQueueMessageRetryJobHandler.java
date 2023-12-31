package com.capol.notify.job.application;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.capol.notify.job.domain.model.message.MessagePublisher;
import com.capol.notify.manage.application.message.MessageService;
import com.capol.notify.manage.application.queue.QueueService;
import com.capol.notify.manage.application.user.querystack.UserQueueDTO;
import com.capol.notify.manage.domain.EnumProcessStatusType;
import com.capol.notify.manage.domain.PageParam;
import com.capol.notify.manage.domain.PageResult;
import com.capol.notify.manage.domain.model.message.UserQueueMessageDO;
import com.capol.notify.sdk.EnumMessageType;
import com.capol.notify.sdk.command.*;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 消息重试处理器
 */
@Slf4j
@Component
public class UserQueueMessageRetryJobHandler {
    /**
     * 要监控的消息类型
     */
    List<EnumMessageType> messageTypes = Arrays.asList(
            EnumMessageType.DING_NORMAL_MESSAGE,
            EnumMessageType.DING_GROUP_MESSAGE,
            EnumMessageType.EMAIL_MESSAGE,
            EnumMessageType.SMS_MESSAGE,
            EnumMessageType.SMS_BATCH_MESSAGE,
            EnumMessageType.WECHAT_MESSAGE);

    /**
     * 要监控的消息处理状态
     */
    List<EnumProcessStatusType> processStatusTypes = Arrays.asList(
            EnumProcessStatusType.FAILURE,
            EnumProcessStatusType.WAIT_TODO
    );

    private MessageService messageService;
    private QueueService queueService;
    private MessagePublisher messagePublisher;

    public UserQueueMessageRetryJobHandler(MessageService messageService,
                                           QueueService queueService,
                                           MessagePublisher messagePublisher) {
        this.messageService = messageService;
        this.queueService = queueService;
        this.messagePublisher = messagePublisher;
    }

    public void retryInit() {
        if (log.isDebugEnabled()) {
            log.debug("-->定时任务初始化, 用途:定时检测需要<重发>的消息!");
        }
    }

    public void retryDestroy() {
        if (log.isDebugEnabled()) {
            log.debug("-->定时任务销毁, 用途:定时检测需要<重发>的消息!");
        }
    }

    @XxlJob(value = "userQueueMessageRetryJobHandler", init = "retryInit", destroy = "retryDestroy")
    public ReturnT<String> checkRetryMessageJob() throws Exception {
        try {
            String param = XxlJobHelper.getJobParam();
            if (StringUtils.isEmpty(param)) {
                log.error("-->userQueueMessageRetryJobHandler 参数配置缺失! 参数格式, 例如：{},第一个参数单位为(天)，第二个参数单位为(分钟)", param, "5,30");
                return ReturnT.FAIL;
            }
            String[] params = param.split(",");
            if (params.length != 2) {
                log.error("-->userQueueMessageRetryJobHandler 参数不合法, 当前配置参数为：{}, 正确配置参数方式, 例如：{} 第一个参数单位为(天)，第二个参数单位为(分钟)!", param, "5,30");
                return ReturnT.FAIL;
            }
            if (log.isDebugEnabled()) {
                log.debug("-->参数Params:{}", JSON.toJSONString(params));
                log.debug("-->开始时间为当前日期<{}>天之前的消息,结束时间为当前日期<{}>分钟之前的消息.", params[0], params[1]);
            }
            //开始时间为3天之前的
            LocalDateTime startDateTime = LocalDateTime.now().minusDays(Long.valueOf(params[0]));
            //结束时间为当前时间前30分钟的
            LocalDateTime endDateTime = LocalDateTime.now().minusMinutes(Long.valueOf(params[1]));

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String startTime = dateTimeFormatter.format(startDateTime);
            String endTime = dateTimeFormatter.format(endDateTime);
            log.info("-->消息<重发处理器>进来了,开始时间:{} 结束时间:{}!", startTime, endTime);

            Long totalCount = messageService.getTotalCountByParam(processStatusTypes, messageTypes, startTime, endTime);

            if (totalCount <= 0) {
                log.info("-->暂无符合<重发>条件的消息记录! 查询时间区间, 开始时间:{} 结束时间:{}", startTime, endTime);
                log.info("-->消息<重发>监控Job任务处理器执行结束!!!!");
                return ReturnT.SUCCESS;
            }

            PageParam pageParam = new PageParam();
            pageParam.setPageSize(100);
            pageParam.setTotalCount(totalCount.intValue());

            //钉钉普通消息
            DingDingNormalMsgCommand dingNormalMsgCommand = null;
            //钉钉群组消息
            DingDingGroupMsgCommand dingDingGroupMsgCommand = null;
            //Email消息
            EmailContentCommand emailContentCommand = null;
            //微信消息
            WeChatMsgCommand weChatMsgCommand = null;
            //SMS短信消息
            SMSMsgCommand smsMsgCommand = null;
            while (pageParam.getPageNo() <= pageParam.getTotalPageNumber()) {
                PageResult<UserQueueMessageDO> messageDOPageResult = messageService.getMessageByPage(processStatusTypes, messageTypes, startTime, endTime, pageParam);
                List<UserQueueMessageDO> records = messageDOPageResult.getList();
                for (UserQueueMessageDO messageDO : records) {
                    UserQueueDTO userQueueDTO = queueService.getUserQueueByIdAndType(messageDO.getUserId(), messageDO.getBusinessType());
                    if (userQueueDTO == null) {
                        log.error("-->定时任务重发消息异常, 消息业务类型:{} 消息ID:{} 消息所属业务系统:{} 消息发送队列不存在或已禁用!",
                                messageDO.getBusinessType(),
                                messageDO.getId(),
                                messageDO.getServiceId());
                        continue;
                    }
                    switch (EnumMessageType.ofValue(messageDO.getMessageType())) {
                        case DING_NORMAL_MESSAGE: {
                            dingNormalMsgCommand = JSON.parseObject(messageDO.getContent(), new TypeReference<DingDingNormalMsgCommand>() {
                            });
                            //处理失败
                            if (EnumProcessStatusType.FAILURE.getCode().equals(messageDO.getProcessStatus())) {
                                log.info("-->定时任务重发<处理失败的消息>, 消息业务类型:{} 消息ID:{} 消息所属业务系统:{} 消息发送队列:{}",
                                        messageDO.getBusinessType(),
                                        messageDO.getId(),
                                        messageDO.getServiceId(),
                                        userQueueDTO.getQueue());
                            }
                            if (EnumProcessStatusType.WAIT_TODO.getCode().equals(messageDO.getProcessStatus())) {
                                log.info("-->定时任务重发<等待处理的消息>, 消息业务类型:{} 消息ID:{} 消息所属业务系统:{} 消息发送队列:{}",
                                        messageDO.getBusinessType(),
                                        messageDO.getId(),
                                        messageDO.getServiceId(),
                                        userQueueDTO.getQueue());
                            }
                            //重发的消息优先级默认为10
                            messagePublisher.messageSender(dingNormalMsgCommand, userQueueDTO.getQueue(), 10, String.valueOf(messageDO.getId()));
                            break;
                        }
                        case DING_GROUP_MESSAGE: {
                            dingDingGroupMsgCommand = JSON.parseObject(messageDO.getContent(), new TypeReference<DingDingGroupMsgCommand>() {
                            });
                            //处理失败
                            if (EnumProcessStatusType.FAILURE.getCode().equals(messageDO.getProcessStatus())) {
                                log.info("-->定时任务重发<处理失败的消息>, 消息业务类型:{} 消息ID:{} 消息所属业务系统:{} 消息发送队列:{}",
                                        messageDO.getBusinessType(),
                                        messageDO.getId(),
                                        messageDO.getServiceId(),
                                        userQueueDTO.getQueue());
                            }
                            if (EnumProcessStatusType.WAIT_TODO.getCode().equals(messageDO.getProcessStatus())) {
                                log.info("-->定时任务重发<等待处理的消息>, 消息业务类型:{} 消息ID:{} 消息所属业务系统:{} 消息发送队列:{}",
                                        messageDO.getBusinessType(),
                                        messageDO.getId(),
                                        messageDO.getServiceId(),
                                        userQueueDTO.getQueue());
                            }
                            //重发的消息优先级默认为10
                            messagePublisher.messageSender(dingDingGroupMsgCommand, userQueueDTO.getQueue(), 10, String.valueOf(messageDO.getId()));
                            break;
                        }
                        case WECHAT_MESSAGE: {
                            weChatMsgCommand = JSON.parseObject(messageDO.getContent(), new TypeReference<WeChatMsgCommand>() {
                            });
                            //处理失败
                            if (EnumProcessStatusType.FAILURE.getCode().equals(messageDO.getProcessStatus())) {
                                log.info("-->定时任务重发<处理失败的消息>, 消息业务类型:{} 消息ID:{} 消息所属业务系统:{} 消息发送队列:{}",
                                        messageDO.getBusinessType(),
                                        messageDO.getId(),
                                        messageDO.getServiceId(),
                                        messageDO.getContent(),
                                        userQueueDTO.getQueue());
                            }
                            if (EnumProcessStatusType.WAIT_TODO.getCode().equals(messageDO.getProcessStatus())) {
                                log.info("-->定时任务重发<等待处理的消息>, 消息业务类型:{} 消息ID:{} 消息所属业务系统:{} 消息发送队列:{}",
                                        messageDO.getBusinessType(),
                                        messageDO.getId(),
                                        messageDO.getServiceId(),
                                        userQueueDTO.getQueue());
                            }
                            //重发的消息优先级默认为10
                            messagePublisher.messageSender(weChatMsgCommand, userQueueDTO.getQueue(), 10, String.valueOf(messageDO.getId()));
                            break;
                        }
                        case EMAIL_MESSAGE: {
                            emailContentCommand = JSON.parseObject(messageDO.getContent(), new TypeReference<EmailContentCommand>() {
                            });
                            //处理失败
                            if (EnumProcessStatusType.FAILURE.getCode().equals(messageDO.getProcessStatus())) {
                                log.info("-->定时任务重发<处理失败的消息>, 消息业务类型:{} 消息ID:{} 消息所属业务系统:{} 消息发送队列:{}",
                                        messageDO.getBusinessType(),
                                        messageDO.getId(),
                                        messageDO.getServiceId(),
                                        userQueueDTO.getQueue());
                            }
                            if (EnumProcessStatusType.WAIT_TODO.getCode().equals(messageDO.getProcessStatus())) {
                                log.info("-->定时任务重发<等待处理的消息>, 消息业务类型:{} 消息ID:{} 消息所属业务系统:{} 消息发送队列:{}",
                                        messageDO.getBusinessType(),
                                        messageDO.getId(),
                                        messageDO.getServiceId(),
                                        userQueueDTO.getQueue());
                            }
                            //重发的消息优先级默认为10
                            messagePublisher.messageSender(emailContentCommand, userQueueDTO.getQueue(), 10, String.valueOf(messageDO.getId()));
                            break;
                        }
                        case SMS_MESSAGE:
                        case SMS_BATCH_MESSAGE: {
                            smsMsgCommand = JSON.parseObject(messageDO.getContent(), new TypeReference<SMSMsgCommand>() {
                            });
                            //处理失败
                            if (EnumProcessStatusType.FAILURE.getCode().equals(messageDO.getProcessStatus())) {
                                log.info("-->定时任务重发<处理失败的消息>, 消息业务类型:{} 消息ID:{} 消息所属业务系统:{} 消息发送队列:{}",
                                        messageDO.getBusinessType(),
                                        messageDO.getId(),
                                        messageDO.getServiceId(),
                                        userQueueDTO.getQueue());
                            }
                            if (EnumProcessStatusType.WAIT_TODO.getCode().equals(messageDO.getProcessStatus())) {
                                log.info("-->定时任务重发<等待处理的消息>, 消息业务类型:{} 消息ID:{} 消息所属业务系统:{} 消息发送队列:{}",
                                        messageDO.getBusinessType(),
                                        messageDO.getId(),
                                        messageDO.getServiceId(),
                                        userQueueDTO.getQueue());
                            }
                            //重发的消息优先级默认为10
                            messagePublisher.messageSender(smsMsgCommand, userQueueDTO.getQueue(), 10, String.valueOf(messageDO.getId()));
                            break;
                        }
                    }
                }

                //处完一页数据后，页码+1
                pageParam.setPageNo(pageParam.getPageNo() + 1);
            }

            log.info("-->消息<重发>监控Job任务处理器执行成功!!!!");
            return ReturnT.SUCCESS;
        } catch (Exception exception) {
            log.error("-->消息<重发>监控Job任务处理器执行异常,异常详情:{}", exception);
            return ReturnT.FAIL;
        }
    }
}