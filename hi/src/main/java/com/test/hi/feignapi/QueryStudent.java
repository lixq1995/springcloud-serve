package com.test.hi.feignapi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by Lixq
 * @Classname QueryStudent
 * @Description TODO
 * @Date 2021/4/7 22:04
 */
@Data
public class QueryStudent {
    @ApiModelProperty(value = "第几页")
    private int pageNum = 1;

    @ApiModelProperty(value = "页大小")
    private int pageSize = 10;

    @ApiModelProperty(value = "年级")
    private String[] grades;

    @ApiModelProperty(value = "模糊查询关键字")
    private String keyword;
}
