package com.test.hello.pojo.dao;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDetailsInfo implements Serializable {
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 接口返回股票默认id（无用）
     */
    private String stockName;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 当前价格
     */
    private String currentPrice;

    /**
     * 昨天收盘价
     */
    private String yesterdayPrice;

    /**
     * 今日开盘价
     */
    private String openingPriceToday;

    /**
     * 成交量（手）
     */
    private String volume;

    /**
     * 内盘，即买入多少手
     */
    private String innerDisk;

    /**
     * 外盘，即卖出多少手
     */
    private String outerDisk;

    /**
     * 涨跌幅
     */
    private String quoteChange;

    /**
     * 今日最高价
     */
    private String highestPriceToday;

    /**
     * 今日最低价
     */
    private String lowestPriceToday;

    /**
     * 换手率
     */
    private String turnoverRate;

    /**
     * 市盈TTM
     */
    private String peRatioTtm;

    /**
     * 振动幅度
     */
    private String vibrationAmplitude;

    /**
     * 公司总市值
     */
    private String totalMarketCapitalization;

    /**
     * 市盈动
     */
    private String peRatioDynamic;

    /**
     * 市盈静
     */
    private String peRatioStatic;

    /**
     * 开市时间
     */
    private Date openingDate;

    private static final long serialVersionUID = 1L;
}