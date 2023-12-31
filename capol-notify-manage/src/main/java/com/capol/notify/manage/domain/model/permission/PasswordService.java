package com.capol.notify.manage.domain.model.permission;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

/**
 * 处理密码的领域服务
 */
@Service
public class PasswordService {

    public String generateSalt() {
        return RandomStringUtils.random(6, true, true);
    }

    public String encryptPassword(String plainPassword, String salt) {
        Validate.notBlank(plainPassword, "密码必须提供,且不能为空或空字符串.");
        return DigestUtils.sha256Hex(plainPassword + (salt == null ? "" : salt));
    }
}
