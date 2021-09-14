package com.test.hello.pojo.vo.response;

import lombok.Data;

import java.util.List;

/**
 * @author by Lixq
 * @Classname Fil
 * @Description TODO
 * @Date 2021/7/15 22:52
 */
@Data
public class FilterStockInfo {
    private List<TurnoverRateStock> turnoverRateStockList;
    private List<KeepRisingStock> keepRisingStockList;
    private List<KeepRisingTurnoverRateStock> KeepRisingTurnoverRateStockList;
}
