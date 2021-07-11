package com.test.mq.controller;

import com.alibaba.fastjson.JSON;
import com.test.mq.pojo.vo.request.KafkaTestRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author by Lixq
 * @Classname KafkaTestController
 * @Description http://www.mydlq.club/article/34/
 * @Date 2021/5/16 20:43
 */
@RestController
@RequestMapping("/kafka")
public class KafkaTestController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/sendTest")
    @ApiOperation("发送kafka消息")
    public String sendKafkaMsg(@RequestBody KafkaTestRequest kafkaTestRequest) {
//        KafkaTestRequest kafkaTestRequest = new KafkaTestRequest();
//        kafkaTestRequest.setTopic("topicTest");
//        kafkaTestRequest.setMessage("hello8");
        kafkaTemplate.send("topicTest123", JSON.toJSONString(kafkaTestRequest));
        return "success";
    }
}
