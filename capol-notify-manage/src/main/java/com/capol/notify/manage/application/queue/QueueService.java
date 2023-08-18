package com.capol.notify.manage.application.queue;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.capol.notify.manage.application.ApplicationException;
import com.capol.notify.manage.application.user.querystack.UserQueueDTO;
import com.capol.notify.manage.domain.*;
import com.capol.notify.manage.domain.model.queue.UserQueueDO;
import com.capol.notify.manage.domain.model.queue.UserQueueEventCenter;
import com.capol.notify.manage.domain.repository.UserQueueMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 队列处理应用层服务
 *
 * @author heyong
 * @since 2023-04-21 16:52:21
 */
@Slf4j
@Service
public class QueueService {
    private final QueueMQService queueMQService;
    private final UserQueueMapper userQueueMapper;

    public QueueService(QueueMQService queueMQService,
                        UserQueueMapper userQueueMapper) {
        this.queueMQService = queueMQService;
        this.userQueueMapper = userQueueMapper;
    }

    /**
     * 分页获取当前用户的队列信息
     *
     * @param pageParam
     * @param currentUserId
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult<UserQueueDTO> getCurrentUserQueues(PageParam pageParam, String currentUserId) {
        LambdaQueryWrapper<UserQueueDO> queryWrapper = new LambdaQueryWrapper<>();
        //如果不是管理员,只显示当前用户自己的队列
        if (!SystemConstants.ADMIN_ID.equals(currentUserId)) {
            queryWrapper.eq(UserQueueDO::getUserId, currentUserId);
        }
        /** status字段已启用@TableLogic注解
         queryWrapper.eq(UserQueueDO::getStatus, EnumStatusType.NORMAL.getCode());*/

        PageResult<UserQueueDO> userQueueDOPageResult = userQueueMapper.selectPage(pageParam, queryWrapper);
        List<UserQueueDTO> userQueueDTOS = userQueueDOPageResult.getList().stream()
                .map(queue -> new UserQueueDTO(
                        queue.getId(),
                        queue.getUserId(),
                        queue.getExchange(),
                        queue.getRouting(),
                        queue.getQueue(),
                        queue.getPriority(),
                        queue.getBusinessType(),
                        queue.getDisabled()
                )).collect(Collectors.toList());

        long total = userQueueDOPageResult.getTotal();
        long pages = userQueueDOPageResult.getPages();
        return new PageResult<>(userQueueDTOS, total, pages);
    }

    /**
     * 根据用户ID和业务类型获取队列
     *
     * @param userId
     * @param businessType
     * @return
     */
    public UserQueueDTO getUserQueueByIdAndType(Long userId, String businessType) {
        LambdaQueryWrapper<UserQueueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserQueueDO::getUserId, userId);
        queryWrapper.eq(UserQueueDO::getBusinessType, businessType);
        /** status字段已启用@TableLogic注解
         queryWrapper.eq(UserQueueDO::getStatus, EnumStatusType.NORMAL.getCode());*/
        queryWrapper.eq(UserQueueDO::getDisabled, false);

        UserQueueDO userQueueDO = userQueueMapper.selectOne(queryWrapper);
        if (userQueueDO != null && !userQueueDO.getDisabled()) {
            return new UserQueueDTO(
                    userQueueDO.getId(),
                    userQueueDO.getUserId(),
                    userQueueDO.getExchange(),
                    userQueueDO.getRouting(),
                    userQueueDO.getQueue(),
                    userQueueDO.getPriority(),
                    userQueueDO.getBusinessType(),
                    userQueueDO.getDisabled()
            );
        } else {
            log.warn("-->用户ID:{} 业务类型:{} 的消息队列已禁用或不存在,请检查配置!", userId, businessType);
            return null;
        }
    }

    /**
     * 校验队列是否合法
     *
     * @param userQueueDTOS
     * @return
     */
    public Boolean verificationUserQueues(List<UserQueueDTO> userQueueDTOS) {
        if (CollectionUtils.isNotEmpty(userQueueDTOS)) {
            for (UserQueueDTO userQueue : userQueueDTOS) {
                LambdaQueryWrapper<UserQueueDO> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper
                        .eq(UserQueueDO::getExchange, userQueue.getExchange())
                        .eq(UserQueueDO::getRouting, userQueue.getRouting())
                        .eq(UserQueueDO::getQueue, userQueue.getQueue());
                if (userQueueMapper.selectCount(queryWrapper) > 0) {
                    throw new ApplicationException(String.format("队列名称：%s 在指定的交换机:%s 路由:%s 下已经存在,不能重复添加!",
                            userQueue.getQueue(),
                            userQueue.getExchange(),
                            userQueue.getRouting()), EnumExceptionCode.InternalServerError);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 批量插入用户队列
     *
     * @param userQueueDTOS
     * @return
     */
    public int insertUserQueues(List<UserQueueDTO> userQueueDTOS) {
        int rows = 0;
        List<Long> queueIds = new ArrayList<>();
        List<UserQueueDO> userQueueDOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(userQueueDTOS)) {
            return rows;
        }
        userQueueDTOS.forEach(o -> {
            UserQueueDO userQueueDO = new UserQueueDO(
                    o.getUserId(),
                    o.getExchange(),
                    o.getRouting(),
                    o.getQueue(),
                    o.getPriority(),
                    o.getBusinessType()
            );
            userQueueDO.buildBaseInfo();
            userQueueDOS.add(userQueueDO);
            queueIds.add(userQueueDO.getId());
        });

        rows = userQueueMapper.batchInsertQueues(userQueueDOS);
        if (rows > 0) {
            log.info("-->插入用户队列成功,影响行数:{} 行!", rows);
            UserQueueEventCenter.doRegister(queueIds);
        } else {
            log.error("-->插入用户队列失败 , 队列详情:{} ", JSONObject.toJSONString(userQueueDOS));
        }
        return rows;
    }

    /**
     * 批量删除用户队列
     *
     * @param queueIds
     * @return
     */
    public int deleteUserQueue(List<Long> queueIds) {
        int rows = 0;
        if (CollectionUtils.isEmpty(queueIds)) {
            log.info("-->指的删除条件queueIds={},不能为空!", queueIds);
            return rows;
        }
        LambdaQueryWrapper<UserQueueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserQueueDO::getId, queueIds);
        /** status字段已启用@TableLogic逻辑删除注解
         queryWrapper.eq(UserQueueDO::getStatus, EnumStatusType.NORMAL.getCode());*/
        List<UserQueueDO> userQueueDOS = userQueueMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(userQueueDOS)) {
            log.info("-->没有符合条件queueIds={}的队列信息!", queueIds);
            return rows;
        }

        List<String> queueNames = userQueueDOS.stream().map(UserQueueDO::getQueue).collect(Collectors.toList());
        Map<String, Integer> countMaps = queueMQService.getQueueMessageCount(queueNames);
        if (countMaps.size() > 0) {
            log.error("-->队列:{}在消息服务器上还有未消费完的消息,暂时不能删除!", JSONObject.toJSONString(countMaps));
            throw new ApplicationException(String.format("队列:%s在消息服务器上还有未消费完的消息,暂时不能删除!", JSONObject.toJSONString(countMaps)), EnumExceptionCode.InternalServerError);
        } else {
            log.info("-->即将删除的队列名称及消息数量：{}", JSONObject.toJSONString(countMaps));
        }
        rows = userQueueMapper.deleteBatchIds(queueIds);
        if (rows > 0) {
            log.info("-->删除用户队列成功,影响行数:{} 行!", rows);
            UserQueueEventCenter.doDeleteByQueues(userQueueDOS);
        } else {
            log.error("-->删除用户队列失败 , 队列Ids:{} ", queueIds);
        }
        return rows;
    }

    /**
     * 批量禁用用户队列
     *
     * @param queueIds
     * @return
     */
    public int disableUserQueue(List<Long> queueIds) {
        int rows = 0;
        if (CollectionUtils.isEmpty(queueIds)) {
            log.info("-->指的禁用条件queueIds={},不能为空!", queueIds);
            return rows;
        }
        LambdaQueryWrapper<UserQueueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserQueueDO::getId, queueIds);
        /** status字段已启用@TableLogic逻辑删除注解
         queryWrapper.eq(UserQueueDO::getStatus, EnumStatusType.NORMAL.getCode());*/
        queryWrapper.eq(UserQueueDO::getDisabled, false);
        List<UserQueueDO> userQueueDOS = userQueueMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(userQueueDOS)) {
            log.info("-->没有符合条件queueIds={}的队列信息!", queueIds);
            return rows;
        }
        rows = userQueueMapper.batchDisableQueues(queueIds);
        if (rows > 0) {
            log.info("-->禁用用户队列成功,影响行数:{} 行!", rows);
            UserQueueEventCenter.doDisabled(queueIds);
        } else {
            log.error("-->禁用用户队列失败 , 队列queueIds={} ", queueIds);
        }
        return rows;
    }

    /**
     * 批量启用用户队列
     *
     * @param queueIds
     * @return
     */
    public int enableUserQueue(List<Long> queueIds) {
        int rows = 0;
        if (CollectionUtils.isEmpty(queueIds)) {
            log.info("-->指的启用条件queueIds={},不能为空!", queueIds);
            return rows;
        }
        LambdaQueryWrapper<UserQueueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserQueueDO::getId, queueIds);
        /** status字段已启用@TableLogic逻辑删除注解
         queryWrapper.eq(UserQueueDO::getStatus, EnumStatusType.NORMAL.getCode());*/
        queryWrapper.eq(UserQueueDO::getDisabled, true);
        List<UserQueueDO> userQueueDOS = userQueueMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(userQueueDOS)) {
            log.info("-->没有符合条件queueIds={}的队列信息!", queueIds);
            return rows;
        }
        rows = userQueueMapper.batchEnableQueues(queueIds);
        if (rows > 0) {
            log.info("-->启用用户队列成功,影响行数:{} 行!", rows);
            UserQueueEventCenter.doEnabled(queueIds);
        } else {
            log.error("-->启用用户队列失败 , 队列queueIds={} ", queueIds);
        }
        return rows;
    }
}
