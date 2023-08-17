package com.capol.notify.manage.domain.repository;

import com.capol.notify.manage.domain.model.user.UserDO;
import com.capol.notify.manage.domain.model.user.UserId;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapperX<UserDO> {
    UserDO findByUserId(UserId userId);

    UserDO findByServiceId(@Param("serviceId") String serviceId);

    UserDO findByAccount(@Param("account") String account);

    int insertUser(@Param("userDO") UserDO userDO);

    int disableUserServiceId(@Param("serviceId") String serviceId);

    int enableUserServiceId(@Param("serviceId") String serviceId);
}
