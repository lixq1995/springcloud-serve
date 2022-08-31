package com.test.hello.controller;

import com.test.common.util.excel.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by Lixq
 * @Classname PoiTestController
 * @Description TODO
 * @Date 2021/5/16 15:09
 */
@RestController
@RequestMapping("/poi")
@Api(tags = "poi测试控制层")
public class PoiTestController {


    @PostMapping(value= "/import")
    @ApiOperation("导入")
    public void importExcel(MultipartFile file) {
        try {
            List<List<Object>> importFile = ExcelUtil.importFile(file);
            System.out.println(importFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @GetMapping(value= "/export")
    @ApiOperation("导出")
    public String exportExcel(HttpServletResponse response) throws IOException {
        String[] headers = new String[] {"姓名","年龄","级别"};
        List<List<Object>> dataList = new ArrayList<>();
        for(int x = 0 ; x < 3 ; x++) {
            List<Object> data = new ArrayList<Object>();
            data.add("姓名ssssss"+x);
            data.add(18+x);
            data.add("级别"+x);
            dataList.add(data);
        }
        try {
            ExcelUtil.export("用户数据", headers, dataList,"poi导出模板.xlsx" ,response);
            return null ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "导出失败 --";
    }

}
