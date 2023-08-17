package com.capol.notify.consumer.domain.model.wechat.sao;

import com.capol.notify.consumer.domain.model.wechat.sao.model.EmployeeDTO;
import com.capol.notify.consumer.domain.model.wechat.sao.response.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "bim-uc-server", fallback = EmployeeServiceFallback.class)
public interface IEmployeeServiceApi {
    @GetMapping(value = "/api/employee/selectById")
    EmployeeDTO selectById(@RequestParam(name = "id") Long id);

    @GetMapping("api/wx/wxUser/getWxAccountOpenId")
    ObjectResponse<String> getWxAccountOpenId(@RequestParam String phone);

}
