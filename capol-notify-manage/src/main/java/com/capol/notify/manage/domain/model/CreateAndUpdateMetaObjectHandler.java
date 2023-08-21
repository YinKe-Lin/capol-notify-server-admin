package com.capol.notify.manage.domain.model;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.capol.notify.manage.domain.DomainException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * MP注入处理器
 *
 * @author heyong
 */
public class CreateAndUpdateMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) metaObject.getOriginalObject();
                Date current = ObjectUtil.isNotNull(baseEntity.getCreatedDatetime())
                        ? baseEntity.getCreatedDatetime() : Convert.toDate(DateUtil.now());
                baseEntity.setCreatedDatetime(current);
                baseEntity.setLatestModifiedDatetime(current);
            }
        } catch (Exception e) {
            throw new DomainException("MP注入处理器异常:" + e.getMessage(), EnumExceptionCode.InternalServerError);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) metaObject.getOriginalObject();
                // 更新时间填充(不管为不为空)
                baseEntity.setLatestModifiedDatetime(Convert.toDate(DateUtil.now()));
            }
        } catch (Exception e) {
            throw new DomainException("MP注入处理器异常:" + e.getMessage(), EnumExceptionCode.InternalServerError);
        }
    }
}
