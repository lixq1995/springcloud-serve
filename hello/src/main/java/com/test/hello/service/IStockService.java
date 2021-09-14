package com.test.hello.service;

import com.test.hello.pojo.dao.StockDetailsInfo;
import com.test.hello.pojo.vo.request.FilterStockInfoVo;
import com.test.hello.pojo.vo.request.ManualGetStockInfoVo;
import com.test.hello.pojo.vo.response.FilterStockInfo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author by Lixq
 * @Classname IStockService
 * @Description TODO
 * @Date 2021/6/22 20:34
 */
public interface IStockService {

    /**
     * 获取股票基础数据名字与代码
     * @return
     */
    String getBaseInfo();

    /**
     * 定时任务获取股票数据
     * @return
     */
    String timedTaskGetStockInfo();

    /**
     * 手动获取股票数据
     * @param manualGetStockInfoVo
     * @return
     */
    String manualGetStockInfo(ManualGetStockInfoVo manualGetStockInfoVo);

    /**
     * 筛选股票数据
     * @param filterStockInfoVo
     * @return
     */
    FilterStockInfo filterStockInfo(FilterStockInfoVo filterStockInfoVo);

    /**
     * 测试多线程
     * @param sleepTime
     * @param str
     * @return
     */
    CompletableFuture<String> async(int sleepTime,String str);
}
