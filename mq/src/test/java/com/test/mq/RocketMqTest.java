package com.test.mq;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author by Lixq
 * @Classname RocketMqTest
 * @Description TODO
 * @Date 2021/5/16 18:34
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RocketMqTest {

    /**
     * 使用RocketMq的生产者
     */
    @Autowired
    private DefaultMQProducer defaultMQProducer;

    /**
     * 发送消息
     *
     * @throws InterruptedException
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws MQClientException
     */
    @Test
    public void send() throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        String msg = "demo msg test123";
        log.info("开始发送消息：" + msg);
        Message sendMsg = new Message("topic1", "tag1", msg.getBytes());
        //默认3秒超时
        SendResult sendResult = defaultMQProducer.send(sendMsg);
        log.info("消息发送响应信息：" + sendResult.toString());
    }
}
