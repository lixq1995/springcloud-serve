package com.test.hello.service.impl;

import com.test.hello.mapper.MybatisMapper;
import com.test.hello.pojo.dao.Student;
import com.test.hello.pojo.vo.request.QueryStudent;
import com.test.hello.service.IHelloService;
import com.test.hello.service.IMybatisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author by Lixq
 * @Classname MybatisServiceImpl
 * @Description TODO
 * @Date 2021/4/7 20:14
 */
@Service
public class MybatisServiceImpl implements IMybatisService {

    @Autowired
    private MybatisMapper mybatisMapper;

    @Autowired
    private IHelloService iHelloService;

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.NESTED,isolation = Isolation.READ_UNCOMMITTED)
    public List<Student> getStudents(QueryStudent queryStudent) {
        List<Student> students = mybatisMapper.getStudents(queryStudent);
        return students;
    }

    public Student getStudentById(Long id) {
        Student student = mybatisMapper.getStudentById(id);
        return student;
    }

    @Transactional(rollbackFor = Exception.class/*,propagation = Propagation.NESTED*//*,isolation = Isolation.READ_UNCOMMITTED*/)
    public void save(Student student) {
        mybatisMapper.insert(student);
        String name = student.getName();
//        String response = iHelloService.getHi(name);
        QueryStudent queryStudent = new QueryStudent();
        queryStudent.setKeyword(student.getName());
        List<Student> students = mybatisMapper.getStudents(queryStudent);
        System.out.println(students);
    }

    public void update(Student student) {
        mybatisMapper.update(student);

    }

    public void delete(Long id) {
        mybatisMapper.delete(id);
    }

    public void batchSave(List<Student> studentList) {
        mybatisMapper.batchSave(studentList);
    }

    public void batchUpdate(List<Student> studentList) {
        mybatisMapper.batchUpdate(studentList);
    }
}
