package com.capol.notify.producer.port.adapter.restapi.controller.message.parameter;

import com.capol.notify.sdk.EnumMessageContentType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel("钉钉消息发送请求参数")
@Data
@NoArgsConstructor
public class DTalkMessageRequestParam extends BaseRequestParam {

    @ApiModelProperty("消息内容类型")
    @NotNull(message = "消息内容类型不允许为空!")
    private EnumMessageContentType contentType;

    /**
     * 普通消息用户ID
     */
    @ApiModelProperty("接收者的用户userIds列表，最大列表长度100")
    private List<String> userIds;
    /**
     * 群会话ID
     */
    @ApiModelProperty("接收者的用户群会话ID")
    private String chatId;

    @ApiModelProperty("应用agentId")
    private Long agentId;

    @ApiModelProperty("消息内容")
    @NotNull(message = "消息内容不允许为空!")
    @NotBlank(message = "消息内容不允许为空!")
    private String content;

    @ApiModelProperty("OA消息内容")
    private OAContentRequestParam oaContent;

    @ApiModelProperty("ActionCard消息内容")
    private ActionCardRequestParam actionCard;
}
