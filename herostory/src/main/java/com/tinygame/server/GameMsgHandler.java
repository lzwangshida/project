package com.tinygame.server;

import com.google.protobuf.GeneratedMessageV3;
import com.tinygame.cmdhandler.*;
import com.tinygame.model.UserManager;
import com.tinygame.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wang
 * 游戏消息处理器  自定义
 * */

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);




    /**
     * 有新连接进来就加入到group中
    * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Broadcaster.addChannel(ctx.channel());
    }


    /**
     * 客户端离开或刷新时
     * */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        Broadcaster.removeChannel(ctx.channel());
        Integer id =(Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null==id){
            return;
        }
        UserManager.removeUserById(id);
        GameMsgProtocol.UserQuitResult.Builder resultBuilder =
                GameMsgProtocol.UserQuitResult.newBuilder();
        resultBuilder.setQuitUserId(id);
        GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();
        Broadcaster.broadCast(newResult);


    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof GeneratedMessageV3) {

            //通过主线程处理消息
            MainThreadProcessor.getInstance().processor(ctx, (GeneratedMessageV3) msg);
        }
    }


}

