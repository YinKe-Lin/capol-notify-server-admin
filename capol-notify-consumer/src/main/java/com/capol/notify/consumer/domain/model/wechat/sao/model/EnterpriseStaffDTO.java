package com.capol.notify.consumer.domain.model.wechat.sao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseStaffDTO {
    private Long enterpriseId;
    private Long organizationId;
    private Long accountId;
    private Long employeeId;
    private String contacterName;
    private String phoneNo;
    private Long postId;
    private String specialityNo;
    private String specialityName;
    private String sapId;
    private Integer status;
    private Integer isDisable = 0;
    private String positions;
    private String email;
    private String oldPhoneNo;
}
