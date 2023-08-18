package com.capol.notify.manage.application.message;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.capol.notify.manage.application.message.querystack.UserQueueMessageDTO;
import com.capol.notify.manage.domain.*;
import com.capol.notify.manage.domain.model.message.UserQueueMessageDO;
import com.capol.notify.manage.domain.repository.UserQueueMessageMapper;
import com.capol.notify.sdk.EnumMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息处理应用层服务
 *
 * @author heyong
 * @since 2023-04-21 16:52:11
 */
@Slf4j
@Service
public class MessageService {

    @Value("${capol.notify.retry.max-count}")
    private Long retryMaxCount;

    private final UserQueueMessageMapper userQueueMessageMapper;

    public MessageService(UserQueueMessageMapper userQueueMessageMapper) {
        this.userQueueMessageMapper = userQueueMessageMapper;
    }

    /**
     * 获取指定MessageID的消息
     *
     * @param id
     * @return
     */
    public UserQueueMessageDO getMessageById(Long id) {
        LambdaQueryWrapper<UserQueueMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserQueueMessageDO::getId, id);
        /** status字段已启用@TableLogic注解
         queryWrapper.eq(UserQueueMessageDO::getStatus, EnumStatusType.NORMAL.getCode());*/
        return userQueueMessageMapper.selectOne(queryWrapper);
    }

    /**
     * 删除指定Ids的消息
     *
     * @param ids
     */
    public void deleteMessageByIds(List<Long> ids) {
        int rows = userQueueMessageMapper.deleteBatchIds(ids);
        log.info("-->删除消息数:{}条! Ids：{}", rows, JSON.toJSONString(ids));
    }

    /**
     * 分页获取当前用户消息列表
     *
     * @param pageParam
     * @param currentUserId
     * @return
     */
    public PageResult<UserQueueMessageDTO> getMessagesByPage(PageParam pageParam, String currentUserId) {
        LambdaQueryWrapper<UserQueueMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        //如果不是管理员,只显示当前用户自己的消息
        if (!SystemConstants.ADMIN_ID.equals(currentUserId)) {
            queryWrapper.eq(UserQueueMessageDO::getUserId, Long.valueOf(currentUserId));
        }
        /** status字段已启用@TableLogic逻辑删除注解
         queryWrapper.eq(UserQueueMessageDO::getStatus, EnumStatusType.NORMAL.getCode());*/
        queryWrapper.orderByAsc(UserQueueMessageDO::getCreatedDatetime);

        PageResult<UserQueueMessageDO> userQueueMessageDOPageResult = userQueueMessageMapper.selectPage(pageParam, queryWrapper);
        List<UserQueueMessageDTO> userQueueMessageDTOS = userQueueMessageDOPageResult.getList().stream().map(o -> new UserQueueMessageDTO(
                o.getServiceId(), o.getUserId(), o.getQueueId(),
                o.getPriority(), o.getMessageType(), o.getBusinessType(),
                o.getContent(), o.getSendResponse(), o.getProcessStatus(),
                o.getRetryCount(), o.getConsumerStartTime(), o.getConsumerEndTime()
        )).collect(Collectors.toList());

        long total = userQueueMessageDOPageResult.getTotal();
        long pages = userQueueMessageDOPageResult.getPages();
        return new PageResult<>(userQueueMessageDTOS, total, pages);
    }

    /**
     * 分页获取指定消息类型的消息
     *
     * @param processStatusTypes
     * @param messageTypes
     * @param startDateTime
     * @param endDataTime
     * @param pageParam
     * @return
     */
    public PageResult<UserQueueMessageDO> getMessageByPage(List<EnumProcessStatusType> processStatusTypes,
                                                           List<EnumMessageType> messageTypes,
                                                           String startDateTime, String endDataTime,
                                                           PageParam pageParam) {

        List<Integer> statusTypes = processStatusTypes.stream().map(EnumProcessStatusType::getCode).collect(Collectors.toList());
        List<String> types = messageTypes.stream().map(EnumMessageType::getCode).collect(Collectors.toList());

        LambdaQueryWrapper<UserQueueMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserQueueMessageDO::getProcessStatus, statusTypes);
        queryWrapper.in(UserQueueMessageDO::getMessageType, types);
        /** status字段已启用@TableLogic逻辑删除注解
         queryWrapper.eq(UserQueueMessageDO::getStatus, EnumStatusType.NORMAL.getCode());*/
        queryWrapper.lt(UserQueueMessageDO::getRetryCount, retryMaxCount);
        queryWrapper.between(UserQueueMessageDO::getCreatedDatetime, startDateTime, endDataTime);
        queryWrapper.orderByAsc(UserQueueMessageDO::getCreatedDatetime);

        log.info("-->开始获取符合重试条件的消息记录, 关键条件值, 消息处理状态:{}, 最大重试次数:{}", statusTypes, retryMaxCount);
        PageResult<UserQueueMessageDO> userQueueMessageDOPageResult = userQueueMessageMapper.selectPage(pageParam, queryWrapper);
        List<UserQueueMessageDO> userQueueMessageDOS = userQueueMessageDOPageResult.getList();
        long total = userQueueMessageDOPageResult.getTotal();
        long pages = userQueueMessageDOPageResult.getPages();
        log.info("-->获取到符合重试条件的消息记录条数：{} 条!", userQueueMessageDOS.size());

        return new PageResult<>(userQueueMessageDOS, total, pages);
    }

    /**
     * 获取指定条件范围的消息总记录数
     *
     * @param processStatusTypes
     * @param messageTypes
     * @param startDateTime
     * @param endDataTime
     * @return
     */
    public Long getTotalCountByParam(List<EnumProcessStatusType> processStatusTypes, List<EnumMessageType> messageTypes,
                                     String startDateTime, String endDataTime) {
        List<Integer> statusTypes = processStatusTypes.stream().map(EnumProcessStatusType::getCode).collect(Collectors.toList());
        List<String> types = messageTypes.stream().map(EnumMessageType::getCode).collect(Collectors.toList());

        LambdaQueryWrapper<UserQueueMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserQueueMessageDO::getProcessStatus, statusTypes);
        queryWrapper.in(UserQueueMessageDO::getMessageType, types);
        /** status字段已启用@TableLogic逻辑删除注解
         queryWrapper.eq(UserQueueMessageDO::getStatus, EnumStatusType.NORMAL.getCode());*/
        queryWrapper.between(UserQueueMessageDO::getCreatedDatetime, startDateTime, endDataTime);
        queryWrapper.orderByAsc(UserQueueMessageDO::getCreatedDatetime);

        return userQueueMessageMapper.selectCount(queryWrapper);
    }

    /**
     * 获取指定条件范围的消息总记录数
     *
     * @param startDateTime
     * @param endDataTime
     * @return
     */
    public List<UserQueueMessageDO> getMessageByDateTime(String startDateTime, String endDataTime) {
        LambdaQueryWrapper<UserQueueMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        /** status字段已启用@TableLogic逻辑删除注解
         queryWrapper.eq(UserQueueMessageDO::getStatus, EnumStatusType.NORMAL.getCode());*/
        queryWrapper.between(UserQueueMessageDO::getCreatedDatetime, startDateTime, endDataTime);
        queryWrapper.orderByAsc(UserQueueMessageDO::getCreatedDatetime);

        return userQueueMessageMapper.selectList(queryWrapper);
    }

    /**
     * 保存或更新消息
     *
     * @param messageDO
     * @return
     */
    public Long saveOrUpdateMessage(UserQueueMessageDO messageDO) {
        UserQueueMessageDO userQueueMessageDO = userQueueMessageMapper.findByMessageId(messageDO.getId());
        if (userQueueMessageDO == null) {
            int rows = userQueueMessageMapper.insert(messageDO);
            if (rows > 0) {
                log.info("-->保存消息成功!");
            } else {
                log.error("-->保存消息失败, 消息ID:{}", messageDO.getId());
            }
        } else {
            messageDO.buildBaseInfo();
            int rows = userQueueMessageMapper.updateById(messageDO);
            if (rows > 0) {
                log.info("-->更新消息成功!");
            } else {
                log.error("-->更新消息失败, 消息ID:{}", messageDO.getId());
            }
        }

        return messageDO.getId();
    }
}
