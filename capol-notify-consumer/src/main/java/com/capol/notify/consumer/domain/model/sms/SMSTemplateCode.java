package com.capol.notify.consumer.domain.model.sms;


/**
 * 短信模板枚举
 */
public enum SMSTemplateCode {

    /**
     * 短信验证码
     */
    VERIFICATIONCODE("SMS_221635219"),

    /**
     * 入驻审批通知
     */
    APPROVALNOTICE("SMS_221485463"),

    /**
     * 企业添加账号通知
     */
    ADDACCOUNTNOTICE("SMS_221480540"),

    /**
     * 逾期前通知（单个） 一个手机号，一个业务类型，只有一个的时候，发送的模版编码
     */
    OVERDUE_BEFORE_NOTICE_SINGLE("SMS_221480538"),

    /**
     * 逾期前通知（多个） 一个手机号，一个业务类型，有多个的时候，发送的模版编码
     */
    OVERDUE_BEFORE_NOTICE_MULTI("SMS_221480534"),

    /**
     * 已逾期通知（单个） 一个手机号，一个业务类型，只有一个的时候，发送的模版编码
     */
    OVERDUE_AFTER_NOTICE_SINGLE("SMS_221635219"),

    /**
     * 已逾期通知（多个） 一个手机号，一个业务类型，有多个的时候，发送的模版编码
     */
    OVERDUE_AFTER_NOTICE_MULTI("SMS_221635217"),

    /**
     * 会议召开通知
     */
    MEETING_HOST_NOTICE("SMS_221640208"),

    /**
     * 会议取消通知
     */
    MEETING_CANCLE_NOTICE("SMS_221635178"),

    /**
     * 会议时间变更通知
     */
    MEETING_TIME_NOTICE("SMS_221640170"),

    /**
     * 会议地点变更通知
     */
    MEETING_ADDRESS_NOTICE("SMS_221635139"),

    /**
     * 会议人员移除通知
     */
    MEETING_REMOVE_NOTICE("SMS_221640193"),

    /**
     * 会议15分钟召开提醒
     */
    MEETING_REMIND_NOTICE("SMS_221640189"),

    /**
     * 会议名称变更提醒
     */
    MEETING_UPDATENAME_NOTICE("SMS_221485431"),

    /**
     * 加入企业通知
     */
    JOIN_ENTERPRISE_NOTICE("SMS_221635210"),

    /**
     * 审核通过短信code
     */
    ENTERPRISE_APPROVAL_PASS("SMS_221635181"),

    /**
     * 审核驳回短信code
     */
    ENTERPRISE_APPROVAL_REJECT("SMS_221635183"),

    /**
     * 审核撤回短信code
     */
    ENTERPRISE_APPROVAL_WITHDRAW("SMS_221635310"),

    /**
     * 企业员工策划-有账号 SMS_205881699 (旧短信码)
     */
    ENTERPRISE_STAFF_ACCOUNT("SMS_221640184"),

    /**
     * 企业员工策划-无账号 SMS_205891757 (旧短信码)
     */
    ENTERPRISE_STAFF_NOT_ACCOUNT("SMS_221485416"),

    /**
     * 短信验证码
     */
    MESSAGE_AUTHENTIC_CODE("SMS_221485439"),

    /**
     * 二维码申请加入时邀请人通知码 SMS_211975798(旧短信码)
     */
    SHARER_NOTIFY_CODE("SMS_221485417"),

    /**
     * 子公司入驻提交资料通知分享人、修改资料通知母公司企业管理员
     */
    SON_ENTERPRISE_SHARER_NOTIFY_CODE("SMS_221635149"),

    /**
     * 子公司入驻修改资料通知母公司企业管理员
     */
    SON_ENTERPRISE_EDIT_SHARER_NOTIFY_CODE("SMS_221485404"),

    /**
     * 短信快捷登录----用户手机号注册
     */
    SHORT_MESSAGE_REGISTER_CODE("SMS_221480437"),

    /**
     * 紧急事件通知
     */
    URGENCY_ADVANCE_EVENT_NOTIFICATION("SMS_221480527"),

    /**
     * 事件开始之前通知
     */
    START_ADVANCE_EVENT_NOTIFICATION("SMS_221480525"),

    /**
     * 事件结束前通知
     */
    END_ADVANCE_EVENT_NOTIFICATION("SMS_221480522"),


    TEMPLATE_CODE("SMS_221480522"),

    TEMPLATE_CODE_NOTICE("SMS_221480522"),

    /**
     * 账号密码短信
     */
    ACCOUNT_AND_PASSWORD("SMS_221480540"),

    /**
     * 下载监控管理--管理员
     */
    DOWNLOAD_MONITOR_MANAGEMENT_ADMIN("SMS_221635147"),

    /**
     * 下载监控管理--管理员
     */
    DOWNLOAD_MONITOR_MANAGEMENT_USER("SMS_221480458"),

    /**
     * 新增企业入驻提醒审核--发送短信给系统后台管理员
     */
    ADD_ENTERPRISE_AUDIT_SUPER_ADMIN("SMS_223560433"),

    /**
     * 编辑企业资料提醒审核--发送短信给系统后台管理员
     */
    UPDATE_ENTERPRISE_AUDIT_SUPER_ADMIN("SMS_223560434"),

    /**
     * 成果交付通过--发送短信给接收人
     */
    ACHIEVEMENT_DELIVER_NOTICE("SMS_223587414"),

    /**
     * 审图流程发送报告完成--发送短信给接收人
     */
    DRAWING_PROCESS_NOTICE("SMS_224890084"),

    /**
     * 进度管控表通知
     */
    PROGRESS_CONTROL_NOTIFY("SMS_227262594"),

    /**
     * 进度管控表里程碑通知
     */
    PROGRESS_CONTROL_MILEPOST_NOTIFY("SMS_227732570"),

    /**
     * 进度管控表事件提前通知
     */
    PROGRESS_CONTROL_EVENT_START_NOTIFY("SMS_267975265"),

    /**
     * 进度管控表事件通知
     */
    PROGRESS_CONTROL_EVENT_NOTIFY("SMS_227747307"),

    /**
     * 设计变更通知单 -发送短信给接收人
     */
    DESIGN_CHANGE_NOTICE("SMS_227740468"),
    ;

    public String value;

    SMSTemplateCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}