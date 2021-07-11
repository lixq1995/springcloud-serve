package com.test.mq.factory;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author by Lixq
 * @Classname DefaultRocketMqConsumer
 * @Description TODO
 * @Date 2021/5/20 22:11
 */
@Slf4j
@Component
public class DefaultRocketMqConsumer implements MessageListenerConcurrently {

    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        if(CollectionUtils.isEmpty(msgs)){
            log.info("接受到的消息为空，不处理，直接返回成功");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        MessageExt messageExt = msgs.get(0);
        String topic = msgs.get(0).getTopic();
        String msgId = msgs.get(0).getMsgId();
        String tag = msgs.get(0).getProperties().get("TAGS");
        String message = "";
        for (MessageExt msg: msgs) {
            message = new String(msg.getBody());
        }
        IRocketMqConsumer rocketConsumer = ConsumerFatory.getRocketConsumer(topic + tag);
        rocketConsumer.onMessage(topic,tag,msgId,message);
        int reconsume = msgs.get(0).getReconsumeTimes();
        // 消息已经重试了3次，还未成功。记录数据库，避免一直调用消费逻辑，占用内存空间
        if(reconsume == 3){
            // todo 将未成功消费的消息记录至数据库
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
