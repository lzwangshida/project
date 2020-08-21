package com.tinygame.async.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MQProducer {

    /**
     * 日志对象
     * */
    private static final Logger LOGGER = LoggerFactory.getLogger(MQProducer.class);

    /**
     *生产者
     * */
    private static DefaultMQProducer _producer = null;

    public static void init(){
        try {

            //创建生产者
            DefaultMQProducer producer = new DefaultMQProducer("herostory");
            //制定nameServer地址
            producer.setNamesrvAddr("192.168.121.11:9876");
            //启动生产者
            producer.start();
            producer.setRetryTimesWhenSendAsyncFailed(3);
            _producer = producer;
        } catch (MQClientException e) {
            LOGGER.error(e.getMessage(),e);
        }
    }

    /**
     * 发送消息到mq
     * */
    public static void sendMsg(String topic , VictorMsg msg){
        if (null == topic || null == msg){
            return;
        }
        if (null  == _producer){
            throw new RuntimeException("_producer尚未初始化");
        }

        Message message = new Message();
        message.setTopic(topic);
        message.setBody(JSONObject.toJSONBytes(msg));

        try {
            _producer.send(message);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
        }
    }
}
