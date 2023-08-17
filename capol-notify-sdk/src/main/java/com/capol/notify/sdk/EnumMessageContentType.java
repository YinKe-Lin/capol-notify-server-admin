package com.capol.notify.sdk;

/**
 * 消息内容类型
 */
public enum EnumMessageContentType {
    TEXT("TEXT", "text"),
    IMAGE("IMAGE", "image"),
    FILE("FILE", "file"),
    LINK("LINK", "link"),
    MARKDOWN("MARKDOWN", "markdown"),
    OA("OA", "oa"),
    ACTION_CARD("ACTION_CARD", "action_card");


    private String type;
    private String typeName;

    EnumMessageContentType(String type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
