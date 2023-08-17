package com.capol.notify.consumer.domain.model.dtalk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("文件媒体信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMediaVO {

    @ApiModelProperty("文件媒体编号")
    private String mediaId;

    @ApiModelProperty("文件地址")
    private String filePath;

}