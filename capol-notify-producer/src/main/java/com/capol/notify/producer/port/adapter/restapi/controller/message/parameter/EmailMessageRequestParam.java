package com.capol.notify.producer.port.adapter.restapi.controller.message.parameter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Email消息发送请求参数
 */
@ApiModel("Email消息发送请求参数")
@Data
@NoArgsConstructor
public class EmailMessageRequestParam extends BaseRequestParam {
    @ApiModelProperty("发件人邮箱")
    private String sender;

    @ApiModelProperty("发件人名称")
    private String username;

    @ApiModelProperty("发件人昵称")
    private String senderNick;

    @ApiModelProperty("发件人邮箱密码")
    private String password;

    @ApiModelProperty("主题")
    private String subject;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("收件人")
    @NotNull(message = "收件人不允许为空!")
    @NotEmpty(message = "收件人不允许为空!")
    private String[] to;

    @ApiModelProperty("抄送人")
    private String[] cc;
}
