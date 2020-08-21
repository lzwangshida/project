package com.tinygame.cmdhandler;

import com.tinygame.model.MoveState;
import com.tinygame.model.User;
import com.tinygame.model.UserManager;
import com.tinygame.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;



public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd>{

    @Override
    public void handler(ChannelHandlerContext ctx,GameMsgProtocol.WhoElseIsHereCmd msg) {
        if (null == ctx
                || null == msg) {
            return;
        }


        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder=
                GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        for(User user: UserManager.listUser()){
            if (null==user){
                continue;
            }

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder resultUserInfo=
                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            resultUserInfo.setUserId(user.getUserId());
            resultUserInfo.setHeroAvatar(user.getHeroAvatar());
            resultUserInfo.setUserName(user.getUserName());

            /**
             * 构建用户移动状态
             * */
            MoveState moveState = user.getMoveState();
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder moveStateBuilder =
                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
                moveStateBuilder.setFromPosX(moveState.getFromPosX());
                moveStateBuilder.setFromPosY(moveState.getFromPosY());
                moveStateBuilder.setToPosX(moveState.getToPosX());
                moveStateBuilder.setToPosY(moveState.getToPosY());
                moveStateBuilder.setStartTime(moveState.getStartTime());
                /**
                 * 将移动状态设置给用户信息
                * */
            resultUserInfo.setMoveState(moveStateBuilder);

            resultBuilder.addUserInfo(resultUserInfo);
        }
        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
