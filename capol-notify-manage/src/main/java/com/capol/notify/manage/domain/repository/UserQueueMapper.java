package com.capol.notify.manage.domain.repository;

import com.capol.notify.manage.domain.model.queue.UserQueueDO;
import com.capol.notify.manage.domain.model.user.UserId;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserQueueMapper extends BaseMapperX<UserQueueDO> {
    List<UserQueueDO> findByUserId(UserId userId);

    /**
     * 批量插入队列
     *
     * @param userQueueDOS
     * @return
     */
    int batchInsertQueues(@Param("userQueueDOS") List<UserQueueDO> userQueueDOS);

    /**
     * 批量删除队列
     *
     * @param userQueueIds
     * @return
     */
    int batchDeleteQueues(@Param("userQueueIds") List<Long> userQueueIds);

    /**
     * 批量禁用队列
     *
     * @param userQueueIds
     * @return
     */
    int batchDisableQueues(@Param("userQueueIds") List<Long> userQueueIds);

    /**
     * 批量禁用队列
     *
     * @param userQueueIds
     * @return
     */
    int batchEnableQueues(@Param("userQueueIds") List<Long> userQueueIds);
}
