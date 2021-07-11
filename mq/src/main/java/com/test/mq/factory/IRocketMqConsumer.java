package com.test.mq.factory;

/**
 * @author by Lixq
 * @Classname IRocketMqConsumer
 * @Description TODO
 * @Date 2021/5/20 22:07
 */
public interface IRocketMqConsumer {

    /**
     * 消息消费
     * @param topic
     * @param tag
     * @param msgId
     * @param message
     */
    void onMessage(String topic,String tag,String msgId,String message);

    /**
     * 根据topic和tag区分业务逻辑
     * @return
     */
    String getTopicTag();
}
