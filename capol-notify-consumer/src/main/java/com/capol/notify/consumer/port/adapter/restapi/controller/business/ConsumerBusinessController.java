package com.capol.notify.consumer.port.adapter.restapi.controller.business;

import com.capol.notify.consumer.domain.model.wechat.WeChatSendMsgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1.0/service")
@Api(value = "/api/v1.0/service", tags = "消费者业务服务接口")
@RestController
@Slf4j
public class ConsumerBusinessController {

    private WeChatSendMsgService weChatSendMsgService;

    public ConsumerBusinessController(WeChatSendMsgService weChatSendMsgService) {
        this.weChatSendMsgService = weChatSendMsgService;
    }

    @ApiOperation("根据人员信息ID获取人员详细信息")
    @GetMapping("/staff/{stallId}")
    public String getEnterpriseStaffInfo(@PathVariable Long stallId) {
        return weChatSendMsgService.getPhone(stallId);
    }
}
