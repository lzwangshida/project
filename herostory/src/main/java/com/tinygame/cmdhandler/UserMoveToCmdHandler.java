package com.tinygame.cmdhandler;

import com.tinygame.model.MoveState;
import com.tinygame.model.User;
import com.tinygame.model.UserManager;
import com.tinygame.msg.GameMsgProtocol;
import com.tinygame.server.Broadcaster;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {

    static private final Logger LOGGER = LoggerFactory.getLogger(UserMoveToCmdHandler.class);

    @Override
    public void handler(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        if (null==ctx || null == cmd){
            return;
        }


        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == userId){
            return;
        }

        /**
         * 获取移动用户
        * */
        User user = UserManager.getUserById(userId);
        if (null == user){
            LOGGER.error("未找到用户,userId={}",userId);
            return;
        }
        /**
        * 设置用户移动状态
        * */
        MoveState moveState = user.getMoveState();

        moveState.fromPosX=cmd.getMoveFromPosX();
        moveState.fromPosY=cmd.getMoveFromPosY();
        moveState.toPosX=cmd.getMoveToPosX();
        moveState.toPosY=cmd.getMoveToPosY();
        moveState.startTime=System.currentTimeMillis();


        GameMsgProtocol.UserMoveToResult.Builder resultBuilder =
                GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveFromPosX(moveState.getFromPosX());
        resultBuilder.setMoveFromPosY(moveState.getFromPosY());
        resultBuilder.setMoveToPosX(moveState.getToPosX());
        resultBuilder.setMoveToPosY(moveState.getToPosY());
        resultBuilder.setMoveStartTime(moveState.getStartTime());

        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();

        Broadcaster.broadCast(newResult);
    }
}
