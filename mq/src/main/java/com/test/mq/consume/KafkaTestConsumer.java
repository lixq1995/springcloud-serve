package com.test.mq.consume;

import com.alibaba.fastjson.JSONObject;
import com.test.mq.pojo.vo.request.KafkaTestRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author by Lixq
 * @Classname KafkaTestConsume
 * @Description TODO
 * @Date 2021/5/16 20:44
 */
@Service
@Slf4j
public class KafkaTestConsumer {

    @KafkaListener(topics = {"${messenger.kafka.topicName}"})
    public void monitorInfo(String message) {
        KafkaTestRequest requestMsg = (KafkaTestRequest) JSONObject.parseObject(message, KafkaTestRequest.class);
        System.out.println(requestMsg.getTopic() + "        " + requestMsg.getMessage());
        log.info("requestMsg topic is :{}, message is :{} ", requestMsg.getTopic(),requestMsg.getMessage());
    }
}
