package com.test.hello.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.test.common.util.DateUtil;
import com.test.common.util.HttpSslClientUtil;
import com.test.hello.pojo.dao.StockDetailsInfo;
import com.test.hello.pojo.vo.request.ManualGetStockInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

import static com.test.hello.config.LoadingStockConfig.STOCK_BASE_INFO_MAP;

/**
 * @author by Lixq
 * @Classname AsyncStock
 * @Description TODO
 * @Date 2021/7/11 17:47
 */
@Service
@Slf4j
public class AsyncStock {

    private static String DETAILS_HISTORY_INFO_URL = "https://q.stock.sohu.com/hisHq";

    /**
     * 多线程手动自定义保存每日信息
     *
     * @param stockCode
     * @param stockDetailsInfoList
     * @param manualGetStockInfoVo
     * @param map
     * @return
     */
    // todo @Async有两个使用的限制 ,1它必须仅适用于 public 方法，2在同一个类中调用异步方法将无法正常工作(self-invocation)
    @Async("threadPoolTaskExecutor")
    public List<StockDetailsInfo> task1(String stockCode, List<StockDetailsInfo> stockDetailsInfoList,
                                        ManualGetStockInfoVo manualGetStockInfoVo, HashMap<String, String> map, int i, Semaphore semaphore) {
        try {
            semaphore.acquire();
            String code = stockCode.substring(2, 8);
            HashMap<String, String> paramMap = new HashMap<>(8);
            paramMap.put("code", "cn_" + code);
            paramMap.put("start", manualGetStockInfoVo.getStartDate());
            paramMap.put("end", manualGetStockInfoVo.getEndDate());

            String responseString = HttpSslClientUtil.httpGet(DETAILS_HISTORY_INFO_URL, paramMap, map);
            int length = responseString.length();
            String substring = responseString.substring(1, length - 2);
            JSONObject jsonObject = JSONObject.parseObject(substring);
            if (null == jsonObject || !jsonObject.containsKey("hq")) {
                return stockDetailsInfoList;
            }
            JSONArray jsonArray = jsonObject.getJSONArray("hq");

            Iterator<Object> it = jsonArray.iterator();
            while (it.hasNext()) {
                JSONArray jsonAry = (JSONArray) it.next();
                String string = jsonAry.getString(0);
                Date date = DateUtil.parseDate(string);
                StockDetailsInfo stockDetailsInfo = StockDetailsInfo.builder().openingDate(date).
                        stockName(STOCK_BASE_INFO_MAP.get(code)).
                        stockCode(code).
                        openingPriceToday(jsonAry.getString(1)).
                        currentPrice(jsonAry.getString(2)).
                        quoteChange(jsonAry.getString(4)).
                        lowestPriceToday(jsonAry.getString(5)).
                        highestPriceToday(jsonAry.getString(6)).
                        volume(jsonAry.getString(7)).
                        turnoverRate(jsonAry.getString(9)).build();
                System.out.println("线程名：" + Thread.currentThread().getName() + "  第几个： " +  i++ + " ：  " + stockDetailsInfo);
                stockDetailsInfoList.add(stockDetailsInfo);
            }
        } catch (Exception e) {
            log.info("get Stock Exception ,stockCode is : {}", stockCode);
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
        return stockDetailsInfoList;
    }
}
