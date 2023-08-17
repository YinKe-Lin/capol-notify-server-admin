package com.capol.notify.consumer.domain.model.wechat;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.json.WxMaGsonBuilder;
import com.alibaba.fastjson.JSONObject;
import com.capol.notify.consumer.domain.model.wechat.sao.IEmployeeServiceApi;
import com.capol.notify.consumer.domain.model.wechat.sao.IEnterpriseStaffApi;
import com.capol.notify.consumer.domain.model.wechat.sao.model.EmployeeDTO;
import com.capol.notify.consumer.domain.model.wechat.sao.model.EnterpriseStaffDTO;
import com.capol.notify.consumer.domain.model.wechat.sao.response.ObjectResponse;
import com.capol.notify.manage.domain.DomainException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.sdk.command.WeChatMsgCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Objects;

/**
 * 微信消息发送领域服务
 */
@Slf4j
@Service
public class WeChatSendMsgService {
    private WxMaService miniProgramWxMaService;
    private WxMaService wxAccountsWxMaService;

    private IEmployeeServiceApi employeeServiceApi;
    private IEnterpriseStaffApi enterpriseStaffApi;

    public WeChatSendMsgService(@Qualifier("miniProgramWxMaService") WxMaService miniProgramWxMaService,
                                @Qualifier("wxAccountsWxMaService") WxMaService wxAccountsWxMaService,
                                IEmployeeServiceApi employeeServiceApi,
                                IEnterpriseStaffApi enterpriseStaffApi) {
        this.miniProgramWxMaService = miniProgramWxMaService;
        this.wxAccountsWxMaService = wxAccountsWxMaService;
        this.employeeServiceApi = employeeServiceApi;
        this.enterpriseStaffApi = enterpriseStaffApi;
    }

    /**
     * 发送消息
     *
     * @param weChatMsgCommand
     * @return
     */
    public String sendWeChatMsg(WeChatMsgCommand weChatMsgCommand) {
        //1. 根据人员ID（员工ID）获取手机号码
        log.info("-->根据StallId:{}, 获取手机号码!", weChatMsgCommand.getStallId());
        String phone = getPhone(weChatMsgCommand.getStallId());
        if (StringUtils.isEmpty(phone)) {
            log.error("-->sendTemplateMsg手机号为空 stallId = {}", weChatMsgCommand.getStallId());
            return String.format("用户stallId = %s,手机号码为空,无法发送微信消息!", weChatMsgCommand.getStallId());
        }
        //2. 根据手机号获取微信openid
        log.info("-->根据手机号:{}, 获取微信openid!", phone);
        ObjectResponse<String> wxAccountOpenId = employeeServiceApi.getWxAccountOpenId(phone);
        String openid = ResponseAnalysisUtil.getData(wxAccountOpenId);
        if (Objects.isNull(openid)) {
            log.error("-->sendTemplateMsg openid 为空!!, 人员ID（stallId）:{}", weChatMsgCommand.getStallId());
            return String.format("用户stallId = %s,openid为空,无法发送微信消息!");
        }
        log.info("-->根据手机号:{}, 获取到微信openid：{} ", phone, openid);
        weChatMsgCommand.setTouser(openid);
        Map<String, String> miniProgram = weChatMsgCommand.getMiniprogram();
        miniProgram.put("appid", miniProgramWxMaService.getWxMaConfig().getAppid());
        //3. 发送消息
        log.info("-->开始发送微信消息,WeChatMsgCommand参数:{}", JSONObject.toJSONString(weChatMsgCommand));
        WeChatMaTemplateMessage response = sendPostRequest(RequestWxServerEnums.sendTemplateMessage, weChatMsgCommand, WeChatMaTemplateMessage.class);
        Assert.isTrue(!(Objects.isNull(response) || Objects.isNull(response.getMsgid())), "发送公众号模板消息失败!");
        log.info("-->发送微信消息完成,返回结果:{}", JSONObject.toJSONString(response));
        return null;
    }

    /**
     * 调用微信SDK发送请求
     *
     * @param requestWxServerEnum
     * @param body
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> T sendPostRequest(RequestWxServerEnums requestWxServerEnum, Object body, Class<T> clazz) {
        String resultStr;
        try {
            resultStr = wxAccountsWxMaService.post(requestWxServerEnum.getUrl(), body);
            log.info("-->请求微信服务接口成功,返回结果：{}", resultStr);
        } catch (Exception e) {
            log.error("-->请求微信服务接口失败, 异常信息：{}", e);
            throw new DomainException(e.getMessage(), EnumExceptionCode.InternalServerError);
        }
        return WxMaGsonBuilder.create().fromJson(resultStr, clazz);
    }

    /**
     * 根据员工ID查询手机号码
     *
     * @param stallId
     * @return
     */
    public String getPhone(Long stallId) {
        EmployeeDTO employeeDTO = employeeServiceApi.selectById(stallId);
        if (Objects.nonNull(employeeDTO)) {
            return employeeDTO.getMobilePhone();
        }
        EnterpriseStaffDTO enterpriseStaffDTO = enterpriseStaffApi.selectEnterpriseStaffById(stallId);
        Assert.notNull(enterpriseStaffDTO, "未找到对应人员(EnterpriseStaff)：" + stallId);
        return enterpriseStaffDTO.getPhoneNo();
    }
}