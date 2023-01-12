package com.test.hey.controller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test {

    public static void main(String[] args) {
        int count = 10;
        for (int i = 0; i < 12; i++) {
            try {
                if (i == count) {
                    int a = 10/0;
                }
                System.out.println(i);
            } catch (Exception e) {
                log.error("exception is : {}",e);
            } finally {
                log.info("最终进入 {}",i);
            }

        }
    }
}
