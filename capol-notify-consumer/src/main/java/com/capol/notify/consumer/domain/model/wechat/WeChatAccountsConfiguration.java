package com.capol.notify.consumer.domain.model.wechat;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.capol.base.utils.StringUtil;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(WxPayService.class)
@EnableConfigurationProperties(WeChatAccountsProperties.class)
@RequiredArgsConstructor
public class WeChatAccountsConfiguration {
    private final WeChatAccountsProperties weChatAccountsProperties;

    @Bean("wxAccountsWxMaService")
    public WxMaService wxMaService(){
        WxMaConfig wxMaConfig= new WxMaDefaultConfigImpl();
        ((WxMaDefaultConfigImpl)wxMaConfig).setAppid(StringUtil.trimToNull(weChatAccountsProperties.getAppId()));
        ((WxMaDefaultConfigImpl)wxMaConfig).setSecret(StringUtil.trimToNull(weChatAccountsProperties.getAppSecret()));
        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(wxMaConfig);
        return wxMaService;
    }
}
