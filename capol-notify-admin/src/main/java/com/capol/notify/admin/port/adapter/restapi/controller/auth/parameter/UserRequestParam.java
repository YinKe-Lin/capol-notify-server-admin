package com.capol.notify.admin.port.adapter.restapi.controller.auth.parameter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserRequestParam {
    /**
     * 服务ID
     */
    @ApiModelProperty(value = "服务ID", required = true)
    @NotNull(message = "服务ID不能为空")
    @NotBlank(message = "服务ID不能为空")
    private String serviceId;

    /**
     * 服务名称
     */
    @ApiModelProperty(value = "服务名称", required = true)
    @NotNull(message = "服务名称不能为空")
    @NotBlank(message = "服务名称不能为空")
    private String serviceName;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号", required = true)
    @NotNull(message = "账号不能为空")
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 账号
     */
    @ApiModelProperty(value = "密码", required = true)
    @NotNull(message = "密码不能为空")
    @NotBlank(message = "密码不能为空")
    private String password;
}
