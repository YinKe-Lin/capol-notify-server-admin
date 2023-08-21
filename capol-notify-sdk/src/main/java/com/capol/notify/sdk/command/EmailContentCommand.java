package com.capol.notify.sdk.command;


import com.capol.notify.sdk.EnumMessageType;
import lombok.Data;

/**
 * Email消息传输对象
 */
@Data
public class EmailContentCommand extends BaseMsgCommand {
    private static final long serialVersionUID = -6643212454457854652L;

    /**
     * 发件人邮箱
     */
    private String sender;

    /**
     * 发件人名称
     */
    private String username;

    /**
     * 发件人昵称
     */
    private String senderNick;

    /**
     * 发件人邮箱密码
     */
    private String password;

    /**
     * 主题
     */
    private String subject;

    /**
     * 内容
     */
    private String content;

    /**
     * 收件人
     */
    private String[] to;

    /**
     * 抄送人
     */
    private String[] cc;

    public EmailContentCommand(String subject, String content, String[] to, String[] cc, Integer priority,
                               Integer ttl, EnumMessageType messageType, String businessType) {
        this.subject = subject;
        this.content = content;
        this.to = to;
        this.cc = cc;
        this.setPriority(priority);
        this.setTtl(ttl);
        this.setMessageType(messageType);
        this.setBusinessType(businessType);
    }

    public EmailContentCommand(String sender, String subject, String content, String[] to, String[] cc,
                               Integer ttl, Integer priority, EnumMessageType messageType, String businessType) {
        this.sender = sender;
        this.subject = subject;
        this.content = content;
        this.to = to;
        this.cc = cc;
        this.setPriority(priority);
        this.setTtl(ttl);
        this.setMessageType(messageType);
        this.setBusinessType(businessType);
    }

    public EmailContentCommand(String sender, String username, String senderNick, String password,
                               String subject, String content, String[] to, String[] cc, Integer priority,
                               Integer ttl, EnumMessageType messageType, String businessType) {
        this.sender = sender;
        this.username = username;
        this.senderNick = senderNick;
        this.password = password;
        this.subject = subject;
        this.content = content;
        this.to = to;
        this.cc = cc;
        this.setPriority(priority);
        this.setTtl(ttl);
        this.setMessageType(messageType);
        this.setBusinessType(businessType);
    }
}