package com.capol.notify.manage.domain.model.queue;

import com.capol.notify.manage.domain.DomainException;
import com.capol.notify.manage.domain.EnumExceptionCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 队列事件中心 事件发布器
 * 提供侦听器注册、事件发布能力
 *
 * @author heyong1
 * @since 2023-07-24 14:21:22
 */
public class UserQueueEventCenter {
    /**
     * 侦听器
     */
    private static List<UserQueueListener> listeners = new ArrayList<>();

    /**
     * 添加队列侦听器默认实现
     */
    static {
        listeners.add(new UserQueueListenerDefault());
    }

    /**
     * 获取已经注册的所有侦听器
     *
     * @return
     */
    public static List<UserQueueListener> getListeners() {
        return listeners;
    }

    /**
     * 重置侦听器
     *
     * @param listeners
     */
    public static void setListeners(List<UserQueueListener> listeners) {
        if (listeners == null) {
            throw new DomainException("重置侦听器集合不能为空!", EnumExceptionCode.InternalServerError);
        }
        UserQueueEventCenter.listeners = listeners;
    }

    /**
     * 注册一个侦听器
     *
     * @param listener
     */
    public static void registerListener(UserQueueListener listener) {
        if (listener == null) {
            throw new DomainException("注册的侦听器集合不能为空!", EnumExceptionCode.InternalServerError);
        }

        listeners.add(listener);
    }

    /**
     * 注册一组侦听器
     *
     * @param listeners
     */
    public static void registerListeners(List<UserQueueListener> listeners) {
        if (listeners == null) {
            throw new DomainException("注册的侦听器集合不能为空!", EnumExceptionCode.InternalServerError);
        }
        for (UserQueueListener listener : listeners) {
            if (listener == null) {
                throw new DomainException("注册的侦听器集合不能为空!", EnumExceptionCode.InternalServerError);
            }
        }
        UserQueueEventCenter.listeners.addAll(listeners);
    }

    /**
     * 移除一个侦听器
     *
     * @param listener
     */
    public static void removeListener(UserQueueListener listener) {
        listeners.remove(listener);
    }

    /**
     * 移除指定类型的所有侦听器
     *
     * @param cls
     */
    public static void removeListener(Class<? extends UserQueueListener> cls) {
        ArrayList<UserQueueListener> listenersCopy = new ArrayList<>(listeners);
        for (UserQueueListener listener : listenersCopy) {
            if (cls.isAssignableFrom(listener.getClass())) {
                listeners.remove(listener);
            }
        }
    }

    /**
     * 清空所有已经注册的侦听器
     */
    public static void clearListeners() {
        listeners.clear();
    }

    /**
     * 判断是否已经注册了指定的侦听器
     *
     * @param listener
     * @return
     */
    public static boolean hasListener(UserQueueListener listener) {
        return listeners.contains(listener);
    }

    /**
     * 判断是否已经注册了指定类型的侦听器
     *
     * @param cls
     * @return
     */
    public static boolean hasListener(Class<? extends UserQueueListener> cls) {
        for (UserQueueListener listener : listeners) {
            if (cls.isAssignableFrom(listener.getClass())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 注册队列事件发布
     *
     * @param queueIds 队列ID
     */
    public static void doRegister(List<Long> queueIds){
        for (UserQueueListener listener : listeners) {
            listener.register(queueIds);
        }
    }

    /**
     * 更改队列事件发布
     *
     * @param queueIds
     */
    public static void doUpdate(List<Long> queueIds) {
        for (UserQueueListener listener : listeners) {
            listener.update(queueIds);
        }
    }

    /**
     * 删除队列事件发布
     *
     * @param queueIds
     */
    public static void doDeleteByIds(List<Long> queueIds) {
        for (UserQueueListener listener : listeners) {
            listener.deleteByIds(queueIds);
        }
    }

    /**
     * 删除队列事件发布
     *
     * @param queues
     */
    public static void doDeleteByQueues(List<UserQueueDO> queues) {
        for (UserQueueListener listener : listeners) {
            listener.deleteByQueues(queues);
        }
    }

    /**
     * 禁用队列事件发布
     *
     * @param queueIds
     */
    public static void doDisabled(List<Long> queueIds) {
        for (UserQueueListener listener : listeners) {
            listener.disabled(queueIds);
        }
    }

    /**
     * 启用队列事件发布
     *
     * @param queueIds
     */
    public static void doEnabled(List<Long> queueIds) {
        for (UserQueueListener listener : listeners) {
            listener.enabled(queueIds);
        }
    }
}
