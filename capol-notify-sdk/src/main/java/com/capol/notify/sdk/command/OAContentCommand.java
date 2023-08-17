package com.capol.notify.sdk.command;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * oa类型钉钉通知
 */
@Data
public class OAContentCommand implements Serializable {

    private static final long serialVersionUID = -8445943548965154999L;

    /**
     * 消息跳转链接
     */
    private String messageUrl;

    /**
     * PC 端消息跳转链接
     */
    private String pcMessageUrl;

    /**
     * 消息头部
     */
    private String headText;

    /**
     * 消息正文
     */
    private String bodyTitle;

    /**
     * 消息表单内容
     */
    private LinkedHashMap<String, String> form;

    /**
     * 多条消息正文
     */
    private List<Map<String, String>> content;
    /**
     * 单条消息正文
     */
    private String bodyContent;
}
