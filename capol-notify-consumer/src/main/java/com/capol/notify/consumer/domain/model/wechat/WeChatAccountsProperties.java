package com.capol.notify.consumer.domain.model.wechat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Data
@Primary
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "wx.accounts")
public class WeChatAccountsProperties {
        private String appId;
        private String appSecret;
}
