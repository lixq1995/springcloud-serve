package com.test.hello.pojo.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author by Lixq
 * @Classname QueryStockBaseInfoDto
 * @Description TODO
 * @Date 2021/6/22 20:36
 */
@Data
@Builder
public class QueryStockBaseInfoDto {
    private String node;
    private String sort;
    private String industryCode;
    private int asc;
    private int pageIndex;
    private int pageSize;
}
