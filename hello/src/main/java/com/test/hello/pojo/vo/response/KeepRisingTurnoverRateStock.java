package com.test.hello.pojo.vo.response;

import lombok.Data;

/**
 * @author by Lixq
 * @Classname KeepRisingTurnoverRateStock
 * @Description TODO
 * @Date 2021/7/18 17:45
 */
@Data
public class KeepRisingTurnoverRateStock {
    private String stockName;
    private String stockCode;
    private double quoteChange;
}
