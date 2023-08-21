package com.capol.notify.manage.domain;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.LogicDeleteBatchByIds;

import java.util.List;

/**
 * Mybatis sql注入器扩展
 *
 * @author CAPOL
 */
public class MybatisPlusSqlInjector extends DefaultSqlInjector {
    private final String LOGIC_DELETE_BATCH_IDS = "logicDeleteBatchIds";

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        // 拿到父类的getMethodList方法
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        // 增加批量逻辑删除方法
        methodList.add(new LogicDeleteBatchByIds(LOGIC_DELETE_BATCH_IDS));
        return methodList;
    }
}