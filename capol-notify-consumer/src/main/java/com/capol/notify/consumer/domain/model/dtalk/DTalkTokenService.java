package com.capol.notify.consumer.domain.model.dtalk;

import com.capol.notify.consumer.DTalkConfig;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiGetJsapiTicketRequest;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGetJsapiTicketResponse;
import com.dingtalk.api.response.OapiGettokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DTalkTokenService {
    private DTalkConfig dTalkConfig;
    private RedisService redisService;

    public DTalkTokenService(DTalkConfig dTalkConfig, RedisService redisService) {
        this.dTalkConfig = dTalkConfig;
        this.redisService = redisService;
    }

    public String getDingToken() throws Exception {

        DefaultDingTalkClient client = new DefaultDingTalkClient(dTalkConfig.getTokenUrl());
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(dTalkConfig.getCorpId());
        request.setAppsecret(dTalkConfig.getCorpSecret());
        request.setHttpMethod("GET");

        OapiGettokenResponse response = client.execute(request);

        //代表执行成功
        if (response.getErrcode() == 0) {
            redisService.set("notify:dtalk:token", response.getAccessToken(), 120L);
            return response.getAccessToken();
        }

        throw new IllegalStateException("token get failure !");
    }

    public void clearDingToken() {
        redisService.delete("notify:dtalk:token");
    }

    public String getDingJsTicket() throws Exception {

        DefaultDingTalkClient client = new DefaultDingTalkClient(dTalkConfig.getJsapiTicketUrl());
        OapiGetJsapiTicketRequest req = new OapiGetJsapiTicketRequest();
        req.setTopHttpMethod("GET");
        OapiGetJsapiTicketResponse response = client.execute(req, getDingToken());

        //代表执行成功
        if (response.getErrcode() == 0) {
            redisService.set("notify:dtalk:jsticket", response.getTicket(), 120L);
            return response.getTicket();
        }

        throw new IllegalStateException("jsticket get failure !");

    }

    public void clearDingJsTicket() {
        redisService.delete("notify:dtalk:jsticket");
    }

    public String getRedisDingToken() {
        return redisService.get("notify:dtalk:token");
    }

    public String getRedisDingJsTicket() {
        return redisService.get("notify:dtalk:jsticket");
    }

}