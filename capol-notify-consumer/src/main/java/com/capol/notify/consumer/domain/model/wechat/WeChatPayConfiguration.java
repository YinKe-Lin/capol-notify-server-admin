package com.capol.notify.consumer.domain.model.wechat;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Data
@Configuration
@ConditionalOnClass(WxPayService.class)
@EnableConfigurationProperties(WeChatPayProperties.class)
@RequiredArgsConstructor
@Primary
public class WeChatPayConfiguration {
    private final WeChatPayProperties weChatPayProperties;

    @Bean("miniProgramWxMaService")
    public WxMaService wxMaService(){
        WxMaConfig wxMaConfig= new WxMaDefaultConfigImpl();
        ((WxMaDefaultConfigImpl)wxMaConfig).setAppid(StringUtils.trimToNull(weChatPayProperties.getAppId()));
        ((WxMaDefaultConfigImpl)wxMaConfig).setSecret(StringUtils.trimToNull(weChatPayProperties.getAppSecret()));
        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(wxMaConfig);
        return wxMaService;
    }

}
