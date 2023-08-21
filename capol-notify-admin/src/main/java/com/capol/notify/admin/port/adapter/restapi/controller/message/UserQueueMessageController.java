package com.capol.notify.admin.port.adapter.restapi.controller.message;

import com.capol.notify.admin.port.adapter.restapi.AuthorizedOperation;
import com.capol.notify.manage.application.message.MessageService;
import com.capol.notify.manage.application.message.querystack.UserQueueMessageDTO;
import com.capol.notify.manage.domain.PageParam;
import com.capol.notify.manage.domain.PageResult;
import com.capol.notify.manage.domain.model.permission.CurrentUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 用户队列消息管理
 *
 * @author heyong
 */
@RestController
@Validated
@RequestMapping("/api/v1.0/admin/message")
@Api(tags = "用户队列消息管理")
public class UserQueueMessageController {

    private final MessageService messageService;
    private final CurrentUserService currentUserService;

    public UserQueueMessageController(MessageService messageService, CurrentUserService currentUserService) {
        this.messageService = messageService;
        this.currentUserService = currentUserService;
    }

    /**
     * 分页获取当前用户的所有消息内容
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation("分页获取当前用户的所有消息内容")
    @GetMapping("/current-user-queue-messages/{pageNo}/{pageSize}")
    public PageResult<UserQueueMessageDTO> queueMessages(@PathVariable("pageNo") Integer pageNo,
                                                         @PathVariable("pageSize") Integer pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return messageService.getMessagesByPage(pageParam, currentUserService.getCurrentUserId());
    }

    /**
     * 获取指定消息内容详情
     *
     * @param messageId 消息ID
     * @return
     */
    @ApiOperation("获取指定消息内容详情")
    @GetMapping("/detail/{messageId}")
    public UserQueueMessageDTO queueMessages(@PathVariable("messageId") Long messageId) {
        return UserQueueMessageDTO.of(messageService.getMessageById(messageId));
    }

    /**
     * 批量删除消息
     *
     * @param messageIds 消息IDs
     * @return
     */
    @AuthorizedOperation(name = "BATCH-DELETE-MESSAGE", key = "BATCH-DELETE-MESSAGE", description = "该权限将会检测当前用户是否为管理员")
    @ApiOperation("批量删除消息")
    @DeleteMapping("/batch")
    public Integer queueMessages(@NotEmpty(message = "消息ID不能为空") @RequestBody List<Long> messageIds) {
        return messageService.deleteMessageByIds(messageIds);
    }
}
