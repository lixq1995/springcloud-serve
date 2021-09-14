package com.test.hello.controller;

import com.github.pagehelper.PageHelper;
import com.test.common.result.CommonPage;
import com.test.common.result.ResultBean;
import com.test.hello.pojo.dao.Student;
import com.test.hello.pojo.vo.request.QueryStudent;
import com.test.hello.pojo.vo.validatedgroup.Save;
import com.test.hello.pojo.vo.validatedgroup.Update;
import com.test.hello.service.IMybatisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author by Lixq
 * @Classname MybatisController
 * @Description TODO
 * @Date 2021/4/7 19:35
 */
@RestController
@RequestMapping("/mybatis")
@Api(tags = "mybatis框架测试")
public class MybatisController {

    @Autowired
    private IMybatisService iMybatisService;

    @PostMapping("/getStudentList")
    @ApiOperation("查询,根据年级，名字筛选")
    public ResultBean<CommonPage<Student>> getStudentList(@RequestBody QueryStudent queryStudent) {
        PageHelper.startPage(queryStudent.getPageNum(),queryStudent.getPageSize());
        List<Student> students=iMybatisService.getStudents(queryStudent);
        return ResultBean.success(CommonPage.restPage(students));
    }

    @GetMapping("/getStudent")
    public Student getStudent(@RequestParam(value = "id")Long id) {
        Student student=iMybatisService.getStudentById(id);
        return student;
    }

    @PostMapping("/add")
    public ResultBean save(@RequestBody @Validated(Save.class) Student student) {
        iMybatisService.save(student);
        return ResultBean.success();
    }

    @PostMapping(value="update")
    public ResultBean update(@RequestBody @Validated(Update.class) Student student) {
        iMybatisService.update(student);
        return ResultBean.success();
    }

    @GetMapping(value="/delete/{id}")
    public ResultBean delete(@PathVariable(value = "id",required = true) Long id) {
        iMybatisService.delete(id);
        return ResultBean.success();
    }

    @PostMapping("/batchAdd")
    @ApiOperation("批量修改测试")
    public ResultBean batchSave(@RequestBody List<Student> studentList) {
        iMybatisService.batchSave(studentList);
        return ResultBean.success();
    }

    @PostMapping("/batchUpdate")
    @ApiOperation("批量新增测试")
    public ResultBean batchUpdate(@RequestBody List<Student> studentList) {
        iMybatisService.batchUpdate(studentList);
        return ResultBean.success();
    }
}
