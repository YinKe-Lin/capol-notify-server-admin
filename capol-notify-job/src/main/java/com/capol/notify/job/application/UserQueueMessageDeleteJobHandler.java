package com.capol.notify.job.application;

import com.alibaba.fastjson.JSON;
import com.capol.notify.manage.application.message.MessageService;
import com.capol.notify.manage.domain.model.message.UserQueueMessageDO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息删除处理器
 */
@Slf4j
@Component
public class UserQueueMessageDeleteJobHandler {
    private MessageService messageService;

    public UserQueueMessageDeleteJobHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    public void deleteInit() {
        if (log.isDebugEnabled()) {
            log.debug("-->定时任务初始化, 用途:定时检测需要删除的消息!");
        }
    }

    public void deleteDestroy() {
        if (log.isDebugEnabled()) {
            log.debug("-->定时任务销毁, 用途:定时检测需要删除的消息!");
        }
    }

    @XxlJob(value = "userQueueMessageDeleteJobHandler", init = "deleteInit", destroy = "deleteDestroy")
    public ReturnT<String> checkDeleteMessageJob() throws Exception {
        try {
            String param = XxlJobHelper.getJobParam();
            if (StringUtils.isEmpty(param)) {
                log.error("-->userQueueMessageDeleteJobHandler 参数配置缺失! 参数格式, 例如：{},第一个参数单位为(天)，第二个参数单位为(天)", param, "5,2");
                return ReturnT.FAIL;
            }
            String[] params = param.split(",");
            if (params.length != 2) {
                log.error("-->userQueueMessageDeleteJobHandler 参数不合法, 当前配置参数为：{}, 正确配置参数方式, 例如：{},第一个参数单位为(天)，第二个参数单位为(天)", param, "5,2");
                return ReturnT.FAIL;
            }
            if (log.isDebugEnabled()) {
                log.debug("-->参数Params:{}", JSON.toJSONString(params));
                log.debug("-->开始时间为当前日期<{}>天之前的消息,结束时间为当前日期<{}>天之前的消息.", params[0], params[1]);
            }
            //开始时间
            LocalDateTime startDateTime = LocalDateTime.now().minusDays(Long.valueOf(params[0]));
            //结束时间
            LocalDateTime endDateTime = LocalDateTime.now().minusDays(Long.valueOf(params[1]));

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String startTime = dateTimeFormatter.format(startDateTime);
            String endTime = dateTimeFormatter.format(endDateTime);
            log.info("-->消息<删除处理器>进来了,开始时间:{} 结束时间:{}!", startTime, endTime);

            List<UserQueueMessageDO> userQueueMessageDOS = messageService.getMessageByDateTime(startTime, endTime);

            if (CollectionUtils.isEmpty(userQueueMessageDOS)) {
                log.info("-->暂无符合<删除>条件的消息记录! 查询时间区间, 开始时间:{} 结束时间:{}", startTime, endTime);
                log.info("-->消息<删除>监控Job任务处理器执行结束!!!!");
                return ReturnT.SUCCESS;
            }
            log.info("-->将要<删除的消息>内容:{}", JSON.toJSONString(userQueueMessageDOS));
            List<Long> ids = userQueueMessageDOS.stream().map(UserQueueMessageDO::getId).collect(Collectors.toList());
            messageService.deleteMessageByIds(ids);
            log.info("-->消息<删除>监控Job任务处理器执行成功!!!!");
            return ReturnT.SUCCESS;
        } catch (Exception exception) {
            log.error("-->消息<删除>监控Job任务处理器执行异常, 异常详情:{}", exception);
            return ReturnT.FAIL;
        }
    }
}