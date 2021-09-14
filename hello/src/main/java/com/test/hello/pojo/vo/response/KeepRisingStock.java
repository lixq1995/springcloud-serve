package com.test.hello.pojo.vo.response;

import lombok.Data;

/**
 * @author by Lixq
 * @Classname KeepRisingStock
 * @Description 持续上涨股票
 * @Date 2021/7/15 22:58
 */
@Data
public class KeepRisingStock {
    private String stockName;
    private String stockCode;
    private double quoteChange;
}
