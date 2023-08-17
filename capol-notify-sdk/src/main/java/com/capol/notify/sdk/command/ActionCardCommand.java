package com.capol.notify.sdk.command;


import lombok.Data;

import java.io.Serializable;

@Data
public class ActionCardCommand implements Serializable {

    private static final long serialVersionUID = -8445943548965154999L;
    /**
     * btnOrientation 表示按钮排列的方向，可选值为 "0" 和 "1"，含义如下：
     *
     * "0"：按钮竖直排列；
     * "1"：按钮横向排列。
     */
    private String btnOrientation;
    /**
     * 卡片 markdown 格式的内容
     */
    private String markdown;
    /**
     * 设置单个按钮的标题
     */
    private String singleTitle;
    /**
     *  设置单个按钮的链接
     */
    private String singleUrl;
    /**
     * 卡片标题
     */
    private String title;
}

