package com.capol.notify.sdk.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 微信消息体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeChatMessageData implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 消息颜色
     */
    private String color;
    /**
     * 消息内容
     */
    private String value;
}