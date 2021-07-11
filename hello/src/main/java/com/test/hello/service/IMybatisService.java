package com.test.hello.service;

import com.test.hello.pojo.dao.Student;
import com.test.hello.pojo.vo.request.QueryStudent;

import java.util.List;

/**
 * @author by Lixq
 * @Classname IMybatisService
 * @Description TODO
 * @Date 2021/4/7 20:13
 */
public interface IMybatisService {

    List<Student> getStudents(QueryStudent queryStudent);

    Student getStudentById(Long id);

    void save(Student student);

    void update(Student student);

    void delete(Long id);

    void batchSave(List<Student> studentList);

    void batchUpdate(List<Student> studentList);
}
