package com.capol.notify.consumer.domain.model.wechat.sao.response;

import lombok.Data;

@Data
public class ObjectResponse<T> extends BaseResponse {
    private T data;
    private boolean rel;

    public ObjectResponse rel(boolean rel) {
        this.setRel(rel);
        return this;
    }

    public ObjectResponse data(T data) {
        this.setData(data);
        return this;
    }

    public void success(T data) {
        this.rel = true;
        this.setData(data);
    }
}
