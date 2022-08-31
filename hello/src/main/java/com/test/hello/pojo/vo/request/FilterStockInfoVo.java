package com.test.hello.pojo.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author by Lixq
 * @Classname FilterStockInfoByFundVo
 * @Description TODO
 * @Date 2021/6/27 19:05
 */
@Data
public class FilterStockInfoVo {

    private String days;

    @ApiModelProperty(value = "开始日",example = "2021-06-15")
    private String startDate;

    @ApiModelProperty(value = "结束日",example = "2021-07-15")
    private String endDate;

    @ApiModelProperty(value = "换手率",example = "3")
    private double turnoverRate;

    @ApiModelProperty(value = "上涨天数占比",example = "0.66")
    private double risingDays;

    @ApiModelProperty(value = "涨幅",example = "5.0")
    private double quoteChange;

    @ApiModelProperty(value = "筛选内ring",example = "换手率,保持上升,换手加上升")
    @NotBlank(message = "filterContent不能为空")
    private String filterContent;
}
