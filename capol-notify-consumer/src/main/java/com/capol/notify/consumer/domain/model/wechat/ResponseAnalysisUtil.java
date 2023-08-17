package com.capol.notify.consumer.domain.model.wechat;


import com.capol.notify.consumer.domain.model.wechat.sao.response.ObjectResponse;
import com.capol.notify.manage.domain.DomainException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import org.apache.http.HttpStatus;

import java.util.Objects;

public class ResponseAnalysisUtil {
    public static <T> T getData(ObjectResponse<T> objectResponse) {
        if (Objects.isNull(objectResponse)) {
            throw new DomainException("objectResponse is null", EnumExceptionCode.InternalServerError);
        }
        if (HttpStatus.SC_OK != objectResponse.getStatus()) {
            throw new DomainException(objectResponse.getMessage(), EnumExceptionCode.InternalServerError);
        }
        return objectResponse.getData();
    }
}