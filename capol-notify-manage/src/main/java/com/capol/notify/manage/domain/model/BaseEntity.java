package com.capol.notify.manage.domain.model;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.capol.notify.manage.domain.EnumStatusType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BaseEntity extends IdentifiedDomainObject {
    /**
     * 记录状态(0-删除 1-正常)
     */
    private Integer status;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("created_datetime")
    private Date createdDateTime;

    /**
     * 最后一次修改时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("latest_modified_datetime")
    private Date latestModifiedDateTime;

    protected final void preRemove() {
        this.onRemove();
    }

    /**
     * 实现类在需要的时候覆盖该方法
     */
    protected void onRemove() {
        // ignore
    }

    public void buildBaseInfo() {
        if (this.getId() == null || this.getId() == 0L) {
            this.setId(IdGenerator.generateId());
            this.setStatus(EnumStatusType.NORMAL.getCode());
            this.setCreatedDatetime(DateUtil.dateSecond());
            this.setLatestModifiedDatetime(DateUtil.dateSecond());
        } else {
            this.setLatestModifiedDatetime(DateUtil.dateSecond());
        }
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedDatetime() {
        return createdDateTime;
    }

    private void setCreatedDatetime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Date getLastModifiedDatetime() {
        return latestModifiedDateTime;
    }

    private void setLatestModifiedDatetime(Date latestModifiedDatetime) {
        this.latestModifiedDateTime = latestModifiedDatetime;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, CustomToStringStyle.MULTILINE_INSTANCE,
                false, false, true, BaseEntity.class);
    }
}