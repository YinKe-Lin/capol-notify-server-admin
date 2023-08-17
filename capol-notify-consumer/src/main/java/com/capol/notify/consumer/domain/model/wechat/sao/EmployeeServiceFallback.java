package com.capol.notify.consumer.domain.model.wechat.sao;

import com.capol.base.response.ObjectResponse;
import com.capol.notify.consumer.domain.model.wechat.sao.model.EmployeeDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmployeeServiceFallback implements IEmployeeServiceApi {
    @Override
    public EmployeeDTO selectById(Long id) {
        log.error("-->调用IEmployeeServiceApi.selectById 异常, ID:{}", id);
        return null;
    }

    @Override
    public ObjectResponse<String> getWxAccountOpenId(String phone) {
        log.error("-->调用IEmployeeServiceApi.getWxAccountOpenId方法异常,参数phone:{}", phone);
        return null;
    }
}
