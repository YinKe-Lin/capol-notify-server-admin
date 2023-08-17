package com.capol.notify.admin.port.adapter.restapi.controller.message;

import com.capol.notify.manage.application.message.MessageService;
import com.capol.notify.manage.application.message.querystack.UserQueueMessageDTO;
import com.capol.notify.manage.domain.PageParam;
import com.capol.notify.manage.domain.PageResult;
import com.capol.notify.manage.domain.model.permission.CurrentUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
    @GetMapping("/current-user-queue-messages")
    public PageResult<UserQueueMessageDTO> queueMessages(int pageNo, int pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return messageService.getMessagesByPage(pageParam, currentUserService.getCurrentUserId());
    }
}
