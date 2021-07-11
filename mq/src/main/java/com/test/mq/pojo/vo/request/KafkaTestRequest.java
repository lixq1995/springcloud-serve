package com.test.mq.pojo.vo.request;

import lombok.Data;

/**
 * @author by Lixq
 * @Classname KafkaTestRequest
 * @Description TODO
 * @Date 2021/5/16 21:06
 */
@Data
public class KafkaTestRequest {
    private String topic;

    private String message;
}
