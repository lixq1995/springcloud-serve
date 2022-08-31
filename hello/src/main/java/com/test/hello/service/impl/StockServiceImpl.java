package com.test.hello.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.test.common.util.date.DateUtils;
import com.test.common.util.http.HttpSslClientUtil;
import com.test.hello.feignapi.mail.MailApi;
import com.test.hello.mapper.StockDetailsInfoMapper;
import com.test.hello.mapper.StockInfoMapper;
import com.test.hello.mapper.StockPlateInfoMapper;
import com.test.hello.pojo.dao.StockBaseInfo;
import com.test.hello.pojo.dao.StockDetailsInfo;
import com.test.hello.pojo.dao.StockPlateInfo;
import com.test.hello.pojo.dto.QueryStockBaseInfoDto;
import com.test.hello.pojo.vo.request.FilterStockInfoVo;
import com.test.hello.pojo.vo.request.ManualGetStockInfoVo;
import com.test.hello.pojo.vo.request.stock.FilterStockInfoByFundVo;
import com.test.hello.pojo.vo.response.FilterStockInfo;
import com.test.hello.pojo.vo.response.KeepRisingStock;
import com.test.hello.pojo.vo.response.KeepRisingTurnoverRateStock;
import com.test.hello.pojo.vo.response.TurnoverRateStock;
import com.test.hello.pojo.vo.response.stock.CapitalFlowStock;
import com.test.hello.pojo.vo.response.stock.CjeRisePlate;
import com.test.hello.pojo.vo.response.stock.FastChangePlate;
import com.test.hello.pojo.vo.response.stock.PressureLevelStock;
import com.test.hello.service.IStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
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

    @Autowired
    private MailApi mailApi;

    /**
     * 股票基础数据
     */
    private static String BASE_INFO_URL = "https://api.doctorxiong.club/v1/stock/rank";

    /**
     * 每日股票数据
     */
    private static String DETAILS_INFO_URL = "http://qt.gtimg.cn/q=";

    /**
     * 历史股票数据
     */
    private static String DETAILS_HISTORY_INFO_URL = "https://q.stock.sohu.com/hisHq";

    /**
     * 每日股票资金流动
     */
    private static String LIQUIDITY_URL = "http://ig507.com/data/all/zjlx/jlrepm";

    /**
     * ig507 licence标识 即token
     */
    private static String licence = "1F2D8604-4407-8C05-740F-6F2EEF7A6545";

    /**
     * 每日股票资金流动 -- 文档地址http://waizaowang.com/api/detail/117
     */
    private static String LIQUIDITY_URL1 = "http://api.waizaowang.com/doc/getStockHSADailyMarket";

    /**
     * 每日股票资金流动 -- 文档地址http://waizaowang.com/api/detail/117
     */
    private static String PLATE_URL = "http://api.waizaowang.com/doc/getStockHYADailyMarket";

    /**
     * 歪枣网token
     */
    private static String WAI_ZAO_TOKEN = "45473d552325be8e33cfa1ab99830271";


    @Autowired
    private StockInfoMapper stockInfoMapper;

    @Autowired
    private StockDetailsInfoMapper stockDetailsInfoMapper;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private StockPlateInfoMapper stockPlateInfoMapper;


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

    @Override
    public String timedTaskGetStockLiquidity() {
        HashMap<String, String> map = new HashMap<>();
        map.put("licence", licence);
        String responseString = HttpSslClientUtil.httpGet(LIQUIDITY_URL, map, map);
        JSONArray jsonArray = JSONObject.parseArray(responseString);
        List<StockDetailsInfo> list = jsonArray.toJavaList(StockDetailsInfo.class);

        //限制条数500条保存一次，
        int pointsDataLimit = 500;
        Integer size = list.size();
        //判断是否有必要分批
        if (pointsDataLimit < size) {
            // 分批数
            int part = size / pointsDataLimit;
            //500条获取插入一次
            int a = 0;
            for (int i = 0; i < part; i++) {
                List<StockDetailsInfo> updateList = list.subList(0, pointsDataLimit);
                int count = stockDetailsInfoMapper.batchUpdate(updateList);
                a = a + count;
                //剔除已经插入的
                System.out.println("a = " + a);
                list.subList(0, pointsDataLimit).clear();
            }
            if (list.size() > 0) {
                //新增最后剩下的
                int count = stockDetailsInfoMapper.batchUpdate(list);
            }
        } else {
            System.out.println("小于500条直接进这里");
        }
        return "success";
    }

    @Override
    public String timedTaskGetStockLiquidity1() {
        // 构造请求入参
        HashMap<String, String> map = new HashMap<>();
        // all表示查所有股票
        map.put("code", "all");
        map.put("startDate", "2022-01-13");
        map.put("endDate", "2022-01-13");
        // 需要获取的字段值
        map.put("fields", "all");
        // 数据导出类型，取值范围[0，1，2，3，4]。0：Txt字符串，1：Json字符串，2：Txt文件，3：Json文件，4：Csv文件，默认值：0
        map.put("export", "1");
        map.put("token", WAI_ZAO_TOKEN);
        String responseString = HttpSslClientUtil.httpGet(LIQUIDITY_URL1, map, map);
        JSONObject jsonObject = JSONObject.parseObject(responseString);
        if (Objects.isNull(jsonObject) || !jsonObject.containsKey("code") || !"200".equals(jsonObject.getString("code"))) {
            log.error("获取歪枣数据失败,返回错误为 : {}", jsonObject.toJSONString());
            throw new RuntimeException();
        }
        JSONArray data = jsonObject.getJSONArray("data");
        List<StockDetailsInfo> list = data.toJavaList(StockDetailsInfo.class);
        //限制条数500条保存一次，
        int pointsDataLimit = 500;
        Integer size = list.size();
        //判断是否有必要分批
        if (pointsDataLimit < size) {
            // 分批数
            int part = size / pointsDataLimit;
            //500条获取插入一次
            int a = 0;
            for (int i = 0; i < part; i++) {
                List<StockDetailsInfo> updateList = list.subList(0, pointsDataLimit);
                int count = stockDetailsInfoMapper.batchUpdate1(updateList);
                a = a + count;
                //剔除已经插入的
                System.out.println("a = " + a);
                list.subList(0, pointsDataLimit).clear();
            }
            if (list.size() > 0) {
                //新增最后剩下的
                int count = stockDetailsInfoMapper.batchUpdate1(list);
            }
        } else {
            System.out.println("小于500条直接进这里");
        }
        return "success";
    }

    @Override
    public String timedTaskGetStockPlate() {
        // 构造请求入参
        HashMap<String, String> map = new HashMap<>();
        // all表示查所有股票
        map.put("code", "all");
        map.put("startDate", "2022-01-13");
        map.put("endDate", "2022-01-13");
        // 需要获取的字段值
        map.put("fields", "all");
        // 数据导出类型，取值范围[0，1，2，3，4]。0：Txt字符串，1：Json字符串，2：Txt文件，3：Json文件，4：Csv文件，默认值：0
        map.put("export", "1");
        map.put("token", WAI_ZAO_TOKEN);
        String responseString = HttpSslClientUtil.httpGet(PLATE_URL, map, map);
        JSONObject jsonObject = JSONObject.parseObject(responseString);
        if (Objects.isNull(jsonObject) || !jsonObject.containsKey("code") || !"200".equals(jsonObject.getString("code"))) {
            log.error("获取歪枣数据失败,返回错误为 : {}", jsonObject.toJSONString());
            throw new RuntimeException();
        }
        JSONArray data = jsonObject.getJSONArray("data");
        List<StockPlateInfo> list = data.toJavaList(StockPlateInfo.class);
        stockPlateInfoMapper.batchSave(list);
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
                String quoteChange = jsonAry.getString(4).replace("%", "");
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
        // 根据股票id分组
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
            double openingDatesize = stockList.size();
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
                    detailsInfo.setQuoteChange(detailsInfo.getQuoteChange().replace("%", ""));
                }
                if (detailsInfo.getTurnoverRate().contains("%")) {
                    detailsInfo.setTurnoverRate(detailsInfo.getTurnoverRate().replace("%", ""));
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
            double avgTurnoverRate = (turnoverRate - endDateTurnoverRate) / (openingDatesize - 1);

            // 换手率大于xx倍的,涨幅大于xx的
            if (filterStockInfoVo.getFilterContent().contains("换手率")) {
                if (endDateTurnoverRate / avgTurnoverRate > filterStockInfoVo.getTurnoverRate() && quoteChange > filterStockInfoVo.getQuoteChange()) {
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
                if ((Double.parseDouble(format) > filterStockInfoVo.getRisingDays()) && (endDateTurnoverRate / avgTurnoverRate > filterStockInfoVo.getTurnoverRate()) && quoteChange > filterStockInfoVo.getQuoteChange()) {
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

    @Override
    public FilterStockInfo filterStockInfoByFund(FilterStockInfoByFundVo filterStockInfoByFundVo) {
        // 筛选个股，先获取最近7天的数据（实际为5天，包含周末2天）
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE,-10);
        String startTime = DateUtils.formatDate(calendar.getTime());
        String endTime = DateUtils.formatDate(new Date());
        FilterStockInfoVo filterStockInfoVo = new FilterStockInfoVo();
        filterStockInfoVo.setStartDate(startTime);
        filterStockInfoVo.setEndDate(endTime);
        List<StockDetailsInfo> stockDetailsList = stockDetailsInfoMapper.getStockDetailsList(filterStockInfoVo);
        // 根据股票id分组
        HashMap<String, List> map2 = new HashMap<>();
        stockDetailsList.stream().forEach(stockDetailsInfo -> {
            List<StockDetailsInfo> list = map2.getOrDefault(stockDetailsInfo.getStockCode(), new ArrayList());
            list.add(stockDetailsInfo);
            map2.put(stockDetailsInfo.getStockCode(), list);
        });
        // 筛选上涨个股逻辑
        List<TurnoverRateStock> turnoverRateStockList = getTurnoverRateStockList(map2);
        FilterStockInfo filterStockInfo = new FilterStockInfo();
        filterStockInfo.setTurnoverRateStockList(turnoverRateStockList);


        // 筛选板块逻辑
        GregorianCalendar calendar1 = new GregorianCalendar();
        calendar1.setTime(new Date());
        calendar1.add(Calendar.DATE, -8);
        String startTime1 = DateUtils.formatDate(calendar1.getTime());
        String endTime1 = DateUtils.formatDate(new Date());
        FilterStockInfoVo filterStockInfoVo1 = new FilterStockInfoVo();
        filterStockInfoVo1.setStartDate(startTime1);
        filterStockInfoVo1.setEndDate(endTime1);
        List<StockPlateInfo> StockPlateInfoList = stockPlateInfoMapper.getStockPlateInfoList(filterStockInfoVo1);
        // 根据板块id分组
        HashMap<String, List> map1 = new HashMap<>();
        StockPlateInfoList.stream().forEach(StockPlateInfo -> {
            List<StockPlateInfo> list = map1.getOrDefault(StockPlateInfo.getPlateCode(), new ArrayList());
            list.add(StockPlateInfo);
            map1.put(StockPlateInfo.getPlateCode(), list);
        });
        List<CjeRisePlate> cjeRisePlateList = new ArrayList<>();
        List<FastChangePlate> fastChangePlateList = new ArrayList<>();
        for (Map.Entry<String, List> plate : map1.entrySet()) {
            String key = plate.getKey();
            if ("BK0815".equals(key) || "BK0816".equals(key) || "BK1050".equals(key) || "BK1051".equals(key)) {
                continue;
            }

            List<StockPlateInfo> plateList = plate.getValue();
            plateList.sort(Comparator.comparing(StockPlateInfo::getOpeningDate).reversed());
            // 成交量大于前几天平均值的百分之20
            // 除去最后一天的成交额
            double cje = 0;
            for (int i = 1; i < plateList.size(); i++) {
                cje = cje + Double.parseDouble(plateList.get(i).getCje());
            }
            // 除去今天的前几天平均成交额
            double averageCje = cje / (plateList.size() - 1);
            // 今天的成交额除以前几天平均成交额
            double cjeChange = Double.parseDouble(plateList.get(0).getCje()) / averageCje;
            // 异动板块
            if (cjeChange > 1.6 && Double.parseDouble(plateList.get(0).getZf05()) > 2.8) {
                FastChangePlate fastChangePlate = new FastChangePlate();
                fastChangePlate.setPlateCode(plateList.get(0).getPlateCode());
                fastChangePlate.setPlateName(plateList.get(0).getPlateName());
                fastChangePlate.setZf05(plateList.get(0).getZf05());
                fastChangePlate.setZf10(plateList.get(0).getZf10());
                fastChangePlate.setCjeChange(cjeChange);
                fastChangePlateList.add(fastChangePlate);
            }
            // 最近上涨板块
            if (Double.parseDouble(plateList.get(0).getZf05()) > 7.0) {
                CjeRisePlate cjeRisePlate = new CjeRisePlate();
                cjeRisePlate.setPlateName(plateList.get(0).getPlateName());
                cjeRisePlate.setPlateCode(plateList.get(0).getPlateCode());
                cjeRisePlate.setZf05(plateList.get(0).getZf05());
                cjeRisePlate.setZf10(plateList.get(0).getZf10());
                cjeRisePlateList.add(cjeRisePlate);
            }
            // 去重
            cjeRisePlateList = cjeRisePlateList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(CjeRisePlate::getPlateCode))), ArrayList::new));
            fastChangePlateList.sort(Comparator.comparing(FastChangePlate::getCjeChange).reversed());
//            cjeRisePlateList.sort(Comparator.comparing(CjeRisePlate::getZf05).reversed());
            cjeRisePlateList.sort(Comparator.comparing(e -> Double.parseDouble(e.getZf05())));
        }


        // 筛选出最近的底部
        GregorianCalendar calendar2 = new GregorianCalendar();
        calendar2.setTime(new Date());
        calendar2.add(Calendar.DATE,-30);
        String startTime2 = DateUtils.formatDate(calendar2.getTime());
        String endTime2 = DateUtils.formatDate(new Date());
        FilterStockInfoVo filterStockInfoVo2 = new FilterStockInfoVo();
        filterStockInfoVo2.setStartDate(startTime2);
        filterStockInfoVo2.setEndDate(endTime2);
        List<StockDetailsInfo> stockDetailsList2 = stockDetailsInfoMapper.getStockDetailsList(filterStockInfoVo2);
        // 根据股票id分组
        HashMap<String, List> map = new HashMap<>();
        stockDetailsList2.stream().forEach(stockDetailsInfo -> {
            List<StockDetailsInfo> list = map.getOrDefault(stockDetailsInfo.getStockCode(), new ArrayList());
            list.add(stockDetailsInfo);
            map.put(stockDetailsInfo.getStockCode(), list);
        });
        List<PressureLevelStock> pressureLevelStockList = new ArrayList<>();
        for (Map.Entry<String, List> stock : map.entrySet()) {
            String key = stock.getKey();
            List<StockDetailsInfo> stockList = stock.getValue();
            String currentPrice = stockList.get(stockList.size() - 1).getCurrentPrice();
            String turnoverRate = stockList.get(stockList.size() - 1).getTurnoverRate();
            Date currentPriceDate = stockList.get(stockList.size() - 1).getOpeningDate();
            double openingDatesize = stockList.size();
            // 按价格由小到大排序
            stockList.sort(Comparator.comparing(e -> Double.parseDouble(e.getCurrentPrice())));
            String lowestPrice = stockList.get(0).getCurrentPrice();
            Date lowestPriceDate = stockList.get(0).getOpeningDate();
            String highstPrice = stockList.get(stockList.size()-1).getCurrentPrice();
            double lowBaiFenBi = (Double.parseDouble(currentPrice) - Double.parseDouble(lowestPrice))/Double.parseDouble(lowestPrice);
            double highBaiFenBi = (Double.parseDouble(highstPrice) - Double.parseDouble(lowestPrice))/Double.parseDouble(lowestPrice);
            // 获取相差的天数
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(lowestPriceDate);
            long timeInMillis1 = calendar3.getTimeInMillis();
            calendar3.setTime(currentPriceDate);
            long timeInMillis2 = calendar3.getTimeInMillis();

            long betweenDays =  (timeInMillis2 - timeInMillis1) / (1000L*3600L*24L);
            if (lowBaiFenBi > 0.03 && lowBaiFenBi < 0.06 && betweenDays > 10 && Double.parseDouble(turnoverRate) > 1 && highBaiFenBi > 0.66) {
                PressureLevelStock pressureLevelStock = new PressureLevelStock();
                pressureLevelStock.setStockCode(key);
                pressureLevelStock.setStockName(STOCK_BASE_INFO_MAP.get(key));
                pressureLevelStockList.add(pressureLevelStock);
            }

        }



        // 发送邮件测试
//        String str = mailApi.sendMailStock(turnoverRateStockList);
//        filterStockInfo.setCapitalFlowStockList(capitalFlowStockList);
        filterStockInfo.setCjeRisePlateList(cjeRisePlateList);
        filterStockInfo.setFastChangePlateList(fastChangePlateList);
        filterStockInfo.setPressureLevelStockList(pressureLevelStockList);
        return filterStockInfo;
    }

    /**
     * 筛选上涨个股
     *
     * @param map
     * @return
     */
    private List<TurnoverRateStock> getTurnoverRateStockList(HashMap<String, List> map) {
        List<TurnoverRateStock> turnoverRateStockList = new ArrayList<>();
        List<CapitalFlowStock> capitalFlowStockList = new ArrayList<>();
        for (Map.Entry<String, List> stock : map.entrySet()) {
            String key = stock.getKey();
            List<StockDetailsInfo> stockList = stock.getValue();
            double openingDatesize = stockList.size();
            // 按时间倒序排序
            stockList.sort(Comparator.comparing(StockDetailsInfo::getOpeningDate).reversed());
            List<Double> quoteChangeList = new ArrayList<>();
            // 换手率
            double turnoverRate = 0;
            // 资金净流入天数
            double inputDay = 0;
            // 涨跌幅
            double quoteChange = 0;
            for (StockDetailsInfo detailsInfo : stockList) {
                if (detailsInfo.getTurnoverRate().contains("%")) {
                    detailsInfo.setTurnoverRate(detailsInfo.getTurnoverRate().replace("%", ""));
                }
                // 将换手率为空的改为字符串0
                if ("".equals(detailsInfo.getTurnoverRate())) {
                    detailsInfo.setTurnoverRate("0");
                }
                // 统计换手率
                turnoverRate = turnoverRate + Double.parseDouble(detailsInfo.getTurnoverRate());
                // 判断资金流入，InflowFunds1资金净流入字段不为空并且不为负数 -
                if (!StringUtils.isEmpty(detailsInfo.getInflowFunds1()) && !detailsInfo.getInflowFunds1().startsWith("-")) {
                    inputDay++;
                }
                // 统计涨跌幅
                quoteChange = quoteChange + Double.parseDouble(detailsInfo.getQuoteChange());
                // 将涨幅大于0的放入一个集合中
                if (Double.parseDouble(detailsInfo.getQuoteChange()) > 0) {
                    quoteChangeList.add(Double.parseDouble(detailsInfo.getQuoteChange()));
                }
            }
            double endDateTurnoverRate = Double.parseDouble(stockList.get(0).getTurnoverRate());
//            double yesterdayTurnoverRate = Double.parseDouble(stockList.get(1).getTurnoverRate());
            // 除最后一天的平均换手率
            double avgTurnoverRate = (turnoverRate - endDateTurnoverRate) / (openingDatesize - 1);

            double turnoverRate1 = 2;
            // 连续上涨的
            // 格式化小数
            DecimalFormat df = new DecimalFormat("0.00");
            String format = df.format((float) quoteChangeList.size() / openingDatesize);
            // 换手率大于xx倍的 与 判断资金流入天数大于4/5的 以及 上涨天数大于4/5的
            if (endDateTurnoverRate / avgTurnoverRate > turnoverRate1 && inputDay / openingDatesize > 0.79 && Double.parseDouble(format) > 0.79) {
                TurnoverRateStock turnoverRateStock = new TurnoverRateStock();
                turnoverRateStock.setStockCode(key);
                turnoverRateStock.setStockName(STOCK_BASE_INFO_MAP.get(key));
                turnoverRateStockList.add(turnoverRateStock);
            }
            // 判断资金流入天数大于4/5的
            if (inputDay / openingDatesize > 0.59) {
                CapitalFlowStock capitalFlowStock = new CapitalFlowStock();
                capitalFlowStock.setStockCode(key);
                capitalFlowStock.setStockName(STOCK_BASE_INFO_MAP.get(key));
                capitalFlowStockList.add(capitalFlowStock);
            }
        }
        return turnoverRateStockList;
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
