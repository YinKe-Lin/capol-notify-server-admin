package com.capol.notify.manage.application.message.querystack;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel(value = "队列消息内容")
@NoArgsConstructor
@AllArgsConstructor
public class UserQueueMessageDTO {
    /**
     * 消息所属业务系统ID
     */
    @ApiModelProperty("消息所属业务系统ID")
    private String serviceId;

    /**
     * 业务系统用户ID
     */
    @ApiModelProperty("业务系统用户ID")
    private Long userId;

    /**
     * 消息所属队列ID
     */
    @ApiModelProperty("消息所属队列ID")
    private Long queueId;

    /**
     * 消息优先级
     */
    @ApiModelProperty("消息优先级")
    private Integer priority;

    /**
     * 消息类型(1-钉钉普通消息 2-钉钉群组消息 3-邮件消息)
     */
    @ApiModelProperty("消息类型(1-钉钉普通消息 2-钉钉群组消息 3-邮件消息)")
    private String messageType;

    /**
     * 消息业务类型
     */
    @ApiModelProperty("消息业务类型")
    private String businessType;

    /**
     * 消息内容
     */
    @ApiModelProperty("消息内容")
    private String content;

    /**
     * 消息发送响应内容
     */
    @ApiModelProperty("消息发送响应内容")
    private String sendResponse;

    /**
     * 消息处理状态(0-待处理 1-处理成功 2-处理失败)
     */
    @ApiModelProperty("消息处理状态(0-待处理 1-处理成功 2-处理失败)")
    private Integer processStatus;

    /**
     * 消息处理失败重试次数
     */
    @ApiModelProperty("消息处理失败重试次数")
    private Integer retryCount;

    /**
     * 消费端开始消费时间
     */
    @ApiModelProperty("消费端开始消费时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date consumerStartTime;

    /**
     * 消费端消费结束时间
     */
    @ApiModelProperty("消费端消费结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date consumerEndTime;
}
