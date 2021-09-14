package com.test.hello.pojo.vo.response;

import lombok.Data;

/**
 * @author by Lixq
 * @Classname TurnoverRateStock
 * @Description 成交量放大2倍股票
 * @Date 2021/7/15 22:57
 */
@Data
public class TurnoverRateStock {
    private String stockName;
    private String stockCode;
    private double quoteChange;
}
