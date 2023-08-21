package com.capol.notify.manage.domain;


import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 实体工具类
 *
 * @author CAPOL
 */
public class EntityUtils {

    /**
     * 获取填充id属性的实体
     *
     * @param entityClass 实体类类型
     * @param id          实体id
     * @param <E>         实体类泛型
     * @return 填充id属性的实体
     */
    public static <E> E getEntityById(Class<E> entityClass, Serializable id) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        E entity = ReflectUtil.newInstance(entityClass);
        ReflectUtil.setFieldValue(entity, tableInfo.getKeyProperty(), id);
        return entity;
    }

    /**
     * 获取填充id属性的实体列表
     *
     * @param entityClass 实体类类型
     * @param ids         实体id列表
     * @param <E>         实体类泛型
     * @return 填充id属性的实体列表
     */
    public static <E> List<E> getEntityListByIds(Class<E> entityClass, List<? extends Serializable> ids) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        List<E> entityList = new ArrayList<>();
        for (Serializable id : ids) {
            E entity = ReflectUtil.newInstance(entityClass);
            ReflectUtil.setFieldValue(entity, tableInfo.getKeyProperty(), id);
            entityList.add(entity);
        }
        return entityList;
    }
}