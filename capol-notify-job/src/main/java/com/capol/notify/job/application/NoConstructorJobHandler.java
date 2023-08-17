package com.capol.notify.job.application;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 在xxl-job中，实现了IJobHandler接口的类可以作为JobHandler使用，而JobHandler的调用方式有两种：Bean模式和GLUE模式。
 * <p>
 * 在Bean模式中，JobHandler的名称是实现类的名称，如果实现类没有使用@XxlJob注解指定名称，则默认使用类的简单名称作为名称。
 * Bean模式下，xxl-job会将JobHandler实例化为Spring Bean，并通过Spring容器管理，使用时可以直接注入该Bean并调用其execute()方法。
 */
@Slf4j
@Component
public class NoConstructorJobHandler {

    /**
     * 初始化方法
     */
    public void init() {
        if (log.isDebugEnabled()) {
            log.debug("-->Job初始化方法:这是一个没有定义带参数构造器的JobHandler!!!!");
        }
    }

    /**
     * 销毁方法
     */
    public void destroy() {
        if (log.isDebugEnabled()) {
            log.debug("-->Job销毁方法:这是一个没有定义带参数构造器的JobHandler!!!!");
        }
    }

    /**
     * 简单任务示例（Bean模式）
     */
    @XxlJob(value = "demoJobHandler", init = "init", destroy = "destroy")
    public ReturnT<String> demoJobHandler() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("这是一个 XXL-JOB 简单任务示例（Bean模式）, Hello World.");
        }
        for (int i = 0; i < 5; i++) {
            log.info("-->beat at:" + i);
            TimeUnit.SECONDS.sleep(2);
        }

        return ReturnT.SUCCESS;
    }

    /**
     * 简单任务示例（Bean模式）
     */
    @XxlJob(value = "demoArgumentsJobHandler", init = "init", destroy = "destroy")
    public ReturnT<String> demoArgumentsJobHandler() throws Exception {
        String param = XxlJobHelper.getJobParam();
        String[] params = param.split(",");
        if (params.length != 2) {
            log.error("-->demoArgumentsJobHandler 参数不合法:{}, 正确配置参数方式:{}", param, "5,2");
            return ReturnT.FAIL;
        }
        log.info("-->参数Params:{}", JSON.toJSONString(params));
        log.info("这是一个 XXL-JOB 简单任务示例（Bean模式）, 带参数:startDay->{},endDays->{}.", params[0], params[1]);
        for (int i = 0; i < 5; i++) {
            log.info("-->beat at:" + i);
            TimeUnit.SECONDS.sleep(2);
        }

        return ReturnT.SUCCESS;
    }
}