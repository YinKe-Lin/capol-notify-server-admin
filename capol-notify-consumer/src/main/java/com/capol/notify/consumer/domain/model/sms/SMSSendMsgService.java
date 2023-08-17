package com.capol.notify.consumer.domain.model.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.capol.notify.manage.domain.DomainException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.sdk.command.SMSMsgCommand;
import com.capol.notify.sdk.pojo.SMSMessageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * SMS短信消息发送领域服务
 */
@Slf4j
@Service
public class SMSSendMsgService {
    private SMSConfig smsConfig;
    private DefaultProfile profile;
    private IAcsClient iAcsClient;

    public SMSSendMsgService(SMSConfig smsConfig) {
        this.smsConfig = smsConfig;
        this.profile = DefaultProfile.getProfile("default", smsConfig.getAccessKeyId(), smsConfig.getAccessKeySecret());
        this.iAcsClient = new DefaultAcsClient(profile);
    }

    /**
     * 发送验证码短信
     *
     * @param phoneNumber
     * @param templateParam
     * @return
     * @throws Exception
     */
    public boolean sendCodeSms(String phoneNumber, String templateParam) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", smsConfig.getSignName());
        request.putQueryParameter("TemplateCode", SMSTemplateCode.TEMPLATE_CODE.getValue());
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + templateParam + "\"}");
        try {
            log.info("-->发送验证码短信参数：{}", JSONObject.toJSONString(request));
            CommonResponse response = iAcsClient.getCommonResponse(request);
            JSONObject responseJson = JSONObject.parseObject(response.getData());
            String code = (String) responseJson.get("Code");
            String message = (String) responseJson.get("Message");
            if ("OK".equals(code)) {
                log.info("-->短信发送成功,返回结果:{}", responseJson);
                return true;
            } else {
                log.error("-->sendSms failure 短信发送失败:{}", message);
                throw new DomainException("短信发送失败:" + message, EnumExceptionCode.InternalServerError);
            }
        } catch (ClientException e) {
            log.error("-->sendSms failure 短信发送异常:{}", e.getMessage());
            throw new DomainException("短信发送异常:" + e.getMessage(), EnumExceptionCode.InternalServerError);
        }
    }

    /**
     * 发送通知短信
     *
     * @param phoneNumber
     * @param templateParam
     * @return
     * @throws Exception
     */
    public boolean sendNoticeSms(String phoneNumber, String templateParam) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", smsConfig.getSignName());
        request.putQueryParameter("TemplateCode", SMSTemplateCode.TEMPLATE_CODE_NOTICE.getValue());
        request.putQueryParameter("TemplateParam", "{\"approvalResult\":\"" + templateParam + "\"}");
        try {
            log.info("-->发送通知短信参数：{}", JSONObject.toJSONString(request));
            CommonResponse response = iAcsClient.getCommonResponse(request);
            JSONObject responseJson = JSONObject.parseObject(response.getData());
            String code = (String) responseJson.get("Code");
            String message = (String) responseJson.get("Message");
            if ("OK".equals(code)) {
                log.info("-->通知短信发送成功,返回结果:{}", responseJson);
                return true;
            } else {
                log.error("-->sendSms failure 通知短信发送失败:{}", message);
                throw new DomainException("通知短信发送失败:" + message, EnumExceptionCode.InternalServerError);
            }
        } catch (ClientException e) {
            log.error("-->sendSms failure 短信发送异常：{}", e.getMessage());
            throw new DomainException("通知短信发送异常:" + e.getMessage(), EnumExceptionCode.InternalServerError);
        }
    }

    /**
     * 发送短信
     *
     * @param smsMsgCommand
     * @return
     * @throws Exception
     */
    public boolean sendSms(SMSMsgCommand smsMsgCommand) throws Exception {
        if (smsMsgCommand.getParamList().size() > 0) {
            CommonRequest request = new CommonRequest();
            request.setMethod(MethodType.POST);
            request.setDomain("dysmsapi.aliyuncs.com");
            request.setVersion("2017-05-25");
            request.setAction("SendSms");
            request.putQueryParameter("RegionId", "cn-hangzhou");
            request.putQueryParameter("PhoneNumbers", smsMsgCommand.getPhoneNumbers());
            request.putQueryParameter("SignName", smsConfig.getSignName());
            request.putQueryParameter("TemplateCode", smsMsgCommand.getTemplateCode());

            Map<String, String> paramMap = new HashMap();
            for (SMSMessageData param : smsMsgCommand.getParamList()) {
                paramMap.put(param.getParamName(), param.getParamValue());
            }
            request.putQueryParameter("TemplateParam", JSON.toJSONString(paramMap));
            try {
                log.info("-->发送短信参数：{}, smsMsgCommand参数：{}", JSONObject.toJSONString(request), JSONObject.toJSONString(smsMsgCommand));
                CommonResponse response = iAcsClient.getCommonResponse(request);
                JSONObject responseJson = JSONObject.parseObject(response.getData());
                String code = (String) responseJson.get("Code");
                String message = (String) responseJson.get("Message");
                if ("OK".equals(code)) {
                    log.info("-->短信发送成功,返回结果:{}", responseJson);
                    return true;
                } else {
                    log.error("-->sendSms failure 短信发送失败:{}", message);
                    throw new RuntimeException("短信发送失败:" + message);
                }
            } catch (ClientException e) {
                log.error("sendSms failure 短信发送异常：{}", e.getMessage());
                throw new DomainException("短信发送异常:" + e.getMessage(), EnumExceptionCode.InternalServerError);
            }
        } else {
            DefaultProfile profile = DefaultProfile.getProfile("default", smsConfig.getAccessKeyId(), smsConfig.getAccessKeySecret());
            IAcsClient client = new DefaultAcsClient(profile);

            CommonRequest request = new CommonRequest();
            request.setMethod(MethodType.POST);
            request.setDomain("dysmsapi.aliyuncs.com");
            request.setVersion("2017-05-25");
            request.setAction("SendSms");
            request.putQueryParameter("RegionId", "cn-hangzhou");
            request.putQueryParameter("PhoneNumbers", smsMsgCommand.getPhoneNumbers());
            request.putQueryParameter("SignName", smsConfig.getSignName());
            request.putQueryParameter("TemplateCode", smsMsgCommand.getTemplateCode());
            request.putQueryParameter("TemplateParam", smsMsgCommand.getTemplateParam());
            try {
                log.info("-->发送短信参数：{}, smsMsgCommand参数：{}", JSONObject.toJSONString(request), JSONObject.toJSONString(smsMsgCommand));
                CommonResponse response = client.getCommonResponse(request);
                JSONObject responseJson = JSONObject.parseObject(response.getData());
                String code = (String) responseJson.get("Code");
                String message = (String) responseJson.get("Message");
                if ("OK".equals(code)) {
                    log.info("-->短信发送成功,返回结果:{}", responseJson);
                    return true;
                } else {
                    log.error("-->sendSms failure 短信发送失败:{}", message);
                    throw new DomainException("短信发送失败:" + message, EnumExceptionCode.InternalServerError);
                }
            } catch (ClientException e) {
                log.error("-->sendSms failure 短信发送异常:{}", e.getMessage());
                throw new DomainException("短信发送异常:" + e.getMessage(), EnumExceptionCode.InternalServerError);
            }
        }
    }

    /**
     * 短信批量发送
     *
     * @param phoneNumberJson
     * @param signNameJson
     * @param templateCode
     * @param templateParamJson
     * @return
     * @throws Exception
     */
    public boolean sendBatchSms(String phoneNumberJson, String signNameJson, String templateCode, String templateParamJson) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendBatchSms");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("PhoneNumberJson", phoneNumberJson);
        request.putQueryParameter("SignNameJson", signNameJson);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParamJson", templateParamJson);
        try {
            log.info("-->批量发送短信参数：{}", JSONObject.toJSONString(request));
            CommonResponse response = iAcsClient.getCommonResponse(request);
            JSONObject responseJson = JSONObject.parseObject(response.getData());
            String code = (String) responseJson.get("Code");
            String message = (String) responseJson.get("Message");
            if ("OK".equals(code)) {
                log.info("-->批量短信发送成功,返回结果:{}", responseJson);
                return true;
            } else {
                log.error("-->sendBatchSms failure 批量短信发送失败:{}", message);
                throw new DomainException("批量短信发送失败:" + message, EnumExceptionCode.InternalServerError);
            }
        } catch (ClientException e) {
            log.error("-->sendBatchSms failure 批量短信发送异常:{}", e.getMessage());
            throw new DomainException("批量短信发送异常:" + e.getMessage(), EnumExceptionCode.InternalServerError);
        }
    }

    /**
     * 发送进度管理流程短信信息
     *
     * @param phoneNumber
     * @param contentProjectName
     * @param contentEventName
     * @param contentDayNum
     * @param templateParam
     * @return
     */
    public boolean sendProgressNotifyMsg(String phoneNumber, String contentProjectName, String contentEventName, Integer contentDayNum, String templateParam) {
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", smsConfig.getSignName());
        request.putQueryParameter("TemplateCode", templateParam);
        if (SMSTemplateCode.END_ADVANCE_EVENT_NOTIFICATION.getValue().equals(templateParam)) {
            request.putQueryParameter("TemplateParam", "{\"pname\":" + contentProjectName + ",\"event1\":" + contentEventName + ",\"day\":" + contentDayNum + "}");
        } else if (SMSTemplateCode.URGENCY_ADVANCE_EVENT_NOTIFICATION.getValue().equals(templateParam)) {
            request.putQueryParameter("TemplateParam", "{\"pname\":" + contentProjectName + ",\"event1\":" + contentEventName + "}");
        } else {
            request.putQueryParameter("TemplateParam", "{\"pname\":" + contentProjectName + ",\"event1\":" + contentEventName + "}");
        }
        log.info("{\"pname\":" + contentProjectName + ",\"event1\":" + contentEventName + ",\"day\":" + contentDayNum + "}");
        try {
            log.info("-->发送进度管理流程短信信息参数：{}", JSONObject.toJSONString(request));
            CommonResponse response = iAcsClient.getCommonResponse(request);
            JSONObject responseJson = JSONObject.parseObject(response.getData());
            String code = (String) responseJson.get("Code");
            String message = (String) responseJson.get("Message");
            if ("OK".equals(code)) {
                log.info("-->发送进度管理流程短信信息成功,返回结果:{}", responseJson);
                return true;
            } else {
                log.error("-->sendSms failure 发送进度管理流程短信信息失败：{}", message);
                throw new DomainException("发送进度管理流程短信信息失败:" + message, EnumExceptionCode.InternalServerError);
            }
        } catch (ClientException e) {
            log.error("-->sendSms failure 短信发送异常：{}", e.getMessage());
            throw new DomainException("发送进度管理流程短信信息异常:" + e.getMessage(), EnumExceptionCode.InternalServerError);
        }
    }
}
