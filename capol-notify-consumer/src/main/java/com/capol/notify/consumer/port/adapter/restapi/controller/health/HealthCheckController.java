package com.capol.notify.consumer.port.adapter.restapi.controller.health;

import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1.0/service")
@Api(value = "/api/v1.0/service", tags = "Consul服务健康检测")
@RestController
@Slf4j
public class HealthCheckController {
    @ApiOperation("消费者健康状态检测")
    @GetMapping("/check-health")
    public String check() {
        return String.format("消费者服务健康存活 Service is alive!!!! 检测时间:%s", DateUtil.dateSecond());
    }
}
