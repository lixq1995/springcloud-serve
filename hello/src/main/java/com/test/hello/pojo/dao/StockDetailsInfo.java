package com.test.hello.pojo.dao;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.test.common.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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
     * 开市时间 —— 歪枣返回时间
     */
    @JSONField(name = "tdate")
    private Date openingDate;

    /**
     * 成交额（元）
     */
    @JSONField(name = "cje")
    private String transaction;

    /**
     * 流入资金（元）
     */
    @JSONField(name = "lrzj")
    private String inflowFunds;

    /**
     * 流出资金（元）
     */
    @JSONField(name = "lczj")
    private String outflowFunds;

    /**
     * 净流入（元）
     */
    @JSONField(name = "jlr")
    private String netInflow;

    /**
     * 净流入率（%）
     */
    @JSONField(name = "jlrl")
    private String netInflowRate;

    /**
     * 每日股票资金流动返回股票名字
     */
    @JSONField(name = "dm")
    private String stockCodeUpdate;

    /**
     * 每日股票资金流动返回开市时间
     */
//    仅对入参，出参有效
//    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JSONField(name = "t")
    private Date openingDateUpdate/* = DateUtils.parseDate("2021-07-07")*/;

    /**
     * 歪枣股票code
     */
    @JSONField(name = "code")
    private String waizaoStockCode;

    /**
     * 歪枣流入资金（元）
     */
    @JSONField(name = "zljlr")
    private String inflowFunds1;


    private static final long serialVersionUID = 1L;

    public Date getOpeningDateUpdate() {
        return openingDateUpdate;
    }

    public void setOpeningDateUpdate(Date openingDateUpdate) {
        // 去掉时分秒
        String format = DateUtils.format(openingDateUpdate, "yyyy-MM-dd HH:mm:ss");
        this.openingDateUpdate = DateUtils.parseDate(format);
    }

    public String getStockCodeUpdate() {
        return stockCodeUpdate;
    }

    public void setStockCodeUpdate(String stockCodeUpdate) {
        // todo 去掉sz,sh字母，优化直接删掉所有字母
        String substring = stockCodeUpdate.substring(2, stockCodeUpdate.length());
        this.stockCodeUpdate = substring;
    }
}