package com.tinygame.async.mq;

import com.alibaba.fastjson.JSONObject;
import com.tinygame.rank.RankService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class MQConsumer {
/***
 * 日志对象
 * */
private static final Logger LOGGER = LoggerFactory.getLogger(MQProducer.class);
       /**
        * * 私有化默认构造器
        * */
       private MQConsumer(){}

       /**
       * 初始化
        * * */
          public static void init(){
           //创建消息队列消费者
              DefaultMQPushConsumer _consumer = new DefaultMQPushConsumer("herostory");
             //設置namesrv地址
             _consumer.setNamesrvAddr("192.168.121.11:9876");
              try {
                  _consumer.subscribe("victer","*");
                  _consumer.registerMessageListener(new MessageListenerConcurrently() {
                      @Override
                      public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                          for(MessageExt messageExt : list){
                              VictorMsg mqMsg = JSONObject.parseObject(messageExt.getBody(), VictorMsg.class);
                              LOGGER.info("从消息中获得战斗结果，winner={}，loser={}",mqMsg.getWinnerId(),mqMsg.getLoserId());
                              RankService.getInstance().refreshRank(mqMsg.getWinnerId(),mqMsg.getLoserId());
                          }

                          return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                      }
                  });

                  _consumer.start();
              } catch (MQClientException e) {
                  LOGGER.error(e.getMessage(),e);
              }


          }


}
