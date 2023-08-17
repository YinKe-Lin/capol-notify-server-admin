package com.capol.notify.producer.port.adapter.restapi.controller.message.parameter;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * ActionCard消息内容
 */
@Data
public class ActionCardRequestParam {
    private static final long serialVersionUID = -8445943548965154999L;
    /**
     * 表示按钮排列的方向，可选值为 "0" 和 "1"，含义如下：
     * "0"：按钮竖直排列；
     * "1"：按钮横向排列。
     */
    private String btnOrientation;
    /**
     * 卡片 markdown 格式的内容
     */
    @ApiModelProperty("卡片markdown格式的内容")
    private String markdown;
    /**
     * 设置单个按钮的标题
     */
    @ApiModelProperty("设置单个按钮的标题")
    @NotNull(message = "按钮的标题不允许为空!")
    @NotBlank(message = "按钮的标题不允许为空!")
    private String singleTitle;
    /**
     * 设置单个按钮的链接
     */
    @ApiModelProperty("设置单个按钮的链接")
    @NotNull(message = "按钮的链接不允许为空!")
    @NotBlank(message = "消按钮的链接不允许为空!")
    private String singleUrl;
    /**
     * 卡片标题
     */
    @ApiModelProperty("卡片标题")
    private String title;
}