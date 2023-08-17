package com.capol.notify.consumer.domain.model.wechat.sao.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseResponse {
    private int status = 200;
    private String message;

    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public BaseResponse(String message) {
        this.message = message;
    }

    public BaseResponse() {
    }
}
