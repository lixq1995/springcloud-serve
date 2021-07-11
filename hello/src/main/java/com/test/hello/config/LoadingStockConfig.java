package com.test.hello.config;

import com.test.hello.mapper.StockInfoMapper;
import com.test.hello.pojo.dao.StockBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by Lixq
 * @Classname LoadingStockConfig
 * @Description TODO
 * @Date 2021/6/27 14:36
 */
@Configuration
public class LoadingStockConfig {

    @Autowired
    private StockInfoMapper stockInfoMapper;

    public static Map<String,String> STOCK_BASE_INFO_MAP = new HashMap<>(6400);

    @Bean
    public void loadingStockBaseInfo() {
        StockBaseInfo queryCondition = new StockBaseInfo();
        List<StockBaseInfo> stockBaseInfoList = stockInfoMapper.getStockBaseInfoList(queryCondition);
        stockBaseInfoList.stream().forEach(stockBaseInfo -> {
            String code = stockBaseInfo.getCode().substring(2, 8);
            STOCK_BASE_INFO_MAP.put(code,stockBaseInfo.getName());
        });
    }
}
