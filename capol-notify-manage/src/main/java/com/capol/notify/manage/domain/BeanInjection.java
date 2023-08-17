package com.capol.notify.manage.domain;

import com.capol.notify.manage.domain.model.queue.UserQueueEventCenter;
import com.capol.notify.manage.domain.model.queue.UserQueueListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 注入各接口的实现到容器
 */
@Slf4j
@Component
public class BeanInjection {
    /**
     * 注入队列事件侦听器
     *
     * @param listeners
     */
    @Autowired(required = false)
    public void setUserQueueListener(List<UserQueueListener> listeners) {
        UserQueueEventCenter.registerListeners(listeners);
        listeners.forEach(o -> {
            log.info("-->注入监听器:{}", o.getClass());
        });
        UserQueueEventCenter.getListeners().forEach(o -> {
            log.info("-->已注册的监听器:{}", o.getClass());
        });
    }
}
