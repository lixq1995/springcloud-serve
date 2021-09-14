package com.test.hello.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.test.common.util.DateUtils;
import com.test.common.util.HttpSslClientUtil;
import com.test.hello.mapper.StockDetailsInfoMapper;
import com.test.hello.mapper.StockInfoMapper;
import com.test.hello.pojo.dao.StockBaseInfo;
import com.test.hello.pojo.dao.StockDetailsInfo;
import com.test.hello.pojo.dto.QueryStockBaseInfoDto;
import com.test.hello.pojo.vo.request.FilterStockInfoVo;
import com.test.hello.pojo.vo.request.ManualGetStockInfoVo;
import com.test.hello.pojo.vo.response.FilterStockInfo;
import com.test.hello.pojo.vo.response.KeepRisingStock;
import com.test.hello.pojo.vo.response.KeepRisingTurnoverRateStock;
import com.test.hello.pojo.vo.response.TurnoverRateStock;
import com.test.hello.service.IStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


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
            log.info("次数 ：{}", (i + 1));
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
            Date date = DateUtils.parseDate1(substringDate);
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


    /**
     * 多线程手动自定义保存每日信息
     *
     * @param stockCode
     * @param manualGetStockInfoVo
     * @param map
     * @return
     */
    // todo @Async有两个使用的限制 ,1它必须仅适用于 public 方法，2在同一个类中调用异步方法将无法正常工作(self-invocation)
    // 不能同时使用@Async线程池与CompletableFuture.supplyAsync。 否则CompletableFuture.supplyAsync返回空指针
    // @Async("threadPoolTaskExecutor")
    // 直接传入for循环外new的stockDetailsInfo对象，500次，其中每次赋值都是对同一个对象操作，最终会覆盖之前的，保存500个同一的值
    public List<StockDetailsInfo> task1(String stockCode, ManualGetStockInfoVo manualGetStockInfoVo,
                                        HashMap<String, String> map/*,StockDetailsInfo stockDetailsInfo*/) {
        ArrayList<StockDetailsInfo> stockDetailsInfoList = new ArrayList<>();
        try {
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
                Date date = DateUtils.parseDate(string);
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
//                log.info("线程名：{}  股票信息 ：{}", Thread.currentThread().getName() ,stockDetailsInfo);
                stockDetailsInfoList.add(stockDetailsInfo);
            }
        } catch (Exception e) {
            log.info("get Stock Exception ,stockCode is : {}", stockCode);
            e.printStackTrace();
        }
        return stockDetailsInfoList;
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
                List<StockDetailsInfo> listCompletableFuture = asyncGetStock(queryCodeList, manualGetStockInfoVo, map);
                stockDetailsInfoMapper.batchSave(listCompletableFuture);
                // 剔除已经插入的
                codeList.subList(0, pointsDataLimit).clear();
                stringBuffer.setLength(0);
            }
            if (codeList.size() > 0) {
                List<StockDetailsInfo> listCompletableFuture = asyncGetStock(codeList, manualGetStockInfoVo, map);
                //新增最后剩下的
                stockDetailsInfoMapper.batchSave(listCompletableFuture);
            }
        } else {
            System.out.println("小于500条直接进这里");
        }
        log.info("success");
        return "success";
    }


    public List<StockDetailsInfo> asyncGetStock(List<String> queryCodeList, ManualGetStockInfoVo manualGetStockInfoVo, HashMap<String, String> map) {
        // todo stockDetailsInfoList参数不能直接传到task1方法中，如果传值，tast1返回return时为null,stockDetailsInfoList.addAll(s);报空指针
        List<StockDetailsInfo> stockDetailsInfoList = Collections.synchronizedList(new ArrayList<>(1024));
        // todo com.test.common.util.HttpSslClientUtil   : Connection timed out: connect
        // 如果线程过多，线程间切换，当某个线程第一次拿到资源，然后被中断，然后2.5秒后还没再次拿到资源，HttpSslClientUtil会报超时
        // forEach改为lambda表达式，lambda的for循环可以定义CompletableFuture来接受， forEach不行，最终不能使用join，等待500线程全部执行完再入库
        CompletableFuture[] completableFutures = queryCodeList.stream().map(stockCode -> CompletableFuture.supplyAsync(() -> {
            List<StockDetailsInfo> stockDetailsInfos = task1(stockCode, manualGetStockInfoVo, map);
            return stockDetailsInfos;
        }, threadPoolTaskExecutor).
                whenComplete((s, e) -> {
                    stockDetailsInfoList.addAll(s);
                })).toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(completableFutures).join();
        return stockDetailsInfoList;
    }

    /*public static void main(String[] args) {
        ManualGetStockInfoVo manualGetStockInfoVo = new ManualGetStockInfoVo();
        manualGetStockInfoVo.setEndDate("20210706");
        manualGetStockInfoVo.setStartDate("20210706");
        task("sh605028",new ArrayList<>(),manualGetStockInfoVo,new HashMap<>(),1);
    }*/

    @Override
    public FilterStockInfo filterStockInfo(FilterStockInfoVo filterStockInfoVo) {
        // 根据开始时间与结束时间查出所有符合的股票
        HashMap<String, List> map = new HashMap<>();
        List<StockDetailsInfo> stockDetailsList = stockDetailsInfoMapper.getStockDetailsList(filterStockInfoVo);
        stockDetailsList.stream().forEach(stockDetailsInfo -> {
            List<StockDetailsInfo> list = map.getOrDefault(stockDetailsInfo.getStockCode(), new ArrayList());
            list.add(stockDetailsInfo);
            map.put(stockDetailsInfo.getStockCode(), list);
        });
        FilterStockInfo filterStockInfo = new FilterStockInfo();
        List<KeepRisingStock> keepRisingStockList = new ArrayList<>();
        List<TurnoverRateStock> turnoverRateStockList = new ArrayList<>();
        List<KeepRisingTurnoverRateStock> keepRisingTurnoverRateStockList = new ArrayList<>();
        for (Map.Entry<String, List> stock : map.entrySet()) {
            String key = stock.getKey();
            List<StockDetailsInfo> stockList = stock.getValue();
            System.out.println(JSON.toJSONString(stockList));
            int openingDatesize = stockList.size();
            // 按时间倒序排序
            stockList.sort(Comparator.comparing(StockDetailsInfo::getOpeningDate).reversed());
            List<Double> quoteChangeList = new ArrayList<>();
            // 换手率
            double turnoverRate = 0;
            // 涨跌幅
            double quoteChange = 0;
            for (StockDetailsInfo detailsInfo : stockList) {
                // 将获取的涨幅与换手率带有的%去掉
                if (detailsInfo.getQuoteChange().contains("%")) {
                    detailsInfo.setQuoteChange(detailsInfo.getQuoteChange().replace("%",""));
                }
                if (detailsInfo.getTurnoverRate().contains("%")) {
                    detailsInfo.setTurnoverRate(detailsInfo.getTurnoverRate().replace("%",""));
                }
                // 将换手率为空的改为字符串0
                if ("".equals(detailsInfo.getTurnoverRate())) {
                    detailsInfo.setTurnoverRate("0");
                }
                // 统计换手率
                turnoverRate = turnoverRate + Double.parseDouble(detailsInfo.getTurnoverRate());
                // 统计涨跌幅
                quoteChange = quoteChange + Double.parseDouble(detailsInfo.getQuoteChange());
                // 将涨幅大于0的放入一个集合中
                if (Double.parseDouble(detailsInfo.getQuoteChange()) > 0) {
                    quoteChangeList.add(Double.parseDouble(detailsInfo.getQuoteChange()));
                }
            }
            double endDateTurnoverRate = Double.parseDouble(stockList.get(0).getTurnoverRate());
            // 除最后一天的平均换手率
            double avgTurnoverRate = (turnoverRate-endDateTurnoverRate)/(openingDatesize - 1);

            // 换手率大于xx倍的,涨幅大于xx的
            if (filterStockInfoVo.getFilterContent().contains("换手率")) {
                if (endDateTurnoverRate/avgTurnoverRate > filterStockInfoVo.getTurnoverRate() && quoteChange > filterStockInfoVo.getQuoteChange()) {
                    TurnoverRateStock turnoverRateStock = new TurnoverRateStock();
                    turnoverRateStock.setStockCode(key);
                    turnoverRateStock.setStockName(STOCK_BASE_INFO_MAP.get(key));
                    turnoverRateStock.setQuoteChange(quoteChange);
                    turnoverRateStockList.add(turnoverRateStock);
                }
            }

            // 连续上涨的
            // 格式化小数
            DecimalFormat df = new DecimalFormat("0.00");
            String format = df.format((float) quoteChangeList.size() / openingDatesize);

            // 涨幅天数大于xx的，涨幅大于xx的
            if (filterStockInfoVo.getFilterContent().contains("保持上升")) {
                if ((Double.parseDouble(format) > filterStockInfoVo.getRisingDays()) && quoteChange > filterStockInfoVo.getQuoteChange()) {
                    KeepRisingStock keepRisingStock = new KeepRisingStock();
                    keepRisingStock.setStockCode(key);
                    keepRisingStock.setStockName(STOCK_BASE_INFO_MAP.get(key));
                    keepRisingStock.setQuoteChange(quoteChange);
                    keepRisingStockList.add(keepRisingStock);
                }
            }

            // 同时满足换手率大于xx，涨幅天数大于xx的，涨幅大于xx的
            if (filterStockInfoVo.getFilterContent().contains("换手加上升")) {
                if ((Double.parseDouble(format) > filterStockInfoVo.getRisingDays()) && (endDateTurnoverRate/avgTurnoverRate > filterStockInfoVo.getTurnoverRate()) && quoteChange > filterStockInfoVo.getQuoteChange()) {
                    KeepRisingTurnoverRateStock keepRisingTurnoverRateStock = new KeepRisingTurnoverRateStock();
                    keepRisingTurnoverRateStock.setStockCode(key);
                    keepRisingTurnoverRateStock.setQuoteChange(quoteChange);
                    keepRisingTurnoverRateStock.setStockName(STOCK_BASE_INFO_MAP.get(key));
                    keepRisingTurnoverRateStockList.add(keepRisingTurnoverRateStock);
                }
            }

        }
        turnoverRateStockList.sort(Comparator.comparing(TurnoverRateStock::getQuoteChange).reversed());
        keepRisingStockList.sort(Comparator.comparing(KeepRisingStock::getQuoteChange).reversed());
        keepRisingTurnoverRateStockList.sort(Comparator.comparing(KeepRisingTurnoverRateStock::getQuoteChange).reversed());
        filterStockInfo.setKeepRisingStockList(keepRisingStockList);
        filterStockInfo.setTurnoverRateStockList(turnoverRateStockList);
        filterStockInfo.setKeepRisingTurnoverRateStockList(keepRisingTurnoverRateStockList);
        return filterStockInfo;
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
