package com.tinygame.server;

import com.google.protobuf.GeneratedMessageV3;
import com.tinygame.cmdhandler.CmdHandlerFactory;
import com.tinygame.cmdhandler.ICmdHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MainThreadProcessor {

    static  private  final Logger LOGGER = LoggerFactory.getLogger(MainThreadProcessor.class);


    /**
     *单列
     * */
    private static final MainThreadProcessor instance = new MainThreadProcessor();

    /**
    * 创建一个单线程
    * */
    private final ExecutorService _es = Executors.newSingleThreadExecutor((newRunnable)->{
            Thread newThread  = new Thread(newRunnable);
            newThread.setName("MainThreadProcessor");
            return newThread;
    });

    /**
     * 私有化
     * */
    private MainThreadProcessor(){}

    /**
     * 返回单列
     * */
    public static MainThreadProcessor getInstance(){
        return instance;
    }

    /**
     * 处理客户端消息
     * */
    public void processor(ChannelHandlerContext ctx , GeneratedMessageV3 msg){

        if (null == ctx || null == msg){
            return;
        }

        Class<? extends GeneratedMessageV3> aClass = msg.getClass();


        _es.submit(()->{

            ICmdHandler<? extends GeneratedMessageV3> iCmdHandler = CmdHandlerFactory.create(aClass);

            if (null==iCmdHandler){
                LOGGER.error(
                        "未找到指定的消息处理器，CmdHandler={}",
                        aClass.getName()
                );
                return;
            }

            try{

                iCmdHandler.handler(ctx,cast(msg));
            }catch (Exception e){
                LOGGER.error(e.getMessage(),e);
            }
    });


    }

    /**
     * 处理消息对象
     * */
    public void processor(Runnable r){
        if (null==r){
            return;
        }
        _es.submit(r);
    }

    /**
     * 消息转换器
     * */
    private  <TCmd extends GeneratedMessageV3> TCmd cast(GeneratedMessageV3 msg){
        if (null==msg || !(msg instanceof  GeneratedMessageV3)){
            return null;
        }

        return (TCmd)msg;

    }

}
