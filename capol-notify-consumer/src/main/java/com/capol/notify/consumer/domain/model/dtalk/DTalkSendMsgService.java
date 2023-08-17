package com.capol.notify.consumer.domain.model.dtalk;

import cn.hutool.core.util.ObjectUtil;
import com.capol.notify.consumer.DTalkConfig;
import com.capol.notify.manage.domain.DomainException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.sdk.EnumMessageContentType;
import com.capol.notify.sdk.command.ActionCardCommand;
import com.capol.notify.sdk.command.DingDingGroupMsgCommand;
import com.capol.notify.sdk.command.DingDingNormalMsgCommand;
import com.capol.notify.sdk.command.OAContentCommand;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiChatSendRequest;
import com.dingtalk.api.request.OapiMediaUploadRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiChatSendResponse;
import com.dingtalk.api.response.OapiMediaUploadResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.taobao.api.FileItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 钉钉消息发送领域服务
 */
@Slf4j
@Service
public class DTalkSendMsgService {
    private DTalkConfig dTalkConfig;
    private DTalkTokenService dTalkTokenService;

    public DTalkSendMsgService(DTalkConfig dTalkConfig, DTalkTokenService dTalkTokenService) {
        this.dTalkConfig = dTalkConfig;
        this.dTalkTokenService = dTalkTokenService;
    }

    /**
     * 发送钉钉普通消息
     *
     * @param normalMsgCommand
     * @return
     * @throws Exception
     */
    public OapiMessageCorpconversationAsyncsendV2Response sendNormalMsg(DingDingNormalMsgCommand normalMsgCommand) throws Exception {
        DingTalkClient client = new DefaultDingTalkClient(dTalkConfig.getSendPersonTextUrl());
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();

        if (!CollectionUtils.isEmpty(normalMsgCommand.getUserIds())) {
            String userIdStr = normalMsgCommand.getUserIds().stream().collect(Collectors.joining(","));
            request.setUseridList(userIdStr);
        }

        request.setAgentId(normalMsgCommand.getAgentId());
        request.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();

        //针对Text类消息，如果content的长度大于500，会转为文件（File）发送
        if (normalMsgCommand.getContentType().getType() == EnumMessageContentType.TEXT.getType() && normalMsgCommand.getContent().length() >= 500) {
            normalMsgCommand.setContentType(EnumMessageContentType.FILE);
        }

        //判断消息类型
        if (normalMsgCommand.getContentType().getType() == EnumMessageContentType.TEXT.getType()) {
            msg.setMsgtype(EnumMessageContentType.TEXT.getTypeName());
            msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
            msg.getText().setContent(normalMsgCommand.getContent());
        }

        if (normalMsgCommand.getContentType().getType() == EnumMessageContentType.MARKDOWN.getType()) {
            SimpleDateFormat sdft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String currTime = sdft.format(new Date());
            String content = currTime + "  \n"
                    + normalMsgCommand.getContent();
            msg.setMsgtype(EnumMessageContentType.MARKDOWN.getTypeName());
            msg.setMarkdown(new OapiMessageCorpconversationAsyncsendV2Request.Markdown());
            msg.getMarkdown().setTitle("消息提醒：");
            msg.getMarkdown().setText(content);
        }

        FileMediaVO fileMediaVO = null;
        if (normalMsgCommand.getContentType().getType() == EnumMessageContentType.FILE.getType()) {
            fileMediaVO = uploadMediaFile(normalMsgCommand.getContent());
            msg.setMsgtype(EnumMessageContentType.FILE.getTypeName());
            msg.setFile(new OapiMessageCorpconversationAsyncsendV2Request.File());
            msg.getFile().setMediaId(fileMediaVO.getMediaId());
        }

        if (normalMsgCommand.getContentType().getType() == EnumMessageContentType.OA.getType()) {
            msg.setMsgtype(EnumMessageContentType.OA.getTypeName());
            OAContentCommand oaContent = normalMsgCommand.getOaContent();
            OapiMessageCorpconversationAsyncsendV2Request.OA oa = new OapiMessageCorpconversationAsyncsendV2Request.OA();

            oa.setMessageUrl(oaContent.getMessageUrl());
            oa.setPcMessageUrl(oaContent.getPcMessageUrl());

            OapiMessageCorpconversationAsyncsendV2Request.Head head = new OapiMessageCorpconversationAsyncsendV2Request.Head();
            head.setBgcolor("FFBBBBBB");
            head.setText(oaContent.getHeadText());
            oa.setHead(head);

            OapiMessageCorpconversationAsyncsendV2Request.Body body = new OapiMessageCorpconversationAsyncsendV2Request.Body();
            ArrayList<OapiMessageCorpconversationAsyncsendV2Request.Form> forms = new ArrayList<>();

            List<Map<String, String>> content = oaContent.getContent();
            for (Map<String, String> map : content) {
                OapiMessageCorpconversationAsyncsendV2Request.Form form = new OapiMessageCorpconversationAsyncsendV2Request.Form();
                String key = (String) map.keySet().toArray()[0];
                form.setKey(key);
                form.setValue(map.get(key));
                forms.add(form);
            }

            body.setContent(oaContent.getBodyContent());
            body.setTitle(oaContent.getBodyTitle());
            body.setForm(forms);
            oa.setBody(body);
            msg.setOa(oa);
        }
        if (normalMsgCommand.getContentType().getType() == EnumMessageContentType.ACTION_CARD.getType()) {
            msg.setMsgtype(EnumMessageContentType.ACTION_CARD.getTypeName());
            OapiMessageCorpconversationAsyncsendV2Request.ActionCard actionCard = new OapiMessageCorpconversationAsyncsendV2Request.ActionCard();
            ActionCardCommand actionCardCommand = normalMsgCommand.getActionCard();
            actionCard.setMarkdown(actionCardCommand.getMarkdown());
            actionCard.setTitle(actionCardCommand.getTitle());
            //actionCard消息必须同时设置single_url和single_title
            if (StringUtils.isNotEmpty(actionCardCommand.getSingleTitle()) && StringUtils.isNotEmpty(actionCardCommand.getSingleUrl())) {
                actionCard.setSingleTitle(actionCardCommand.getSingleTitle());
                actionCard.setSingleUrl(actionCardCommand.getSingleUrl());
                List<OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList> btnJsonLists = new ArrayList<>();
                OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList btnJsonOne = new OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList();
                btnJsonOne.setTitle(actionCardCommand.getSingleTitle());
                btnJsonOne.setActionUrl(actionCardCommand.getSingleUrl());
                btnJsonLists.add(btnJsonOne);
                actionCard.setBtnJsonList(btnJsonLists);
            }
            if (StringUtils.isNotEmpty(actionCardCommand.getBtnOrientation())) {
                /**
                 * 当 btnOrientation 的值为 "1" 时，按钮最多只能有两个；
                 * 当 btnOrientation 的值为 "0" 时，按钮最多可以有四个。
                 * 通常情况下，ActionCard 对象的 btnOrientation 属性值为 "0"，即按钮竖直排列。
                 */
                actionCard.setBtnOrientation(actionCardCommand.getBtnOrientation());
            } else {
                actionCard.setBtnOrientation("0");
            }
            msg.setActionCard(actionCard);
        }

        if (StringUtils.isEmpty(msg.getMsgtype())) {
            throw new DomainException("钉钉消息发送失败,MsgType不能为空!", EnumExceptionCode.InternalServerError);
        }
        request.setMsg(msg);
        String token = getToken();

        OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, token);
        commonOperate(response.isSuccess(), response.getSubCode(), fileMediaVO, normalMsgCommand.getContentType().getType());
        return response;
    }

    /**
     * 发送钉钉群组消息
     *
     * @param groupMsgCommand
     * @return
     * @throws Exception
     */
    public OapiChatSendResponse sendGroupMsg(DingDingGroupMsgCommand groupMsgCommand) throws Exception {
        DingTalkClient client = new DefaultDingTalkClient(dTalkConfig.getSendGroupTextUrl());
        OapiChatSendRequest request = new OapiChatSendRequest();
        request.setChatid(groupMsgCommand.getChatId());

        OapiChatSendRequest.Msg msg = new OapiChatSendRequest.Msg();

        //针对Text类消息，如果content的长度大于500，会转为文件（File）发送
        if (groupMsgCommand.getContentType().getType() == EnumMessageContentType.TEXT.getType() && groupMsgCommand.getContent().length() >= 500) {
            groupMsgCommand.setContentType(EnumMessageContentType.FILE);
        }

        //判断消息类型
        if (groupMsgCommand.getContentType().getType() == EnumMessageContentType.TEXT.getType()) {
            msg.setMsgtype(EnumMessageContentType.TEXT.getTypeName());
            OapiChatSendRequest.Text text = new OapiChatSendRequest.Text();
            text.setContent(groupMsgCommand.getContent());
            msg.setText(text);
        }

        FileMediaVO fileMediaVO = null;
        if (groupMsgCommand.getContentType().getType() == EnumMessageContentType.FILE.getType()) {
            fileMediaVO = uploadMediaFile(groupMsgCommand.getContent());
            OapiChatSendRequest.File file = new OapiChatSendRequest.File();
            msg.setMsgtype(EnumMessageContentType.FILE.getTypeName());
            msg.setFile(file);
            msg.getFile().setMediaId(fileMediaVO.getMediaId());
        }

        request.setMsg(msg);
        String token = getToken();
        OapiChatSendResponse response = client.execute(request, token);

        commonOperate(response.isSuccess(), response.getSubCode(), fileMediaVO, groupMsgCommand.getContentType().getType());
        return response;
    }

    /**
     * 上传文件
     *
     * @param content
     * @return
     * @throws Exception
     */
    public FileMediaVO uploadMediaFile(String content) throws Exception {
        DingTalkClient client = new DefaultDingTalkClient(dTalkConfig.getMediaUploadUrl());
        OapiMediaUploadRequest request = new OapiMediaUploadRequest();
        request.setType(EnumMessageContentType.FILE.getTypeName());
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String time = formatter.format(now);
        String filePath = getDingTalkFilePath() + "-dtalk-" + time + ".txt";
        request.setMedia(new FileItem(string2File(content, filePath)));
        OapiMediaUploadResponse response = client.execute(request, dTalkTokenService.getDingToken());
        return new FileMediaVO(response.getMediaId(), filePath);
    }

    private String getToken() throws Exception {
        String token = dTalkTokenService.getRedisDingToken();
        if (StringUtils.isEmpty(token)) {
            token = dTalkTokenService.getDingToken();
        }
        return token;
    }

    private void clearToken(String errorCode) {
        if (ObjectUtil.equals(errorCode, "33001") || ObjectUtil.equals(errorCode, "40001") || ObjectUtil.equals(errorCode, "40014")
                || ObjectUtil.equals(errorCode, "42001")) {

            dTalkTokenService.clearDingToken();
        }
    }

    private File string2File(String res, String filePath) {
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            File distFile = new File(filePath);
            if (!distFile.getParentFile().exists()) {
                distFile.getParentFile().mkdirs();
            }
            bufferedReader = new BufferedReader(new StringReader(res));
            bufferedWriter = new BufferedWriter(new FileWriter(distFile));
            //字符缓冲区
            char buf[] = new char[1024];
            int len;
            while ((len = bufferedReader.read(buf)) != -1) {
                bufferedWriter.write(buf, 0, len);
            }
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
            return distFile;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String getDingTalkFilePath() {
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
        }

        if (path == null || !path.exists()) {
            path = new File("");
        }

        String pathStr = path.getAbsolutePath();
        pathStr = pathStr.replace("\\target\\classes", "\\src\\main\\resources\\dingTalkFile");

        return pathStr;
    }

    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    private void commonOperate(boolean success, String errorCode, FileMediaVO fileMediaVO, String msgType) {
        if (!success) {
            //如果是Token有问题，需要清除缓存中的Token
            clearToken(errorCode);
        }

        if (msgType == EnumMessageContentType.FILE.getType() && fileMediaVO != null) {
            //如果是文件，需要删除已经上传的文件
            deleteFile(fileMediaVO.getFilePath());
        }
    }
}
