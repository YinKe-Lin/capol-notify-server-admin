package com.capol.notify.sdk.command;

import com.capol.notify.sdk.EnumMessageType;
import com.capol.notify.sdk.pojo.WeChatMessageData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatMsgCommand extends BaseMsgCommand {

    private static final long serialVersionUID = -6643212454457854215L;

    private Long id;

    /**
     * 人员ID（员工ID）
     */
    private Long stallId;

    /**
     * 消息接收用户OpenID
     */
    private String touser;

    /**
     * "消息模板id
     */
    private String template_id;

    /**
     * 主题色
     */
    private String topcolor;

    /**
     * data 不能为空
     */
    private Map<String, WeChatMessageData> data;

    private Map<String, String> miniprogram;

    private Long runStartTime;

    public WeChatMsgCommand(Long stallId, String templateId, Map<String, WeChatMessageData> data, Map<String, String> miniProgram, Integer priority,
                            EnumMessageType messageType, String businessType) {
        this.setStallId(stallId);
        this.setTemplate_id(templateId);
        this.setData(data);
        this.setMiniprogram(miniProgram);
        this.setPriority(priority);
        this.setMessageType(messageType);
        this.setBusinessType(businessType);
    }
}