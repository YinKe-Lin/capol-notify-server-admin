package com.capol.notify.consumer.domain.model.email;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class EmailManager {

    // 发件人昵称
    public static String senderNick = "system notice";
    private Properties props;
    private Session session;
    private MimeMessage mimeMsg;
    private Multipart mp;

    public EmailManager(Properties props) {

        this.props = props;
        // 建立会话
        session = Session.getDefaultInstance(props);
        session.setDebug(true);
    }

    /**
     * 发送邮件
     *
     * @param from    发件人
     * @param to      收件人, 多个Email以英文逗号分隔
     * @param cc      抄送, 多个Email以英文逗号分隔
     * @param subject 主题
     * @param content 内容
     * @return
     */
    public boolean sendMail(String from, String[] to, String[] cc, String subject, String content) {
        boolean success = true;
        try {
            mimeMsg = new MimeMessage(session);
            mp = new MimeMultipart();
            // 自定义发件人昵称
            String nick = (String) props.get("mail.senderNick");
            try {
                if (StringUtils.isBlank(nick)) {
                    nick = javax.mail.internet.MimeUtility.encodeText(senderNick);
                }

            } catch (UnsupportedEncodingException e) {
                log.warn("-->不支持的昵称格式 email nickname {}", e.getMessage());
            }
            // 设置发件人
            mimeMsg.setFrom(new InternetAddress(nick + " <" + from + ">"));
            // 设置收件人
            if (to != null && to.length > 0) {
                String toListStr = getMailStr(to);
                mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toListStr));
            }
            // 设置抄送人
            if (cc != null && cc.length > 0) {
                String ccListStr = getMailStr(cc);
                mimeMsg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccListStr));
            }
            // 设置主题
            mimeMsg.setSubject(subject, "UTF-8");
            // 设置正文
            BodyPart bp = new MimeBodyPart();
            bp.setContent(content, "text/html;charset=utf-8");
            mp.addBodyPart(bp);

            mimeMsg.setContent(mp);
            mimeMsg.saveChanges();

            // 发送邮件
            if (props.get("mail.smtp.auth").equals("true")) {
                Transport transport = session.getTransport("smtp");
                transport.connect((String) props.get("mail.smtp.host"), (String) props.get("mail.username"), (String) props.get("mail.password"));
                transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
                transport.close();
            } else {
                Transport.send(mimeMsg);
            }
            log.info("-->发送邮件到:{}成功!", JSON.toJSONString(to));
        } catch (MessagingException e) {
            success = false;
            log.error("-->发送邮件到:{} 失败, 失败原因:{}", JSON.toJSONString(to), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            success = false;
            log.error("-->发送邮件到:{} 失败, 失败原因:{}", JSON.toJSONString(to), e.getMessage());
            e.printStackTrace();
        }
        return success;
    }

    private String getMailStr(String[] mailArray) {
        if (mailArray != null && mailArray.length < 2) {
            return mailArray[0];
        }
        return Arrays.asList(mailArray).stream().collect(Collectors.joining(","));
    }
}