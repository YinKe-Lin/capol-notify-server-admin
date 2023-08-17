package com.capol.notify.consumer.domain.model.wechat;

import com.alibaba.fastjson.JSONObject;
import com.capol.notify.manage.domain.DomainException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

@Data
@Slf4j
public class WeChatErrorMessage implements Serializable {
    private static final long serialVersionUID = 2393269568666085259L;

    private Integer errcode;

    private String errmsg;

    public void isFail(WeChatErrorMessage weChatErrorMessage) {
        if (Objects.isNull(weChatErrorMessage.getErrcode())) {
            log.error("-->请求微信失败 : {}", JSONObject.toJSONString(weChatErrorMessage));
            throw new DomainException("请求微信失败，请联系管理员!", EnumExceptionCode.InternalServerError);
        }
    }
}
