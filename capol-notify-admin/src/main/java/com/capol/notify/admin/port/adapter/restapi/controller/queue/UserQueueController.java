package com.capol.notify.admin.port.adapter.restapi.controller.queue;

import com.capol.notify.admin.port.adapter.restapi.AuthorizedOperation;
import com.capol.notify.admin.port.adapter.restapi.controller.queue.parameter.UserQueueRequestParam;
import com.capol.notify.manage.application.queue.QueueService;
import com.capol.notify.manage.application.user.UserService;
import com.capol.notify.manage.application.user.querystack.UserInfoDTO;
import com.capol.notify.manage.application.user.querystack.UserQueueDTO;
import com.capol.notify.manage.domain.PageParam;
import com.capol.notify.manage.domain.PageResult;
import com.capol.notify.manage.domain.model.IdGenerator;
import com.capol.notify.manage.domain.model.permission.CurrentUserService;
import com.capol.notify.manage.domain.model.user.UserId;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1.0/admin/queue")
@Api(tags = "用户队列管理")
public class UserQueueController {
    private final UserService userService;
    private final QueueService queueService;
    private final CurrentUserService currentUserService;

    public UserQueueController(UserService userService,
                               QueueService queueService,
                               CurrentUserService currentUserService) {
        this.userService = userService;
        this.queueService = queueService;
        this.currentUserService = currentUserService;
    }

    /**
     * 批量插入队列信息
     *
     * @param request
     * @return
     */
    @AuthorizedOperation(name = "ADD-USER-QUEUE", key = "ADD-USER-QUEUE", description = "该权限将会检测当前用户是否为管理员")
    @ApiOperation(value = "批量插入队列信息")
    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    public int insertUserQueues(@Validated @RequestBody UserQueueRequestParam request) {
        int result = 0;
        if (request == null || CollectionUtils.isEmpty(request.getUserQueueDataList())) {
            return result;
        }
        UserInfoDTO userInfoDTO = userService.userInfo(request.getUserId());
        if (userInfoDTO != null) {
            List<UserQueueDTO> userQueueDTOS = new ArrayList<>();
            for (UserQueueRequestParam.UserQueueData userQueueData : request.getUserQueueDataList()) {
                UserQueueDTO userQueueDTO = new UserQueueDTO();
                userQueueDTO.setQueueId(IdGenerator.generateId().longValue());
                userQueueDTO.setQueue(userQueueData.getQueue());
                userQueueDTO.setUserId(new UserId(request.getUserId()));
                userQueueDTO.setBusinessType(userQueueData.getBusinessType());
                userQueueDTO.setRouting(userQueueData.getRouting());
                userQueueDTO.setExchange(userQueueData.getExchange());
                userQueueDTO.setPriority(userQueueData.getPriority());
                userQueueDTOS.add(userQueueDTO);
            }
            result = queueService.insertUserQueues(userQueueDTOS);
        }
        return result;
    }

    /**
     * 批量删除队列信息
     *
     * @param queueIds
     * @return
     */
    @AuthorizedOperation(name = "DELETE-USER-QUEUE", key = "DELETE-USER-QUEUE", description = "该权限将会检测当前用户是否为管理员")
    @ApiOperation(value = "批量删除队列信息")
    @RequestMapping(value = "/batch", method = RequestMethod.DELETE)
    public int deleteUserQueues(@RequestBody @NotEmpty(message = "队列ID不能为空!") List<Long> queueIds) {
        int result = 0;
        if (CollectionUtils.isEmpty(queueIds)) {
            return result;
        }
        result = queueService.deleteUserQueue(queueIds);
        return result;
    }

    /**
     * 批量禁用队列信息
     *
     * @param queueIds
     * @return
     */
    @AuthorizedOperation(name = "DISABLE-USER-QUEUE", key = "DISABLE-USER-QUEUE", description = "该权限将会检测当前用户是否为管理员")
    @ApiOperation(value = "批量禁用队列信息")
    @RequestMapping(value = "/batch/disable", method = RequestMethod.PUT)
    public int disableUserQueues(@RequestBody @NotEmpty(message = "队列ID不能为空!") List<Long> queueIds) {
        int result = 0;
        if (CollectionUtils.isEmpty(queueIds)) {
            return result;
        }
        result = queueService.disableUserQueue(queueIds);
        return result;
    }

    /**
     * 批量启用队列信息
     *
     * @param queueIds
     * @return
     */
    @AuthorizedOperation(name = "ENABLED-USER-QUEUE", key = "ENABLED-USER-QUEUE", description = "该权限将会检测当前用户是否为管理员")
    @ApiOperation(value = "批量启用队列信息")
    @RequestMapping(value = "/batch/enable", method = RequestMethod.PUT)
    public int EnableUserQueues(@RequestBody @NotEmpty(message = "队列ID不能为空!") List<Long> queueIds) {
        int result = 0;
        if (CollectionUtils.isEmpty(queueIds)) {
            return result;
        }
        result = queueService.enableUserQueue(queueIds);
        return result;
    }

    /**
     * 分页获取当前用户的队列配置
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation("分页获取当前用户的队列配置")
    @GetMapping("/current-user-queues")
    public PageResult<UserQueueDTO> queues(int pageNo, int pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return queueService.getCurrentUserQueues(pageParam, currentUserService.getCurrentUserId());
    }
}
