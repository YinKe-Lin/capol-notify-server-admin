package com.capol.notify.manage.domain.model.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 队列侦听器默认实现, 在具体引用模块中可重新实现UserQueueListener并注入到事件管理中心
 *
 * @author heyong1
 * @since 2023-07-24 14:21:22
 */
@Slf4j
public class UserQueueListenerDefault implements UserQueueListener {
    /**
     * 队列注册（每次添加新的队列时触发）
     *
     * @param queueIds
     */
    @Override
    public void register(List<Long> queueIds) {
        log.info("-->队列:{} 注册（每次添加新的队列时触发）", queueIds);
    }

    /**
     * 队列删除（每次删除队列时触发）
     *
     * @param queueIds
     */
    @Override
    public void deleteByIds(List<Long> queueIds) {
        log.info("-->队列:{} 删除（每次删除队列时触发）", queueIds);
    }

    /**
     * 队列删除（每次删除队列时触发）
     *
     * @param queues
     */
    @Override
    public void deleteByQueues(List<UserQueueDO> queues) {
        log.info("-->队列:{} 删除（每次删除队列时触发）", queues);
    }

    /**
     * 更改队列（每次更改队列时触发）
     *
     * @param queueIds
     */
    @Override
    public void update(List<Long> queueIds) {
        log.info("-->更改队列:{}（每次更改队列时触发）", queueIds);
    }

    /**
     * 禁用队列（每次禁用队列时触发）
     *
     * @param queueIds
     */
    @Override
    public void disabled(List<Long> queueIds) {
        log.info("-->禁用队列:{}（每次禁用队列时触发）", queueIds);
    }

    /**
     * 启用队列（每次启用队列时触发）
     *
     * @param queueIds
     */
    @Override
    public void enabled(List<Long> queueIds) {
        log.info("-->启用队列:{}（每次启用队列时触发）", queueIds);
    }
}
