package com.capol.notify.admin.port.adapter.restapi.controller.queue.parameter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserQueueRequestParam {
    @ApiModelProperty(value = "业务系统用户ID", required = true)
    @NotBlank(message = "业务系统用户ID不能为空")
    private String userId;

    @ApiModelProperty(value = "队列信息", required = true)
    @NotNull(message = "队列信息不能为空")
    @NotEmpty(message = "队列信息不能为空")
    private List<UserQueueData> userQueueDataList;

    @Data
    public class UserQueueData {
        /**
         * 队列ID
         */
        @ApiModelProperty(value = "队列ID", notes = "编辑时不能为空")
        private Long queueId;

        /**
         * 交换机名称
         */
        @ApiModelProperty(value = "交换机名称", required = true)
        @NotBlank(message = "交换机名称不能为空")
        private String exchange;

        /**
         * 路由名称
         */
        @ApiModelProperty(value = "路由名称", required = true)
        @NotBlank(message = "路由名称不能为空")
        private String routing;

        /**
         * 队列名称
         */
        @ApiModelProperty(value = "队列名称", required = true)
        @NotBlank(message = "队列名称不能为空")
        private String queue;

        /**
         * 队列优先级
         */
        @ApiModelProperty("队列优先级")
        private Integer priority;

        /**
         * 队列业务类型
         */
        @ApiModelProperty(value = "队列业务类型", required = true)
        @NotBlank(message = "队列业务类型不能为空")
        private String businessType;
    }
}
