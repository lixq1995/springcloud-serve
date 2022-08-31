package com.test.hello.pojo.vo.response;

import com.test.hello.pojo.vo.response.stock.CapitalFlowStock;
import com.test.hello.pojo.vo.response.stock.CjeRisePlate;
import com.test.hello.pojo.vo.response.stock.FastChangePlate;
import com.test.hello.pojo.vo.response.stock.PressureLevelStock;
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
    private List<CapitalFlowStock> capitalFlowStockList;
    private List<CjeRisePlate> cjeRisePlateList;
    private List<FastChangePlate> fastChangePlateList;
    private List<PressureLevelStock> pressureLevelStockList;
}
