package com.capol.notify.manage.application.user;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.capol.notify.manage.application.ApplicationException;
import com.capol.notify.manage.application.user.querystack.AuthenticateTokenDTO;
import com.capol.notify.manage.application.user.querystack.UserDTO;
import com.capol.notify.manage.application.user.querystack.UserInfoDTO;
import com.capol.notify.manage.application.user.querystack.UserQueueDTO;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.manage.domain.model.IdGenerator;
import com.capol.notify.manage.domain.model.permission.*;
import com.capol.notify.manage.domain.model.user.UserDO;
import com.capol.notify.manage.domain.model.user.UserId;
import com.capol.notify.manage.domain.repository.UserMapper;
import com.capol.notify.manage.domain.repository.UserQueueMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务
 *
 * @author heyong
 * @since 2023-08-02 14:52:21
 */
@Slf4j
@Service
public class UserService {

    private final TokenService tokenService;
    private final AuthenticationService authenticationService;
    private final PasswordService passwordService;
    private final UserMapper userMapper;
    private final UserQueueMapper userQueueMapper;

    public UserService(TokenService tokenService,
                       AuthenticationService authenticationService,
                       PasswordService passwordService,
                       UserMapper userMapper,
                       UserQueueMapper userQueueMapper) {
        this.tokenService = tokenService;
        this.authenticationService = authenticationService;
        this.passwordService = passwordService;
        this.userMapper = userMapper;
        this.userQueueMapper = userQueueMapper;
    }

    /**
     * 通过账号密码登录
     *
     * @param account  账号
     * @param password 密码
     * @return 登录Token
     */
    @Transactional(readOnly = true)
    public AuthenticateTokenDTO authenticateByPassword(String account, String password) {
        UserDescriptor descriptor = authenticationService.authenticateByPassword(account, password);
        if (descriptor == null) {
            throw new ApplicationException(
                    String.format("登录失败,账户: %s不存在!", account),
                    EnumExceptionCode.UserNotExists);
        }

        if (log.isDebugEnabled()) {
            log.debug(">>>>>> 业务系统用户 {}({}) 通过密码方式访问消息服务.",
                    descriptor.getName(),
                    descriptor.getUserId());
        }
        AuthenticateToken token = tokenService.generateToken(descriptor);
        return new AuthenticateTokenDTO(
                token.getToken(),
                token.getExpiresTime());
    }

    /**
     * 刷新Token
     *
     * @param oldToken
     * @return
     */
    public String refreshToken(String oldToken) {
        return tokenService.refreshToken(oldToken);
    }

    /**
     * 新增用户
     *
     * @param userDTO
     * @return
     */
    public int insertUser(UserDTO userDTO) {
        if (userDTO.getUserId() == null) {
            userDTO.setUserId(IdGenerator.generateId().toString());
        }
        UserDO userDO = new UserDO(
                new UserId(userDTO.getUserId()),
                userDTO.getAccount(),
                userDTO.getServiceId(),
                userDTO.getServiceName(),
                StringUtils.isBlank(userDTO.getPassword()) ? "123456" : userDTO.getPassword(),
                passwordService);
        userDO.buildBaseInfo();

        int rows = userMapper.insert(userDO);
        if (rows > 0) {
            log.info("-->插入用户:{}成功!", userDO.getAccount());
        } else {
            log.error("-->插入用户:{} 失败 , 用户详情:{} ", userDO.getAccount(), JSONObject.toJSONString(userDO));
        }
        return rows;
    }

    /**
     * 删除用户
     *
     * @param userIds
     * @return
     */
    public int deleteUser(List<Long> userIds) {
        int rows = 0;
        if (CollectionUtils.isEmpty(userIds)) {
            log.warn("-->指的删除条件userIds={},不能为空!", userIds);
            return rows;
        }
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserDO::getId, userIds);
        List<UserDO> userDOS = userMapper.selectList(queryWrapper);
        rows = userMapper.deleteBatchIds(userDOS);
        return rows;
    }

    /**
     * 获取业务系统身份及队列信息
     *
     * @param anUserId 用户ID
     */
    @Transactional(readOnly = true)
    public UserInfoDTO userInfo(String anUserId) {
        UserDO user = userMapper.findByUserId(new UserId(anUserId));
        if (user == null) {
            throw new ApplicationException(
                    String.format("账户ID:<%s>不存在!", anUserId),
                    EnumExceptionCode.UserNotExists);
        }
        return new UserInfoDTO(
                user.getUserId().getId(),
                user.getAccount(),
                user.getServiceName(),
                userQueueMapper.findByUserId(user.getUserId()).stream()
                        .map(queue -> new UserQueueDTO(
                                queue.getId(),
                                queue.getUserId(),
                                queue.getExchange(),
                                queue.getRouting(),
                                queue.getQueue(),
                                queue.getPriority(),
                                queue.getTtl(),
                                queue.getBusinessType(),
                                queue.getDisabled()
                        )).collect(Collectors.toList())
        );
    }

    /**
     * 获取业务系统身份信息
     *
     * @param anUserId 用户ID
     */
    @Transactional(readOnly = true)
    public UserDTO userBaseInfo(String anUserId) {
        UserDO user = userMapper.findByUserId(new UserId(anUserId));
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getUserId().getId(),
                user.getAccount(),
                user.getPassword(),
                user.getServiceId(),
                user.getServiceName(),
                user.getDisabled());
    }

    /**
     * 根据ServiceID获取业务系统身份信息
     *
     * @param serviceId
     * @return
     */
    @Transactional(readOnly = true)
    public UserDTO userServiceInfo(String serviceId) {
        UserDO user = userMapper.findByServiceId(serviceId);
        if (user == null) {
            throw new ApplicationException(
                    String.format("服务ID:<%s>不存在!", serviceId),
                    EnumExceptionCode.UserNotExists);
        }
        if (user.getDisabled()) {
            throw new ApplicationException(
                    String.format("服务ID:<%s>已经被禁用!", serviceId),
                    EnumExceptionCode.Forbidden);
        }
        return new UserDTO(
                user.getUserId().getId(),
                user.getAccount(),
                user.getPassword(),
                user.getServiceId(),
                user.getServiceName(),
                user.getDisabled());
    }

    /**
     * 根据ServiceID获取业务系统身份信息
     *
     * @param serviceId
     * @return
     */
    @Transactional(readOnly = true)
    public UserDescriptor userDescriptorInfo(String serviceId) {
        UserDO user = userMapper.findByServiceId(serviceId);
        if (user == null) {
            log.warn("-->服务ID:<{}>不存在!", serviceId);
            throw new ApplicationException(
                    String.format("服务ID:<%s>不存在!", serviceId),
                    EnumExceptionCode.UserNotExists);
        }
        if (user.getDisabled()) {
            log.warn("-->服务ID:<{}>已经被禁用!", serviceId);
            throw new ApplicationException(
                    String.format("服务ID:<%s>已经被禁用!", serviceId),
                    EnumExceptionCode.Forbidden);
        }
        return user.userDescriptor();
    }

    /**
     * 禁用指定ServiceID,禁用后无法使用队列发送消息
     *
     * @param serviceId
     * @return
     */
    public int disableServiceId(String serviceId) {
        int rows = 0;
        UserDO user = userMapper.findByServiceId(serviceId);
        if (user != null) {
            rows = userMapper.disableUserServiceId(user.getServiceId());
            if (rows > 0) {
                log.info("-->ServiceId:【{}】禁用成功!", serviceId);
            } else {
                log.warn("-->ServiceId:【{}】禁用失败!", serviceId);
            }
        } else {
            log.warn("-->禁用失败,ServiceId:【{}】不存在!", serviceId);
        }
        return rows;
    }

    /**
     * 启用指定ServiceID
     *
     * @param serviceId
     * @return
     */
    public int enableServiceId(String serviceId) {
        int rows = 0;
        UserDO user = userMapper.findByServiceId(serviceId);
        if (user != null) {
            rows = userMapper.enableUserServiceId(user.getServiceId());
            if (rows > 0) {
                log.info("-->ServiceId:【{}】启用成功!", serviceId);
            } else {
                log.warn("-->ServiceId:【{}】启用失败!", serviceId);
            }
        } else {
            log.warn("-->启用失败,ServiceId:【{}】不存在!", serviceId);
        }
        return rows;
    }
}