package com.capol.notify.admin.domain.model.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.capol.notify.manage.application.queue.QueueMQService;
import com.capol.notify.manage.domain.EnumStatusType;
import com.capol.notify.manage.domain.model.queue.UserQueueDO;
import com.capol.notify.manage.domain.model.queue.UserQueueListener;
import com.capol.notify.manage.domain.repository.UserQueueMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户队列操作监听器
 */
@Slf4j
@Service
public class UserQueueListenerForOperation implements UserQueueListener {

    private final QueueMQService queueMQService;
    private final UserQueueMapper userQueueMapper;

    public UserQueueListenerForOperation(UserQueueMapper userQueueMapper, QueueMQService queueMQService) {
        this.userQueueMapper = userQueueMapper;
        this.queueMQService = queueMQService;
    }

    /**
     * 队列注册（每次添加新的队列时触发）
     *
     * @param queueIds
     */
    @Override
    public void register(List<Long> queueIds) {
        if (CollectionUtils.isEmpty(queueIds)) {
            log.warn("-->指定的队列:{} 不存在, 数据库中没有该队列配置信息!", queueIds);
            return;
        }
        LambdaQueryWrapper<UserQueueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserQueueDO::getId, queueIds);
        queryWrapper.eq(UserQueueDO::getStatus, EnumStatusType.NORMAL.getCode());
        try {
            List<UserQueueDO> userQueueDOS = userQueueMapper.selectList(queryWrapper);
            queueMQService.registrationQueue(userQueueDOS);
            log.info("-->Admin端实现的【注册】监听事件, 向MQ注册新增队列成功!");
        } catch (Exception exception) {
            log.error("-->Admin端实现的【注册】监听事件, 向MQ注册新增队列失败!");
        }
    }

    /**
     * 队列删除（每次删除队列时触发）
     *
     * @param queueIds
     */
    @Override
    public void delete(List<Long> queueIds) {
        if (CollectionUtils.isEmpty(queueIds)) {
            log.warn("-->指定的队列:{} 不存在, 数据库中没有该队列配置信息!", queueIds);
            return;
        }
        LambdaQueryWrapper<UserQueueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserQueueDO::getId, queueIds);
        queryWrapper.eq(UserQueueDO::getStatus, EnumStatusType.DELETE.getCode());

        try {
            List<UserQueueDO> userQueueDOS = userQueueMapper.selectList(queryWrapper);
            queueMQService.deleteQueue(userQueueDOS);
            log.info("-->Admin端实现的【删除】监听事件, 向MQ删除队列成功!");
        } catch (Exception exception) {
            log.error("-->Admin端实现的【删除】监听事件, 向MQ删除队列失败!");
        }
    }

    /**
     * 更改队列（每次更改队列时触发）
     *
     * @param queueIds
     */
    @Override
    public void update(List<Long> queueIds) {
        log.info("-->Admin端实现的【更改】监听事件, 需要向MQ删除队列后重新注册队列!");
    }

    /**
     * 禁用队列（每次禁用队列时触发）
     *
     * @param queueIds
     */
    @Override
    public void disabled(List<Long> queueIds) {
        log.info("-->Admin端实现的【禁用】监听事件, 需要向MQ删除队列后重新注册队列!");
    }

    /**
     * 启用队列（每次启用队列时触发）
     *
     * @param queueIds
     */
    @Override
    public void enabled(List<Long> queueIds) {
        if (CollectionUtils.isEmpty(queueIds)) {
            log.warn("-->指定的队列:{} 不存在, 数据库中没有该队列配置信息!", queueIds);
            return;
        }
        LambdaQueryWrapper<UserQueueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserQueueDO::getId, queueIds);
        queryWrapper.eq(UserQueueDO::getStatus, EnumStatusType.NORMAL.getCode());
        try {
            List<UserQueueDO> userQueueDOS = userQueueMapper.selectList(queryWrapper);
            queueMQService.registrationQueue(userQueueDOS);
            log.info("-->Admin端实现的【注册】监听事件, 向MQ注册启用队列成功!");
        } catch (Exception exception) {
            log.error("-->Admin端实现的【注册】监听事件, 向MQ注册启用队列失败!");
        }
    }
}
