package com.capol.notify.producer.port.adapter.restapi.controller.message.parameter;

import com.capol.notify.sdk.pojo.WeChatMessageData;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@ApiModel("微信消息发送请求参数")
@Data
@NoArgsConstructor
public class WeChatMessageRequestParam extends BaseRequestParam {
    @NotNull(message = "人员id不能为空")
    private Long stallId;

    private String toUser;

    @NotBlank(message = "消息模板id 不能为空")
    private String templateId;

    /**
     * 主题色
     */
    private String topColor;
    /**
     * 消息体
     */
    @NotNull(message = "微信消息内容不能为空")
    private Map<String, WeChatMessageData> data;
    /**
     * 消息跳转参数
     */
    private Map<String, String> miniProgram;

    private Long runStartTime;


    public WeChatMessageRequestParam putData(String key, WeChatMessageData msgData) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        data.put(key, msgData);
        return this;
    }
}
