package com.capol.notify.sdk.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSProcessNotifyMsgCommand extends SMSMsgCommand {
    private String contentProjectName;
    private String contentEventName;
    private Integer contentDayNum;
}
