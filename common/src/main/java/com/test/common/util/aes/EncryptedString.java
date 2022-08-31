package com.test.common.util.aes;


import cn.hutool.core.util.RandomUtil;
import lombok.Data;

@Data
public class  EncryptedString {

    public static  String key = "1234567890adbcde";//长度为16个字符

    public static  String iv  = "1234567890hjlkew";//长度为16个字符

    public static void main(String[] args) {
        // 生成随机16位
        System.out.println(RandomUtil.randomString(16));
        System.out.println(RandomUtil.randomString(16));
    }
}
