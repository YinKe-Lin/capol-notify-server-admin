package com.capol.notify.admin.port.adapter.restapi.controller.auth;

import com.capol.notify.admin.port.adapter.restapi.AuthorizedOperation;
import com.capol.notify.admin.port.adapter.restapi.controller.auth.parameter.UserRequestParam;
import com.capol.notify.manage.application.user.UserService;
import com.capol.notify.manage.application.user.querystack.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;


@Validated
@RestController
@RequestMapping("/api/v1.0/admin/user")
@Api(tags = "系统用户管理")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 添加用户
     *
     * @param request
     * @return
     */
    @AuthorizedOperation(name = "ADD-USER", key = "ADD-USER", description = "该权限将会检测当前用户是否为管理员")
    @ApiOperation("添加用户")
    @PostMapping("/account")
    public int insertAccount(@Validated @RequestBody UserRequestParam request) {
        UserDTO userDTO = new UserDTO();
        userDTO.setServiceId(request.getServiceId());
        userDTO.setServiceName(request.getServiceName());
        userDTO.setAccount(request.getAccount());
        userDTO.setPassword(request.getPassword());
        return userService.insertUser(userDTO);
    }

    /**
     * 批量删除用户
     *
     * @param userIds 用户IDs
     * @return
     */
    @AuthorizedOperation(name = "BATCH-DELETE-USER", key = "BATCH-DELETE-USER", description = "该权限将会检测当前用户是否为管理员")
    @ApiOperation("获取指定消息内容详情")
    @DeleteMapping("/batch")
    public Integer queueMessages(@NotEmpty(message = "用户ID不能为空") @RequestBody List<Long> userIds) {
        return userService.deleteUser(userIds);
    }

    /**
     * 禁用业务端ServiceID
     *
     * @param serviceId
     * @return
     */
    @AuthorizedOperation(name = "DISABLE-USER-SERVICE-ID", key = "DISABLE-USER-SERVICE-ID", description = "该权限将会检测当前用户是否为管理员")
    @ApiOperation("禁用业务端ServiceID")
    @PutMapping("/service-id/disable")
    public int disableAccount(@NotBlank(message = "ServiceID不能为空!") String serviceId) {
        return userService.disableServiceId(serviceId);
    }

    /**
     * 启用业务端ServiceID
     *
     * @param serviceId
     * @return
     */
    @AuthorizedOperation(name = "ENABLE-USER-SERVICE-ID", key = "ENABLE-USER-SERVICE-ID", description = "该权限将会检测当前用户是否为管理员")
    @ApiOperation("启用业务端ServiceID")
    @PutMapping("/service-id/enable")
    public int enableAccount(@NotBlank(message = "ServiceID不能为空!") String serviceId) {
        return userService.enableServiceId(serviceId);
    }
}
