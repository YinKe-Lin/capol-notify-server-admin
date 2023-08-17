package com.capol.notify.consumer.domain.model.wechat.sao;

import com.capol.notify.consumer.domain.model.wechat.sao.model.EnterpriseStaffDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(
        value = "enterprise-manage-server",
        fallback = EnterpriseStaffFallback.class
)
public interface IEnterpriseStaffApi {
    @GetMapping({"/api/enterpriseStaff/{id}"})
    EnterpriseStaffDTO selectEnterpriseStaffById(@PathVariable Long id);

}
