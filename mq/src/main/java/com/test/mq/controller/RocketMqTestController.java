package com.test.mq.controller;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author by Lixq
 * @Classname RocketMqTestController
 * @Description TODO
 * @Date 2021/5/20 23:19
 */
@Slf4j
@RestController
@RequestMapping("/rocketMq")
public class RocketMqTestController {

    /**
     * 使用RocketMq的生产者
     */
    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @GetMapping("/sendTest")
    @ApiOperation("发送kafka消息")
    public String send() throws Exception {
        String msg = "demo msg test123";
        log.info("开始发送消息：" + msg);
        Message sendMsg = new Message("topic1", "tag1", msg.getBytes());
        //默认3秒超时
        SendResult sendResult = defaultMQProducer.send(sendMsg);
        log.info("消息发送响应信息：" + sendResult.toString());
        return "success";
    }
}
