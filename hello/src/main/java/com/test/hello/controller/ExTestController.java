package com.test.hello.controller;

import com.test.common.enums.HelloEnum;
import com.test.common.exception.BusinessException;
import com.test.common.result.ResultBean;
import com.test.hello.pojo.vo.request.TestValidation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author by Lixq
 * @Classname ExTestController
 * @Description TODO
 * @Date 2021/4/3 22:47
 */
@Slf4j
@RestController
@RequestMapping("/exception")
@Api(tags = "全局异常测试控制层")
public class ExTestController {

    @GetMapping("/business")
    @ApiOperation("测试全局异常BusinessException")
    public ResultBean business(int num) {
        if (num < 3) {
            throw new BusinessException(HelloEnum.EXCEPTION_ONE);
        }
        return ResultBean.success();
    }

    @PostMapping("/validation")
    @ApiOperation("测试全局异常validation")
    public ResultBean validation(@Valid @RequestBody TestValidation testValidation) {
        log.info("成功");
        return ResultBean.success();
    }


    @PostMapping("/validationBranch")
    @ApiOperation("测试全局异常validation分支异常")
    public ResultBean validationBranch(@Valid @RequestBody TestValidation testValidation, BindingResult bindingResult) {
        // 如果为1暂存，就算其他字段Valid效验异常，也能继续执行暂存
        if (bindingResult.hasErrors() && !"1".equals(testValidation.getFlag())) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            String defaultMessage = allErrors.get(0).getDefaultMessage();
            throw new BusinessException(defaultMessage);
        }
        System.out.println(testValidation);
        return ResultBean.success();
    }

    @GetMapping("/validationModelAttribute")
    @ApiOperation("测试全局异常validationModelAttribute注解异常")
    public ResultBean validationModelAttribute(@ModelAttribute @Validated TestValidation testValidation) {
        System.out.println(testValidation);
        return ResultBean.success();
    }
}
