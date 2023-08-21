package com.capol.notify.manage.domain;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.capol.notify.manage.domain.model.CreateAndUpdateMetaObjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis-plus配置类
 *
 * @author heyong
 */
@Configuration
public class MybatisPlusConfig {
    /**
     * 新增分页拦截器，并设置数据库类型为mysql
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(paginationInnerInterceptor());
        return interceptor;
    }

    /**
     * 元对象字段填充控制器
     * 为系统字段填充值[创建人、创建时间、更新人、更新时间]
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new CreateAndUpdateMetaObjectHandler();
    }

    /**
     * sql注入器扩展
     */
    @Bean
    public ISqlInjector iSqlInjector() {
        return new MybatisPlusSqlInjector();
    }

    /**
     * 分页插件，自动识别数据库类型
     */
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInnerInterceptor.setMaxLimit(500L);
        // 分页合理化(如果真实数据页数,超出了查询请求中的分页的页数,是否要自动处理溢出,为true表示自动处理且返回处理后的页数据,为false表示不处理直接返回空数据页)
        paginationInnerInterceptor.setOverflow(false);
        return paginationInnerInterceptor;
    }
}
