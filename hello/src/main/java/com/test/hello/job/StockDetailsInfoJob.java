//package com.test.hello.job;
//
//import com.test.hello.service.IStockService;
//import com.xxl.job.core.handler.annotation.XxlJob;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * @author by Lixq
// * @Classname GetStockDetailsInfoJob
// * @Description TODO
// * @Date 2021/6/26 0:29
// */
//@Component
//@Slf4j
//public class StockDetailsInfoJob {
//
//    @Autowired
//    private IStockService stockService;
//
//    // todo elastic-job使用
//
//    /**
//     * 同步股票详情定时任务
//     * @throws Exception
//     */
//    @XxlJob("stockJobHandler")
//    public void stockJobHandler() throws Exception {
//        log.info("执行同步股票详情定时任务");
//        System.out.println("执行同步股票详情定时任务");
////        stockService.timedTaskGetStockInfo();
//    }
//}
