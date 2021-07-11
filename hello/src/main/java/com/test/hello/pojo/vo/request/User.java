package com.test.hello.pojo.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author by Lixq
 * @Classname User
 * @Description TODO
 * @Date 2021/4/7 20:10
 */
@Data
public class User {

    @NotNull(message = "不能为空")
    private Integer id;

    @NotBlank(message = "不能为空")
    private String name;

}
