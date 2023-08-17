package com.capol.notify.consumer.domain.model.wechat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatMaTemplateMessage extends WeChatErrorMessage implements Serializable {
    private static final long serialVersionUID = -8445943548965154778L;

    private Long msgid;

    private Integer type;

    private Long stallId;

    private String touser;

    private String template_id;

    private String topcolor;

    private Map<String, MsgData> data;

    private Map<String, String> miniprogram;

    public WeChatMaTemplateMessage putData(String key, WeChatMaTemplateMessage.MsgData msgData) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        data.put(key, msgData);
        return this;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MsgData implements Serializable {
        private static final long serialVersionUID = 1L;

        private String color;
        private String value;
    }
}
