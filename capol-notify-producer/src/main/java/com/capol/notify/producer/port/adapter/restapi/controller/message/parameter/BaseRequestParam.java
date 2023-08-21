package com.capol.notify.producer.port.adapter.restapi.controller.message.parameter;

import com.capol.notify.sdk.EnumMessageType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 消息发送基本信息
 */
@Data
@NoArgsConstructor
public class BaseRequestParam {
    @ApiModelProperty("消息类型(1-钉钉普通消息 2-钉钉群组消息 3-微信消息 4-邮件消息)")
    @NotNull(message = "消息类型不允许为空->1.DING_NORMAL_MESSAGE:钉钉普通消息,2.DING_GROUP_MESSAGE:钉钉群组消息,3.WECHAT_MESSAGE:微信消息 4.EMAIL_MESSAGE:邮件消息")
    private EnumMessageType messageType;

    @ApiModelProperty("消息优先级")
    private Integer priority;

    @ApiModelProperty("消息过期时间")
    private Integer ttl;

    @ApiModelProperty("消息业务类型")
    @NotNull(message = "消息业务类型不允许为空!")
    private String businessType;
}