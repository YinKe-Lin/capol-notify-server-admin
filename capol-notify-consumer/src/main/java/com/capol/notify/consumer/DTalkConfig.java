package com.capol.notify.consumer;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ding-talk")
public class DTalkConfig {
    /**
     * 钉钉的corpId
     */
    private String corpId;

    /**
     * 钉钉的CorpSecret
     */
    private String corpSecret;

    /**
     * 获取Tokenurl
     */
    private String tokenUrl;

    /**
     * 获取jsapi_ticket
     */
    private String jsapiTicketUrl;

    /**
     * 发送消息URL
     */
    private String sendPersonTextUrl;

    /**
     * 发送群消息url
     */
    private String sendGroupTextUrl;
    /**
     * content保存在redis中的时间
     */
    private Integer saveContentHour;
    /**
     * content保存在redis中的前缀
     */
    private String saveDtalkGroupKey;

    /***
     * 钉钉上传媒体文件url
     */
    private String mediaUploadUrl;

}