package com.capol.notify.producer.port.adapter.restapi.controller.message.parameter;

import com.capol.notify.sdk.pojo.SMSMessageData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 短信消息发送请求参数
 */
@ApiModel("短信消息发送请求参数")
@Data
public class SMSMessageRequestParam extends BaseRequestParam {

    @ApiModelProperty("接收短信的手机号码")
    private String phoneNumbers;

    @ApiModelProperty("短信签名名称")
    @NotNull(message = "短信签名不允许为空!")
    private String signName;

    @ApiModelProperty("短信模板ID")
    @NotNull(message = "短信模板ID不允许为空!")
    private String templateCode;

    @ApiModelProperty("短信模板参数")
    private String templateParam;

    /**
     * 示例：["15900000000","13500000000"]
     */
    @ApiModelProperty("接收短信的手机号码，JSON数组格式")
    private String phoneNumberJson;

    /**
     * 示例：["阿里云","阿里巴巴"]
     */
    @ApiModelProperty("短信签名名称，JSON数组格式")
    private String signNameJson;

    /**
     * 示例：[{"name":"TemplateParamJson"},{"name":"TemplateParamJson"}]
     */
    @ApiModelProperty("短信模板变量对应的实际值，JSON格式")
    private String templateParamJson;

    @ApiModelProperty("短信模板变量对象数组")
    private List<SMSMessageData> paramList = new ArrayList();
}
