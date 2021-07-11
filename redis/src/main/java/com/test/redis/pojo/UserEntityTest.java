package com.test.redis.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author by Lixq
 * @Classname UserEntityTest
 * @Description TODO
 * @Date 2021/5/17 22:38
 */
@Data
public class UserEntityTest implements Serializable {

    private static final long serialVersionUID = 5237730257103305078L;

    private Long id;
    private String userName;
    private String userSex;
}
