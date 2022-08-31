package com.test.hello.pojo.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.test.hello.pojo.vo.validatedgroup.Save;
import com.test.hello.pojo.vo.validatedgroup.Update;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author by Lixq
 * @Classname User
 * @Description TODO
 * @Date 2021/4/7 20:10
 */
@Data
public class Student {

    @NotNull(message = "不能为空",groups ={Update.class})
    private Integer id;

//    @NotBlank(message = "不能为空",groups ={Save.class})
//    @NotBlank(message = "不能为空",groups ={Update.class})
    private String name;
    private int age;
    private String gender;
    private String grade;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private String keys;

}
