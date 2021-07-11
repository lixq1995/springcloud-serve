package com.test.hello.mapper;


import com.test.hello.pojo.dao.Student;
import com.test.hello.pojo.vo.request.QueryStudent;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author by Lixq
 * @Classname MybatisMapper
 * @Description TODO
 * @Date 2021/4/7 19:38
 */
@Mapper
public interface MybatisMapper {

    List<Student> getStudents(QueryStudent queryStudent);

    Student getStudentById(Long id);

    void insert(Student student);

    void update(Student student);

    void delete(Long id);

    void batchSave(@Param(value = "studentList") List<Student> studentList);

    void batchUpdate(@Param(value = "studentList") List<Student> studentList);
}
