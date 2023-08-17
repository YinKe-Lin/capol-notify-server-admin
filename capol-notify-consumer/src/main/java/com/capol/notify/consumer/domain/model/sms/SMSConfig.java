package com.capol.notify.consumer.domain.model.sms;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "sms")
public class SMSConfig {
    /**
     * 阿里云账户的 accessKeyId
     */
    private String accessKeyId;

    /**
     * 阿里云账户的 accessKeySecret
     */
    private String accessKeySecret;

    /**
     * 短信签名 iBIM、CAPOL
     */
    private String signName;
}
