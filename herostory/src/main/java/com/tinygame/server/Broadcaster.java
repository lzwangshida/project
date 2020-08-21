package com.tinygame.server;


import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Broadcaster {

    static private final DefaultChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private Broadcaster(){}

    /**
    * 添加信道
    * */
     public static void addChannel(Channel channel){
         if (null!=channel)
             _channelGroup.add(channel);
     }

     /**
     * 移除信道
     * */
     public static void removeChannel(Channel channel){
        _channelGroup.remove(channel);
     }
     /**
     * 广播消息
     * */
     public static void broadCast(Object msg){
        if (null!=msg)
            _channelGroup.flushAndWrite(msg);
     }
}
