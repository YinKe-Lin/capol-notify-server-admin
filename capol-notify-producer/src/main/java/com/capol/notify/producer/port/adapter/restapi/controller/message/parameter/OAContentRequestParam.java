package com.capol.notify.producer.port.adapter.restapi.controller.message.parameter;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * OA消息内容
 */
@Data
public class OAContentRequestParam {
    private static final long serialVersionUID = -8445943548965154999L;

    @ApiModelProperty("消息跳转的URL")
    private String messageUrl;
    @ApiModelProperty("PC端消息跳转的URL")
    private String pcMessageUrl;
    @ApiModelProperty("消息头部信息")
    private String headText;
    @ApiModelProperty("消息内容标题")
    private String bodyTitle;
    @ApiModelProperty("消息表单内容")
    private LinkedHashMap<String, String> form;
    @ApiModelProperty("多条消息正文")
    private List<Map<String, String>> content;
    @ApiModelProperty("单条消息正文")
    private String bodyContent;
}
