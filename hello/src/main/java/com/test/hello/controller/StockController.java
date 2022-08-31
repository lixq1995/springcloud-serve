package com.test.hello.controller;

import com.test.hello.pojo.dao.StockDetailsInfo;
import com.test.hello.pojo.vo.request.FilterStockInfoVo;
import com.test.hello.pojo.vo.request.ManualGetStockInfoVo;
import com.test.hello.pojo.vo.request.stock.FilterStockInfoByFundVo;
import com.test.hello.pojo.vo.response.FilterStockInfo;
import com.test.hello.service.IStockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author by Lixq
 * @Classname StockController
 * @Description TODO
 * @Date 2021/6/22 20:12
 */
@RestController
@RequestMapping("/stock")
@Api(tags = "股票数据controller")
public class StockController {

    @Autowired
    private IStockService stockService;

    @GetMapping("/test")
    @ApiOperation("获取hello数据")
    public String getAllHrs() {
        return "hello";
    }

    @GetMapping("/getBaseInfo")
    @ApiOperation("获取股票基础数据名字与代码")
    public String getBaseInfo() {
        String baseInfo = stockService.getBaseInfo();
        return baseInfo;
    }

    @GetMapping("/timedTaskGetStockInfo")
    @ApiOperation("定时任务获取股票数据")
    public String timedTaskGetStockInfo() {
        String baseInfo = stockService.timedTaskGetStockInfo();
        return baseInfo;
    }

//    @GetMapping("/timedTaskGetStockLiquidity")
//    @ApiOperation("定时任务获取股票资金流动----作废，接口返回数据与实际同花顺对不上")
//    public String timedTaskGetStockLiquidity() {
//        String baseInfo = stockService.timedTaskGetStockLiquidity();
//        return baseInfo;
//    }

    @GetMapping("/timedTaskGetStockLiquidity1")
    @ApiOperation("定时任务获取股票资金流动----歪枣")
    public String timedTaskGetStockLiquidity1() {
        String baseInfo = stockService.timedTaskGetStockLiquidity1();
        return baseInfo;
    }

    @GetMapping("/timedTaskGetStockPlate")
    @ApiOperation("定时任务获取股票板块信息----歪枣")
    public String timedTaskGetStockPlate() {
        String baseInfo = stockService.timedTaskGetStockPlate();
        return baseInfo;
    }


    @PostMapping("/manualGetStockInfo")
    @ApiOperation("手动获取股票数据")
    public String manualGetStockInfo(@RequestBody ManualGetStockInfoVo manualGetStockInfoVo) {
        String baseInfo = stockService.manualGetStockInfo(manualGetStockInfoVo);
        return baseInfo;
    }

    @PostMapping("/filterStockInfo")
    @ApiOperation("根据条件筛选股票")
    public FilterStockInfo filterStockInfo(@RequestBody @Validated FilterStockInfoVo filterStockInfoVo) {
        FilterStockInfo filterStockInfo = stockService.filterStockInfo(filterStockInfoVo);
        return filterStockInfo;
    }

    @PostMapping("/filterStockInfoByFund")
    @ApiOperation("短线根据资金流向与换手率筛选")
    public FilterStockInfo filterStockInfoByFund(@RequestBody @Validated FilterStockInfoByFundVo filterStockInfoByFundVo) {
        FilterStockInfo filterStockInfo = stockService.filterStockInfoByFund(filterStockInfoByFundVo);
        return filterStockInfo;
    }

    @PostMapping("/testAsync")
    @ApiOperation("测试多线程")
    public String testAsync() {
        CompletableFuture<String> async1 = stockService.async(10000,"async1..............");
        CompletableFuture<String> async2 = stockService.async(3000,"async2..............");

        CompletableFuture<List> uCompletableFuture = async1.thenApply(a -> {
            ArrayList<String> list = new ArrayList<>();
            list.add(a);
            list.add("b");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(list);
            return list;

        });
//        CompletableFuture.allOf(async1,async2);
//        System.out.println(async1.join() + "  " + async2.join() + "  " + uCompletableFuture.join());

        return "success";
    }
}
