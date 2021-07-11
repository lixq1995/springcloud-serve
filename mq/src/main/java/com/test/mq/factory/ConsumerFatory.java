package com.test.mq.factory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author by Lixq
 * @Classname ConsumerFatory
 * @Description TODO
 * @Date 2021/5/20 22:22
 */
@Component
public class ConsumerFatory implements ApplicationContextAware {

    private static Map<String,IRocketMqConsumer> rocketMap = new HashMap<String, IRocketMqConsumer>();


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, IRocketMqConsumer> rocketMq = applicationContext.getBeansOfType(IRocketMqConsumer.class);
        if (Objects.nonNull(rocketMq)) {
            rocketMq.forEach((k,v)->rocketMap.put(v.getTopicTag(),v));
        }

    }


    public static <T extends IRocketMqConsumer> T getRocketConsumer(String topicTag) {
        return (T)rocketMap.get(topicTag);
    }
}
