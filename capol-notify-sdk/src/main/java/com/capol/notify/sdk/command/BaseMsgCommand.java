package com.capol.notify.sdk.command;

import com.capol.notify.sdk.EnumMessageContentType;
import com.capol.notify.sdk.EnumMessageType;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseMsgCommand implements Serializable {
    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 当前发送消息的身份ID(通过该ID，获取用户队列信息)
     */
    private Long userId;

    /**
     * 消息优先级
     */
    private Integer priority;

    /**
     * 消息过期时间
     */
    private Integer ttl;

    /**
     * 消息类型(1-钉钉普通消息 2-钉钉群组消息 3-微信消息 4-邮件消息 5-短信消息)
     */
    private EnumMessageType messageType;

    /**
     * 消息内容类型(1:text 2:image 3:file 4:link 5:markdown 6:action_card)
     */
    private EnumMessageContentType contentType;

    /**
     * 消息业务类型(根据业务类型确定要发送的消息队列)
     */
    private String businessType;
}
