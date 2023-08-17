package com.capol.notify.manage.application.user.querystack;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "业务系统用户Token信息")
@NoArgsConstructor
public class AuthenticateTokenDTO {

    @ApiModelProperty("Token")
    private String token;

    @ApiModelProperty("Token过期时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expiresTime;

    public AuthenticateTokenDTO(String token, LocalDateTime expiresTime) {
        this.token = token;
        this.expiresTime = expiresTime;
    }
}
