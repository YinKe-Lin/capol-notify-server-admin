package com.capol.notify.producer.port.adapter.restapi.controller.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.capol.notify.manage.application.ApplicationException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.producer.application.message.SendMessageService;
import com.capol.notify.producer.port.adapter.restapi.controller.message.parameter.SMSMessageRequestParam;
import com.capol.notify.sdk.EnumMessageType;
import com.capol.notify.sdk.command.SMSMsgCommand;
import com.capol.notify.sdk.command.SMSProcessNotifyMsgCommand;
import com.capol.notify.sdk.pojo.SMSMessageData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/api/v1.0/service/message/sms")
@Api(tags = "SMS短信消息发送服务")
public class SMSMessageSendController {

    private final SendMessageService sendMessageService;

    public SMSMessageSendController(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    /**
     * 发送验证码   内容不能超过20长度
     *
     * @param phoneNumber   手机号
     * @param templateParam 内容
     * @return
     * @author ：mingxing.zhang
     * @date ：Created in 2020/4/28 8:20
     * @modified By：
     */
    @ApiOperation("发送验证码短信")
    @PostMapping("/send-verify-code")
    public void sendCodeSms(@ApiParam(value = "接收短信的手机号码", required = true) @RequestParam("phoneNumber") String phoneNumber,
                            @ApiParam(value = "短信模板变量对应的实际值", required = true) @RequestParam("templateParam") String templateParam,
                            @ApiParam(value = "业务类型", required = true) @RequestParam("businessType") String businessType) {
        //长度验证
        validateSmsContentLength(templateParam);
        SMSMsgCommand smsMsgCommand = new SMSMsgCommand();
        smsMsgCommand.setPhoneNumbers(phoneNumber);
        smsMsgCommand.setTemplateParam(templateParam);
        smsMsgCommand.setMessageType(EnumMessageType.SMS_MESSAGE);
        smsMsgCommand.setBusinessType(businessType);
        smsMsgCommand.setPriority(1);
        sendMessageService.sendSMSMsg(smsMsgCommand);
    }

    @ApiOperation("发送通知短信")
    @PostMapping("/send-notice")
    public void sendNoticeSms(@ApiParam(value = "接收短信的手机号码", required = true) @RequestParam("phoneNumber") String phoneNumber,
                              @ApiParam(value = "短信模板变量对应的实际值", required = true) @RequestParam("templateParam") String templateParam,
                              @ApiParam(value = "业务类型", required = true) @RequestParam("businessType") String businessType) {
        //长度验证
        validateSmsContentLength(templateParam);
        SMSMsgCommand smsMsgCommand = new SMSMsgCommand();
        smsMsgCommand.setPhoneNumbers(phoneNumber);
        smsMsgCommand.setTemplateParam(templateParam);
        smsMsgCommand.setMessageType(EnumMessageType.SMS_MESSAGE);
        smsMsgCommand.setBusinessType(businessType);
        smsMsgCommand.setPriority(1);
        sendMessageService.sendSMSMsg(smsMsgCommand);
    }

    @ApiOperation("发送短信")
    @PostMapping("/send-message")
    public void sendSms(@RequestBody @Validated SMSMessageRequestParam messageRequestParam) throws Exception {
        //参数长度校验
        if (messageRequestParam.getParamList().size() > 0) {
            for (SMSMessageData param : messageRequestParam.getParamList()) {
                validateSmsContentLength(param.getParamValue());
            }
        } else {
            if (StringUtils.isNotBlank(messageRequestParam.getTemplateParam())) {
                validateSmsContentLength(messageRequestParam.getTemplateParam());
            }
        }
        SMSMsgCommand smsMsgCommand = new SMSMsgCommand();
        smsMsgCommand.setTemplateCode(messageRequestParam.getTemplateCode());
        smsMsgCommand.setPhoneNumbers(messageRequestParam.getPhoneNumbers());
        smsMsgCommand.setPhoneNumberJson(messageRequestParam.getPhoneNumbers());
        smsMsgCommand.setTemplateParam(messageRequestParam.getTemplateParam());
        smsMsgCommand.setTemplateParamJson(messageRequestParam.getTemplateParamJson());
        smsMsgCommand.setParamList(messageRequestParam.getParamList());
        smsMsgCommand.setSignName(messageRequestParam.getSignName());
        smsMsgCommand.setSignNameJson(messageRequestParam.getSignNameJson());
        smsMsgCommand.setMessageType(EnumMessageType.SMS_MESSAGE);
        //通用接口需要从业务端传入BusinessType, 否则消息平台需要枚举BusinessType, 会造成跟业务耦合。
        smsMsgCommand.setBusinessType(messageRequestParam.getBusinessType());
        smsMsgCommand.setPriority(messageRequestParam.getPriority());
        sendMessageService.sendSMSMsg(smsMsgCommand);
    }

    @ApiOperation("批量发送短信")
    @PostMapping("/send-batch-message")
    public void sendBatchSms(@RequestBody @Validated SMSMessageRequestParam messageRequestParam) throws Exception {
        SMSMsgCommand smsMsgCommand = new SMSMsgCommand();
        smsMsgCommand.setTemplateCode(messageRequestParam.getTemplateCode());
        smsMsgCommand.setPhoneNumberJson(messageRequestParam.getPhoneNumberJson());
        smsMsgCommand.setTemplateParamJson(messageRequestParam.getTemplateParamJson());
        smsMsgCommand.setSignNameJson(messageRequestParam.getSignNameJson());
        smsMsgCommand.setMessageType(EnumMessageType.SMS_BATCH_MESSAGE);
        //通用接口需要从业务端传入BusinessType, 否则消息平台需要枚举BusinessType, 会造成跟业务耦合。
        smsMsgCommand.setBusinessType(messageRequestParam.getBusinessType());
        smsMsgCommand.setPriority(2);
        sendMessageService.sendSMSMsg(smsMsgCommand);
    }


    /**
     * 发送进度管理流程短信信息
     *
     * @param phoneNumber        手机号
     * @param contentProjectName 内容中的项目名称
     * @param contentEventName   内容中的事件名称
     * @param contentDayNum      内容中的天数
     * @param templateParam      模板参数
     * @return
     */
    @ApiOperation("发送进度管理流程短信信息")
    @PostMapping("/send-progress-notify-message")
    public void sendProgressNotifyMsg(@RequestParam(value = "phoneNumber", required = true) String phoneNumber,
                                      @RequestParam(value = "contentProjectName", required = false) String contentProjectName,
                                      @RequestParam(value = "contentEventName", required = false) String contentEventName,
                                      @RequestParam(value = "contentDayNum", required = false) Integer contentDayNum,
                                      @RequestParam(value = "templateParam", required = true) String templateParam,
                                      @ApiParam(value = "业务类型", required = true) @RequestParam("businessType") String businessType) {

        SMSProcessNotifyMsgCommand notifyMsgCommand = new SMSProcessNotifyMsgCommand();
        notifyMsgCommand.setPhoneNumbers(phoneNumber);
        notifyMsgCommand.setTemplateParam(templateParam);
        notifyMsgCommand.setContentProjectName(contentProjectName);
        notifyMsgCommand.setContentEventName(contentEventName);
        notifyMsgCommand.setContentDayNum(contentDayNum);
        notifyMsgCommand.setMessageType(EnumMessageType.SMS_MESSAGE);
        notifyMsgCommand.setBusinessType(businessType);
        notifyMsgCommand.setPriority(1);
        sendMessageService.sendSMSMsg(notifyMsgCommand);
    }

    /**
     * 验证短信内容长度  一个参数不能超过20
     *
     * @param content 短信内容
     */
    private void validateSmsContentLength(String content) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        try {
            JSONObject jsonObject = JSON.parseObject(content);
            Collection<Object> values = jsonObject.values();
            values.forEach(key -> {
                validateContentString((String) key);
            });
        } catch (Exception e) {
            //证明不是JSON
            validateContentString(content);
        }
    }

    /**
     * 校验内容合法性
     *
     * @param content
     */
    private void validateContentString(String content) {
        if (StringUtils.isBlank(content)) {
            //存在内容是空的，当做异常
            throw new ApplicationException("内容不能为空：", EnumExceptionCode.BadRequest);
        }
        if (content.length() > 20) {
            throw new ApplicationException("内容长度过长，不能超过20个字符!", EnumExceptionCode.BadRequest);
        }
    }
}
