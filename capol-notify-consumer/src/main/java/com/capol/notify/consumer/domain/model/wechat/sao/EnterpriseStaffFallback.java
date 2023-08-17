package com.capol.notify.consumer.domain.model.wechat.sao;

import com.capol.notify.consumer.domain.model.wechat.sao.model.EnterpriseStaffDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnterpriseStaffFallback implements IEnterpriseStaffApi{
    @Override
    public EnterpriseStaffDTO selectEnterpriseStaffById(Long id) {
        log.error("-->调用IEnterpriseStaffApi.selectEnterpriseStaffById 异常, ID:{}", id);
        return null;
    }
}
