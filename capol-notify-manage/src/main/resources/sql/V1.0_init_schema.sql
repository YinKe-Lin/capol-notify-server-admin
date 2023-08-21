CREATE DATABASE `db_capol_notify` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `t_user`
(
    `id`                       bigint       NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `account`                  varchar(80)  NOT NULL COMMENT '账号',
    `password`                 varchar(64)  NULL     DEFAULT NULL COMMENT '登录密码',
    `service_id`               varchar(64)  NULL     DEFAULT NULL COMMENT '业务系统id',
    `service_name`             varchar(128) NULL     DEFAULT NULL COMMENT '业务系统名称',
    `salt`                     varchar(6)   NULL     DEFAULT NULL COMMENT '密码Salt',
    `disabled`                 bit(1)       NULL     DEFAULT NULL COMMENT '是否禁用(0-未禁用 1-禁用)',
    `status`                   tinyint      NOT NULL DEFAULT 1 COMMENT '记录状态 (0.删除 1.正常)',
    `latest_login_datetime`    datetime(6)  NULL     DEFAULT NULL COMMENT '最近一次登录时间',
    `created_datetime`         datetime(6)  NULL     DEFAULT NULL COMMENT '创建时间',
    `latest_modified_datetime` datetime(6)  NULL     DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `UK_account` (`account`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '业务系统用户表';

INSERT INTO db_capol_notify.t_user
(id, account, password, service_id, service_name, salt, disabled, status, latest_login_datetime, created_datetime, latest_modified_datetime)
VALUES(1830828142927777, 'workflow_server_2023', 'b4cdd4acb68a1f4b8ca357f7772800fa4c002970a7076a4102a7e8d26c8d0785', 'workflow_2023_1000001', '流程审批中心服务', '$%5FDS', 0, 1, '2023-05-06 10:00:00', '2023-05-06 10:00:00', '2023-05-06 10:00:00');


CREATE TABLE IF NOT EXISTS `t_user_queue`
(
    `id`                       bigint       NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `user_id`                  bigint       NOT NULL COMMENT '用户id',
    `exchange`                 varchar(80)  NOT NULL COMMENT '队列使用的交换机',
    `routing`                  varchar(100) NULL     DEFAULT NULL COMMENT '队列使用的路由',
    `queue`                    varchar(100) NULL     DEFAULT NULL COMMENT '队列名称',
    `business_type`            varchar(50)  NULL     DEFAULT NULL COMMENT '队列业务类型(通过枚举值定义)',
    `priority`                 int          NULL     DEFAULT 0 COMMENT '队列优先级',
    `disabled`                 bit(1)       NULL     DEFAULT 0 COMMENT '是否禁用(0-未禁用 1-禁用)',
    `status`                   tinyint      NOT NULL DEFAULT 1 COMMENT '记录状态 (0.删除 1.正常)',
    `created_datetime`         datetime(6)  NULL     DEFAULT NULL COMMENT '创建时间',
    `latest_modified_datetime` datetime(6)  NULL     DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `UK_queue` (`queue`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '业务系统使用的队列';


INSERT INTO db_capol_notify.t_user_queue
(id, user_id, exchange, routing, queue, business_type, priority, disabled, status, created_datetime, latest_modified_datetime)
VALUES
(1830828142233215, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.reject.routing', 'workflow.notify.queue.reject', 'WORKFLOW_FOR_REJECT', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233216, 1830828142927777, 'workflow_notify_exchange', 'orkflow.notify.queue.completed.routing', 'workflow.notify.queue.completed', 'WORKFLOW_FOR_COMPLETED', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233217, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.assignment.routing', 'workflow.notify.queue.assignment', 'WORKFLOW_FOR_ASSIGNMENT', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233218, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.delay.routing', 'workflow.notify.queue.delay', 'WORKFLOW_FOR_DELAY', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233219, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.comment.routing', 'workflow.notify.queue.comment', 'WORKFLOW_FOR_COMMENT', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233220, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.finished.routing', 'workflow.notify.queue.finished', 'WORKFLOW_FOR_FINISHED', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233221, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.mentioned.routing', 'workflow.notify.queue.mentioned', 'WORKFLOW_FOR_MENTIONED', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233222, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.undo.routing', 'workflow.notify.queue.undo', 'WORKFLOW_FOR_UNDO', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233223, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.transfer.routing', 'workflow.notify.queue.transfer', 'WORKFLOW_FOR_TRANSFER', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233224, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.cancel.routing', 'workflow.notify.queue.cancel', 'WORKFLOW_FOR_CANCEL', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233225, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.start.routing', 'workflow.notify.queue.start', 'WORKFLOW_FOR_START', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233226, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.stop.routing', 'workflow.notify.queue.stop', 'WORKFLOW_FOR_STOP', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233227, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.back.routing', 'workflow.notify.queue.back', 'WORKFLOW_FOR_BACK', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233228, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.revoke.routing', 'workflow.notify.queue.revoke', 'WORKFLOW_FOR_REVOKE', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233229, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.carboncopy.routing', 'workflow.notify.queue.carboncopy', 'WORKFLOW_FOR_CARBONCOPY', 1, 0, 1, '2023-05-06 10:05:00', '2023-05-06 10:05:00')
,(1830828142233230, 1830828142927777, 'workflow_notify_exchange', 'workflow.notify.queue.sms.routing', 'workflow.notify.queue.sms', 'WORKFLOW_FOR_SMS_NORMAL_MESSAGE', 0, 0, 1, '2023-06-28 20:39:18', '2023-06-28 20:39:18');


CREATE TABLE IF NOT EXISTS `t_user_queue_message`
(
    `id`                  bigint      NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `service_id`          varchar(64) NULL DEFAULT NULL COMMENT '业务系统id',
    `user_id`                  bigint       NOT NULL COMMENT '用户id',
    `queue_id`            bigint      NOT NULL COMMENT '队列id',
    `message_type`        varchar(100)     NOT NULL COMMENT '消息类型(1-钉钉普通消息 2-钉钉群组消息 3-邮件消息)',
    `business_type`       varchar(100)     NOT NULL COMMENT '消息业务类型',
    `priority`            integer     NOT NULL DEFAULT 0 COMMENT '消息优先级',
    `content`             mediumtext  NULL DEFAULT NULL COMMENT '消息内容',
    `send_response`       text        NULL DEFAULT NULL COMMENT '消息发送响应内容',
    `retry_count`         integer     NOT NULL DEFAULT 0 COMMENT '消息处理重试次数',
    `process_status`      tinyint     NOT NULL COMMENT '消息处理状态(0-待处理 1-成功 2-失败)',
    `status`              tinyint     NOT NULL COMMENT '记录状态(0-删除 1-正常)',
    `consumer_start_time` datetime(6) NULL DEFAULT NULL COMMENT '消息消费开始时间',
    `consumer_end_time`   datetime(6) NULL DEFAULT NULL COMMENT '消息消费结束时间',
    `created_datetime`        datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
    `latest_modified_datetime`  datetime(6) NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '业务系统消息表';




-- 添加消息过期时间字段
ALTER  table t_user_queue_message ADD COLUMN `ttl` INT  NULL  DEFAULT 10000 COMMENT '消息过期时间(毫秒)' AFTER `priority`;