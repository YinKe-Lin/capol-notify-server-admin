package com.capol.notify.consumer.domain.model.wechat;

import com.capol.notify.manage.domain.DomainException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.google.common.base.Joiner;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 微信发送API
 */
public enum RequestWxServerEnums {
    getAccountsAllUserList("https://api.weixin.qq.com/cgi-bin/user/get", HttpMethod.GET, null),
    getAccountsUserInfo("https://api.weixin.qq.com/cgi-bin/user/info", HttpMethod.GET, new String[]{"openid"}),
    sendTemplateMessage("https://api.weixin.qq.com/cgi-bin/message/template/send", HttpMethod.POST, null);

    private String url;
    private HttpMethod httpMethod;
    private String[] parameterNameArray;


    RequestWxServerEnums(String url, HttpMethod httpMethod, String[] parameterNameArray) {
        this.url = url;
        this.httpMethod = httpMethod;
        this.parameterNameArray = parameterNameArray;
    }

    public String getUrl() {
        return url;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String[] getParameterNameArray() {
        return parameterNameArray;
    }

    public static String getParameterStr(RequestWxServerEnums requestWxServerEnum, String... parameterValueArray) {
        if (Objects.isNull(requestWxServerEnum.parameterNameArray) || requestWxServerEnum.parameterNameArray.length == 0) {
            return null;
        }
        if (Objects.isNull(parameterValueArray)) {
            throw new DomainException("缺失参数", EnumExceptionCode.InternalServerError);
        }
        if (requestWxServerEnum.parameterNameArray.length != parameterValueArray.length) {
            throw new DomainException("缺失参数", EnumExceptionCode.InternalServerError);
        }
        Map<String, String> parameterMap = new HashMap<>();
        for (int i = 0; i < requestWxServerEnum.parameterNameArray.length; i++) {
            parameterMap.put(requestWxServerEnum.parameterNameArray[i], parameterValueArray[i]);
        }
        return Joiner.on("&").withKeyValueSeparator("=").join(parameterMap);
    }
}
