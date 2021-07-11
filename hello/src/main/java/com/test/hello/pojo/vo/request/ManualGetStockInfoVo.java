package com.test.hello.pojo.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by Lixq
 * @Classname ManualGetStockInfoVo
 * @Description TODO
 * @Date 2021/6/27 12:56
 */
@Data
public class ManualGetStockInfoVo {
    @ApiModelProperty(value = "开始日",example = "20210706")
    private String startDate;

    @ApiModelProperty(value = "结束日",example = "20210709")
    private String endDate;
}
