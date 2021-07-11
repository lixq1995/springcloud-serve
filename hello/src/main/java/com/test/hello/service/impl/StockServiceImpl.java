package com.test.hello.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.test.common.util.DateUtil;
import com.test.common.util.HttpSslClientUtil;
import com.test.hello.mapper.StockDetailsInfoMapper;
import com.test.hello.mapper.StockInfoMapper;
import com.test.hello.pojo.dao.StockBaseInfo;
import com.test.hello.pojo.dao.StockDetailsInfo;
import com.test.hello.pojo.dto.QueryStockBaseInfoDto;
import com.test.hello.pojo.vo.request.FilterStockInfoVo;
import com.test.hello.pojo.vo.request.ManualGetStockInfoVo;
import com.test.hello.service.AsyncStock;
import com.test.hello.service.IStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import static com.test.hello.config.LoadingStockConfig.STOCK_BASE_INFO_MAP;

/**
 * @author by Lixq
 * @Classname StockServiceImpl
 * @Description TODO
 * @Date 2021/6/22 20:35
 */
@Service
@Slf4j
public class StockServiceImpl implements IStockService {

    private static String BASE_INFO_URL = "https://api.doctorxiong.club/v1/stock/rank";

    private static String DETAILS_INFO_URL = "http://qt.gtimg.cn/q=";

    private static String DETAILS_HISTORY_INFO_URL = "https://q.stock.sohu.com/hisHq";

    @Autowired
    private StockInfoMapper stockInfoMapper;

    @Autowired
    private StockDetailsInfoMapper stockDetailsInfoMapper;

    @Autowired
    private AsyncStock asyncStock;


    @Override
    public String getBaseInfo() {
        List<StockBaseInfo> saveStockInfoList = new ArrayList<>();
        // BASE_INFO_URL该接口一次最多返回50条数据，4404个股票，共89页，需查询89次
        for (int i = 0; i < 89; i++) {
            QueryStockBaseInfoDto queryStockBaseInfoDto = QueryStockBaseInfoDto.builder().node("a").sort("turnover").asc(0).pageIndex(i + 1).pageSize(50).build();
            String jsonString = JSON.toJSONString(queryStockBaseInfoDto);
            HashMap<String, String> map = new HashMap<>();
            String responseString = HttpSslClientUtil.httpPost(BASE_INFO_URL, jsonString, map);
            JSONObject jsonObject = JSONObject.parseObject(responseString);
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("rank");
            List<StockBaseInfo> stockBaseInfos = jsonArray.toJavaList(StockBaseInfo.class);
            saveStockInfoList.addAll(stockBaseInfos);
            System.out.println("次数 ：" + (i + 1));
        }
        stockInfoMapper.batchSave(saveStockInfoList);
        return "success";
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String timedTaskGetStockInfo() {
        // todo 增加字段量比
        StockBaseInfo queryCondition = new StockBaseInfo();
        List<StockBaseInfo> stockBaseInfoList = stockInfoMapper.getStockBaseInfoList(queryCondition);
        List<String> codeList = stockBaseInfoList.stream().map(StockBaseInfo::getCode).collect(Collectors.toList());
        HashMap<String, String> map = new HashMap<>(8);
        //限制条数500条一次，数据过多get股票接口报错入参太长
        int pointsDataLimit = 500;
        Integer size = codeList.size();
        //判断是否有必要分批
        if (pointsDataLimit < size) {
            // 分批数
            int part = size / pointsDataLimit;
            StringBuffer stringBuffer = new StringBuffer();
            //500条获取插入一次
            for (int i = 0; i < part; i++) {
                List<String> queryCodeList = codeList.subList(0, pointsDataLimit);
                BatchSaveStockDetailsInfo(map, stringBuffer, queryCodeList);
                //剔除已经插入的
                codeList.subList(0, pointsDataLimit).clear();
                stringBuffer.setLength(0);
            }
            if (codeList.size() > 0) {
                //新增最后剩下的
                BatchSaveStockDetailsInfo(map, stringBuffer, codeList);
            }
        } else {
            System.out.println("小于500条直接进这里");
        }
        return "success";
    }

    /**
     * 每日批量新增StockDetailsInfo
     *
     * @param map
     * @param stringBuffer
     * @param queryCodeList
     */
    private void BatchSaveStockDetailsInfo(HashMap<String, String> map, StringBuffer stringBuffer, List<String> queryCodeList) {
        queryCodeList.stream().forEach(code -> stringBuffer.append(code + ","));
        String url = DETAILS_INFO_URL + stringBuffer.toString();
        String responseString = HttpSslClientUtil.httpGet(url, map, map);
        // 去掉返回的string数据换行符
        String replace = responseString.replace("\n", "");
        List<String> stockList = Arrays.asList(replace.split(";"));
        List<StockDetailsInfo> stockDetailsInfoList = new ArrayList<>();
        stockList.stream().forEach(stock -> {
            // 将单条股票数据通过~分割，获取其各个数据
            List<String> oneStockInfoList = Arrays.asList(stock.split("~"));
            String substringDate = oneStockInfoList.get(30).substring(0, 8);
            Date date = DateUtil.parseDate1(substringDate);
            StockDetailsInfo stockDetailsInfo = StockDetailsInfo.builder().
                    stockName(oneStockInfoList.get(1)).
                    stockCode(oneStockInfoList.get(2)).
                    currentPrice(oneStockInfoList.get(3)).
                    yesterdayPrice(oneStockInfoList.get(4)).
                    openingPriceToday(oneStockInfoList.get(5)).
                    volume(oneStockInfoList.get(6)).
                    innerDisk(oneStockInfoList.get(7)).
                    outerDisk(oneStockInfoList.get(8)).
                    quoteChange(oneStockInfoList.get(32)).
                    highestPriceToday(oneStockInfoList.get(33)).
                    lowestPriceToday(oneStockInfoList.get(34)).
                    turnoverRate(oneStockInfoList.get(38)).
                    peRatioTtm(oneStockInfoList.get(39)).
                    vibrationAmplitude(oneStockInfoList.get(43)).
                    totalMarketCapitalization(oneStockInfoList.get(45)).
                    peRatioDynamic(oneStockInfoList.get(52)).
                    peRatioStatic(oneStockInfoList.get(53)).
                    openingDate(date).build();
            stockDetailsInfoList.add(stockDetailsInfo);

        });
        // 根据第一条数据查询数据库是否已存在该条数据。如果存在则抛异常，此次定时任务不保存
        // 主要是防止周六周日定时任务同步到周五的老数据
        StockDetailsInfo oldStockDetailsInfo = stockDetailsInfoMapper.getStockDetailsInfo(stockDetailsInfoList.get(0));
        if (oldStockDetailsInfo != null) {
            log.error("data already exists, don't sync again!");
            throw new RuntimeException();
        }
        // 批量新增
        stockDetailsInfoMapper.batchSave(stockDetailsInfoList);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String manualGetStockInfo(ManualGetStockInfoVo manualGetStockInfoVo) {
        StockBaseInfo queryCondition = new StockBaseInfo();
        List<StockBaseInfo> stockBaseInfoList = stockInfoMapper.getStockBaseInfoList(queryCondition);
        List<String> codeList = stockBaseInfoList.stream().map(StockBaseInfo::getCode).collect(Collectors.toList());


        HashMap<String, String> map = new HashMap<>(8);
        //限制条数500条一次，数据过多get股票接口报错入参太长
        int pointsDataLimit = 500;
        Integer size = codeList.size();
        //判断是否有必要分批
        if (pointsDataLimit < size) {
            // 分批数
            int part = size / pointsDataLimit;
            StringBuffer stringBuffer = new StringBuffer();
            // 500条获取插入一次
            for (int i = 0; i < part; i++) {
                List<String> queryCodeList = codeList.subList(0, pointsDataLimit);
//                List<StockDetailsInfo> stockDetailsInfoList = new ArrayList<>();
                int j = 1;
                CompletableFuture<List<StockDetailsInfo>> listCompletableFuture = asyncGetStock(queryCodeList, manualGetStockInfoVo, map, j);
                List<StockDetailsInfo> join = listCompletableFuture.join();




                /*int j = 1;
                for (String stockCode : queryCodeList) {
                    stockDetailsInfoList = task(stockCode, stockDetailsInfoList, manualGetStockInfoVo, map, j++);
                }*/
                stockDetailsInfoMapper.batchSave(join);
                // 剔除已经插入的
                codeList.subList(0, pointsDataLimit).clear();
                stringBuffer.setLength(0);
            }
            if (codeList.size() > 0) {
//                List<StockDetailsInfo> stockDetailsInfoList = new ArrayList<>();
                /*List<StockDetailsInfo> stockDetailsInfoList = Collections.synchronizedList(new ArrayList<>());
                int j = 1;
                for (String stockCode : codeList) {
                    stockDetailsInfoList = task(stockCode, stockDetailsInfoList, manualGetStockInfoVo, map, j);
                }*/
                int j = 1;
                CompletableFuture<List<StockDetailsInfo>> listCompletableFuture = asyncGetStock(codeList, manualGetStockInfoVo, map, j);
                List<StockDetailsInfo> join = listCompletableFuture.join();
                //新增最后剩下的
                stockDetailsInfoMapper.batchSave(join);
            }
        } else {
            System.out.println("小于500条直接进这里");
        }
        return "success";
    }


    public CompletableFuture<List<StockDetailsInfo>> asyncGetStock(List<String> queryCodeList, ManualGetStockInfoVo manualGetStockInfoVo, HashMap<String, String> map, int num) {
//        List<StockDetailsInfo> stockDetailsInfoList = new ArrayList<>();
        List<StockDetailsInfo> stockDetailsInfoList = Collections.synchronizedList(new ArrayList<>(1024));
        Semaphore semaphore = new Semaphore(queryCodeList.size());
        for (String stockCode : queryCodeList) {
            stockDetailsInfoList = asyncStock.task1(stockCode, stockDetailsInfoList, manualGetStockInfoVo, map, num++, semaphore);
        }
        return CompletableFuture.completedFuture(stockDetailsInfoList);
    }

    /**
     * 手动自定义保存每日信息
     *
     * @param stockCode
     * @param stockDetailsInfoList
     * @param manualGetStockInfoVo
     * @param map
     * @return
     */
    public static List<StockDetailsInfo> task(String stockCode, List<StockDetailsInfo> stockDetailsInfoList,
                                              ManualGetStockInfoVo manualGetStockInfoVo, HashMap<String, String> map, int i) {
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
            System.out.println(i++ + " ：  " + stockDetailsInfo);
            stockDetailsInfoList.add(stockDetailsInfo);
        }
        return stockDetailsInfoList;
    }

    /*public static void main(String[] args) {
        ManualGetStockInfoVo manualGetStockInfoVo = new ManualGetStockInfoVo();
        manualGetStockInfoVo.setEndDate("20210706");
        manualGetStockInfoVo.setStartDate("20210706");
        task("sh605028",new ArrayList<>(),manualGetStockInfoVo,new HashMap<>(),1);
    }*/

    @Override
    public List<StockDetailsInfo> filterStockInfo(FilterStockInfoVo filterStockInfoVo) {
        return null;
    }

    // TODO 多线程，测试
    // 计时工具 StopWatch：https://www.cnblogs.com/kingsonfu/p/11175524.html
    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<String> async(int sleepTime, String str) {
        StopWatch stopWatch = new StopWatch("计时器");
        stopWatch.start(str);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String hello = str;
        stopWatch.stop();
        System.out.println(str + " 耗时 " + stopWatch.getTotalTimeMillis() + Thread.currentThread().getName());
        System.out.println("计时器整体数据" + stopWatch.prettyPrint());
        return CompletableFuture.completedFuture(hello);
    }
}
