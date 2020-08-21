package com.tinygame.server;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.tinygame.msg.GameMsgProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* @author wnag
*将websocket二进制消息解码
* */

public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof BinaryWebSocketFrame)){
            return;
        }
        //websocket 二进制消息会通过httpServerCodec 解码成BinaryWebSocketFrame对象
        BinaryWebSocketFrame frame =(BinaryWebSocketFrame)msg;
        ByteBuf byteBuf = frame.content();
        //读取消息长度
        byteBuf.readShort();
        //读取消息编号
       int msgCode= byteBuf.readShort();
        Message.Builder buildByMsgCode = GameMsgRecognizer.getBuildByMsgCode(msgCode);
        if (buildByMsgCode==null){
            LOGGER.error("无法识别的消息,buildmsgCode={}",msgCode);
            return;
        }
        //读取消息体
        byte[] msgBody = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(msgBody);


        buildByMsgCode.clear();
        buildByMsgCode.mergeFrom(msgBody);
        Message newBuild = buildByMsgCode.build();


        if (null != newBuild){
            ctx.fireChannelRead(newBuild);
        }

    }
}
