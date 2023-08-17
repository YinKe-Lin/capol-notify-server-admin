package com.capol.notify.manage.domain.model.queue;

import java.util.List;

/**
 * 队列侦听器
 * 通过实现此接口在管理界面维护队列时（删除、修改、添加）等操作时进行MQ服务器队列的注册和删除操作
 *
 * @author heyong1
 * @since 2023-07-24 14:21:22
 */
public interface UserQueueListener {
    /**
     * 队列注册（每次添加新的队列时触发）
     *
     * @param queueIds
     */
    void register(List<Long> queueIds);

    /**
     * 队列删除（每次删除队列时触发）
     *
     * @param queueIds
     */
    void delete(List<Long> queueIds);

    /**
     * 更改队列（每次更改队列时触发）
     *
     * @param queueIds
     */
    void update(List<Long> queueIds);

    /**
     * 禁用队列（每次禁用队列时触发）
     *
     * @param queueIds
     */
    void disabled(List<Long> queueIds);

    /**
     * 启用队列（每次启用队列时触发）
     *
     * @param queueIds
     */
    void enabled(List<Long> queueIds);
}
