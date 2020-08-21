package com.tinygame.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

public interface ICmdHandler<TCmd extends GeneratedMessageV3> {

    /**
     * 处理指令
     * */

    void handler(ChannelHandlerContext ctx,TCmd msg);
}
