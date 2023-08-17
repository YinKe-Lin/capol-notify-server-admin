package com.capol.notify.consumer.domain.model.email;


import com.capol.notify.consumer.EmailConfig;
import com.capol.notify.sdk.command.EmailContentCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Email消息发送领域服务
 */
@Slf4j
@Service
public class EmailSendService {
    private EmailConfig emailConfig;

    public EmailSendService(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    /**
     * 发送邮件
     *
     * @param emailContentCommand
     * @return
     * @throws Exception
     */
    public boolean sendEmail(EmailContentCommand emailContentCommand) throws Exception {
        Properties prop = new Properties();
        prop.setProperty("mail.smtp.host", emailConfig.getSmtp().getHost());
        prop.setProperty("mail.smtp.auth", emailConfig.getSmtp().getAuth());
        prop.setProperty("mail.smtp.port", "25");
        prop.setProperty("mail.transport.protocol", emailConfig.getTransport().getProtocol());
        prop.setProperty("mail.username", emailConfig.getUsername());
        prop.setProperty("mail.password", emailConfig.getPassword());
        if (StringUtils.isNotBlank(emailContentCommand.getSenderNick())) {
            prop.setProperty("mail.senderNick", emailContentCommand.getSenderNick());
        }

        EmailManager emailManager = new EmailManager(prop);

        if (StringUtils.isNotBlank(emailContentCommand.getSender())) {
            prop.setProperty("mail.username", emailContentCommand.getUsername());
            prop.setProperty("mail.password", emailContentCommand.getPassword());
            return emailManager.sendMail(emailContentCommand.getSender(), emailContentCommand.getTo(), emailContentCommand.getCc(), emailContentCommand.getSubject(), emailContentCommand.getContent());
        } else {
            return emailManager.sendMail(emailConfig.getSender(), emailContentCommand.getTo(), emailContentCommand.getCc(), emailContentCommand.getSubject(), emailContentCommand.getContent());
        }
    }
}