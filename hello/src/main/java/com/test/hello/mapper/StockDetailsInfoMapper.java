package com.test.hello.mapper;

import com.test.hello.pojo.dao.StockBaseInfo;
import com.test.hello.pojo.dao.StockDetailsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * StockDetailsInfoMapper继承基类
 */
@Mapper
@Repository
public interface StockDetailsInfoMapper extends MyBatisBaseDao<StockDetailsInfo, Long> {

    /**
     * 批量新增
     * @param stockDetailsInfoList
     */
    void batchSave(@Param(value = "stockDetailsInfoList") List<StockDetailsInfo> stockDetailsInfoList);

    /**
     * 获取股票数据
     * @param stockDetailsInfoList
     * @return
     */
    StockDetailsInfo getStockDetailsInfo(StockDetailsInfo stockDetailsInfoList);
}