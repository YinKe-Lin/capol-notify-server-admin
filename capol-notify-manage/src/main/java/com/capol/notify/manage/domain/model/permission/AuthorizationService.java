package com.capol.notify.manage.domain.model.permission;

import com.capol.notify.manage.domain.SystemConstants;
import com.capol.notify.manage.domain.model.user.UserDO;
import com.capol.notify.manage.domain.model.user.UserId;
import com.capol.notify.manage.domain.repository.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthorizationService {

    private final UserMapper userMapper;

    public AuthorizationService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 检查操作是否授权
     *
     * @param userId 操作用户ID
     */
    public boolean authorizedOperation(UserId userId) {
        if (userId == null) {
            return false;
        }
        //检查当前用户是否为管理员
        if (!SystemConstants.ADMIN_ID.equals(userId.getId())) {
            log.warn("-->无操作权限,该操作只限管理员!");
            return false;
        }
        log.info("-->【检查操作是否授权】操作用户ID：{}", userId);
        UserDO user = userMapper.findByUserId(userId);
        if (user == null) {
            return false;
        }
        return !user.getDisabled();
    }
}