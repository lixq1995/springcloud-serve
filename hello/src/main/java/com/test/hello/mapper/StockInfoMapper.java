package com.test.hello.mapper;

import com.test.hello.pojo.dao.StockBaseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author by Lixq
 * @Classname StockInfoMapper
 * @Description TODO
 * @Date 2021/6/22 20:54
 */
@Mapper
public interface StockInfoMapper {

    /**
     * 批量新增
     * @param stockInfoList
     */
    void batchSave(@Param(value = "stockInfoList") List<StockBaseInfo> stockInfoList);

    /**
     * 查询StockBaseInfoList
     * @param stockBaseInfo
     * @return
     */
    List<StockBaseInfo> getStockBaseInfoList(StockBaseInfo stockBaseInfo);
}
