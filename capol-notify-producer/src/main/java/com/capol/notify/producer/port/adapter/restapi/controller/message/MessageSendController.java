package com.capol.notify.producer.port.adapter.restapi.controller.message;

import com.capol.notify.manage.application.ApplicationException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.producer.application.message.SendMessageService;
import com.capol.notify.producer.port.adapter.restapi.controller.message.parameter.EmailMessageRequestParam;
import com.capol.notify.producer.port.adapter.restapi.controller.message.parameter.DTalkMessageRequestParam;
import com.capol.notify.producer.port.adapter.restapi.controller.message.parameter.WeChatMessageRequestParam;
import com.capol.notify.sdk.EnumMessageContentType;
import com.capol.notify.sdk.EnumMessageType;
import com.capol.notify.sdk.command.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1.0/service/message")
@Api(tags = "消息发送服务")
public class MessageSendController {

    private final SendMessageService sendMessageService;

    public MessageSendController(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @PostMapping("/send-wechat-request")
    @ApiOperation("微信消息发送请求")
    public void messageSendRequest(@RequestBody @Valid WeChatMessageRequestParam request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ApplicationException(bindingResult.getFieldError().getDefaultMessage(), EnumExceptionCode.BadRequest);
        }
        sendMessageService.sendWeiXinMsg(new WeChatMsgCommand(
                request.getStallId(),
                request.getTemplateId(),
                request.getData(),
                request.getMiniProgram(),
                request.getPriority(),
                EnumMessageType.WECHAT_MESSAGE,
                request.getBusinessType()));
    }

    @PostMapping("/send-email-request")
    @ApiOperation("邮件消息发送请求")
    public void messageSendRequest(@RequestBody @Valid EmailMessageRequestParam request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ApplicationException(bindingResult.getFieldError().getDefaultMessage(), EnumExceptionCode.BadRequest);
        }
        if (request.getTo() == null || request.getTo().length <= 0) {
            throw new ApplicationException(String.format("收件人列表 (to) 不允许为空!"), EnumExceptionCode.BadRequest);
        }
        sendMessageService.sendEmailMsg(new EmailContentCommand(
                request.getSubject(),
                request.getContent(),
                request.getTo(),
                request.getCc(),
                request.getPriority(),
                EnumMessageType.EMAIL_MESSAGE,
                request.getBusinessType()));
    }

    @PostMapping("/send-dtalk-request")
    @ApiOperation("钉钉消息发送请求")
    public void messageSendRequest(@RequestBody @Validated DTalkMessageRequestParam request) {
        switch (request.getMessageType()) {
            case DING_NORMAL_MESSAGE: {
                if (CollectionUtils.isEmpty(request.getUserIds())) {
                    throw new ApplicationException("参数不合法,钉钉用户会话ID (UserIds)不允许为空,长度最长100位用户!", EnumExceptionCode.BadRequest);
                }
                if (request.getAgentId() == null || request.getAgentId() == 0L) {
                    throw new ApplicationException("参数不合法,AgentID 不允许为空!", EnumExceptionCode.BadRequest);
                }

                if (EnumMessageContentType.OA.equals(request.getContentType())) {
                    OAContentCommand oaContentCommand = null;
                    if (request.getOaContent() != null
                            && StringUtils.isNotEmpty(request.getOaContent().getBodyTitle())
                            && StringUtils.isNotEmpty(request.getOaContent().getHeadText())
                            && CollectionUtils.isNotEmpty(request.getOaContent().getContent())) {
                        oaContentCommand = new OAContentCommand();
                        oaContentCommand.setContent(request.getOaContent().getContent());
                        oaContentCommand.setBodyContent(request.getOaContent().getBodyContent());
                        oaContentCommand.setForm(request.getOaContent().getForm());
                        oaContentCommand.setMessageUrl(request.getOaContent().getMessageUrl());
                        oaContentCommand.setPcMessageUrl(request.getOaContent().getPcMessageUrl());
                        oaContentCommand.setBodyTitle(request.getOaContent().getBodyTitle());
                        oaContentCommand.setHeadText(request.getOaContent().getHeadText());
                    } else {
                        throw new ApplicationException("参数不合法,消息内容类型为OA时, OAContent对象以及BodyTitle、HeadText、Content不允许为空!", EnumExceptionCode.BadRequest);
                    }
                    sendMessageService.sendDingDingNormalMsg(new DingDingNormalMsgCommand(
                            request.getUserIds(),
                            request.getAgentId(),
                            request.getContent(),
                            request.getPriority(),
                            request.getMessageType(),
                            request.getContentType(),
                            request.getBusinessType(),
                            oaContentCommand));
                }
                if (EnumMessageContentType.ACTION_CARD.equals(request.getContentType())) {
                    ActionCardCommand actionCardCommand = null;
                    if (request.getActionCard() != null
                            && StringUtils.isNotEmpty(request.getActionCard().getSingleTitle())
                            && StringUtils.isNotEmpty(request.getActionCard().getSingleUrl())) {
                        actionCardCommand = new ActionCardCommand();
                        actionCardCommand.setMarkdown(request.getActionCard().getMarkdown());
                        actionCardCommand.setTitle(request.getActionCard().getTitle());
                        actionCardCommand.setBtnOrientation(request.getActionCard().getBtnOrientation());
                        actionCardCommand.setSingleTitle(request.getActionCard().getSingleTitle());
                        actionCardCommand.setSingleUrl(request.getActionCard().getSingleUrl());
                    } else {
                        throw new ApplicationException("参数不合法,消息内容类型为ActionCard时, ActionCard对象以及SingleTitle和SingleUrl都不允许为空!", EnumExceptionCode.BadRequest);
                    }
                    sendMessageService.sendDingDingNormalMsg(new DingDingNormalMsgCommand(
                            request.getUserIds(),
                            request.getAgentId(),
                            request.getContent(),
                            request.getPriority(),
                            request.getMessageType(),
                            request.getContentType(),
                            request.getBusinessType(),
                            actionCardCommand));
                }
                break;
            }
            case DING_GROUP_MESSAGE: {
                if (StringUtils.isEmpty(request.getChatId())) {
                    throw new ApplicationException("参数不合法,钉钉群组会话ID (ChatID) 不允许为空!", EnumExceptionCode.BadRequest);
                }
                sendMessageService.sendDingDingGroupMsg(new DingDingGroupMsgCommand(
                        request.getChatId(),
                        request.getContent(),
                        request.getPriority(),
                        request.getMessageType(),
                        request.getContentType(),
                        request.getBusinessType()));
                break;
            }
        }
    }
}
