package com.test.mq.consume;

import com.test.mq.factory.IRocketMqConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author by Lixq
 * @Classname RocketMqTestConsumer
 * @Description TODO
 * @Date 2021/5/20 22:36
 */
@Slf4j
@Service
public class RocketMqTestConsumer implements IRocketMqConsumer {
    @Override
    public void onMessage(String topic, String tag, String msgId, String message) {
        log.info("rocket mq 开始监听");
        log.info(" topic : {} , tag : {} , msgId : {} , message : {}",topic, tag ,msgId ,message);
        System.out.println("message is : " + message);
    }

    @Override
    public String getTopicTag() {
        return "topic1" + "tag1";
    }
}
