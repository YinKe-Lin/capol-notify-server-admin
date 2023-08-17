package com.capol.notify.sdk.command;

import com.capol.notify.sdk.pojo.SMSMessageData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSMsgCommand extends BaseMsgCommand {

    private static final long serialVersionUID = -6643212454457854288L;

    private Long id;

    /**
     * 接收短信的手机号码
     */
    private String phoneNumbers;

    /**
     * 短信签名名称
     */
    private String signName;

    /**
     * 短信模板ID
     */
    private String templateCode;

    /**
     * 短信模板参数
     */
    private String templateParam;

    /**
     * 接收短信的手机号码，JSON数组格式
     * 示例：["15900000000","13500000000"]
     */
    private String phoneNumberJson;

    /**
     * 短信签名名称，JSON数组格式
     * 示例：["iBIM","CAPOL"]
     */
    private String signNameJson;

    /**
     * 短信模板变量对应的实际值，JSON格式
     * 示例：[{"name":"TemplateParamJson"},{"name":"TemplateParamJson"}]
     */
    private String templateParamJson;

    /**
     * 短信模板变量对象数组
     */
    private List<SMSMessageData> paramList = new ArrayList();
}
