package com.capol.notify.admin.port.adapter.restapi.controller.auth.parameter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PasswordRequestParam {

    @ApiModelProperty(value = "账号", required = true)
    @NotNull(message = "账号不能为空")
    @NotBlank(message = "账号不能为空")
    private String account;

    @ApiModelProperty(value = "密码", required = true)
    @NotNull(message = "密码不能为空")
    @NotBlank(message = "密码不能为空")
    private String password;
}
