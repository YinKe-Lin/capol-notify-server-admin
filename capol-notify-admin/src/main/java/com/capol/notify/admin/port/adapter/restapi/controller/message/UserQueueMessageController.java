package com.capol.notify.admin.port.adapter.restapi.controller.message;

import com.capol.notify.manage.application.message.MessageService;
import com.capol.notify.manage.application.message.querystack.UserQueueMessageDTO;
import com.capol.notify.manage.domain.PageParam;
import com.capol.notify.manage.domain.PageResult;
import com.capol.notify.manage.domain.model.permission.CurrentUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
