package com.tinygame.cmdhandler;


import com.tinygame.async.mq.MQProducer;
import com.tinygame.async.mq.VictorMsg;
import com.tinygame.model.User;
import com.tinygame.model.UserManager;
import com.tinygame.msg.GameMsgProtocol;
import com.tinygame.server.Broadcaster;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    static private  final Logger LOGGER = LoggerFactory.getLogger(UserAttkCmdHandler.class);


    @Override
    public void handler(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {

        if (null == ctx || null ==cmd){
            return;
        }

        Integer attkUserId =  (Integer)ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == attkUserId){
            return;
        }

        Integer targetUserId = cmd.getTargetUserId();


        /**
        * 构建攻击消息
        * */
        GameMsgProtocol.UserAttkResult.Builder attkBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        attkBuilder.setAttkUserId(attkUserId);
        attkBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult newAttkBuilder = attkBuilder.build();
        Broadcaster.broadCast(newAttkBuilder);

        User targetUser = UserManager.getUserById(targetUserId);

        LOGGER.info("当前线程为=={}",Thread.currentThread().getName());

        if (null == targetUser){
            return;
        }

        Random random = new Random();
        int subtractHp=random.nextInt(10)+10;

        int currHp = targetUser.getCurrHp()-subtractHp;
        targetUser.setCurrHp(currHp);

        broadCasterUserSubractHp(targetUserId, subtractHp);


        if (targetUser.getCurrHp()<=0){

            broadCasterUserDie(targetUserId);

            VictorMsg victorMsg = new VictorMsg();
            victorMsg.setWinnerId(attkUserId);
            victorMsg.setLoserId(targetUserId);
            MQProducer.sendMsg("victer",victorMsg);

        }

    }

    private void broadCasterUserDie(Integer targetUserId) {
        GameMsgProtocol.UserDieResult.Builder userDieBuild = GameMsgProtocol.UserDieResult.newBuilder();
        userDieBuild.setTargetUserId(targetUserId);
        GameMsgProtocol.UserDieResult build = userDieBuild.build();
        Broadcaster.broadCast(build);
    }

    private void broadCasterUserSubractHp(Integer targetUserId, int subtractHp) {
        GameMsgProtocol.UserSubtractHpResult.Builder hpBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        hpBuilder.setTargetUserId(targetUserId);
        hpBuilder.setSubtractHp(subtractHp);
        GameMsgProtocol.UserSubtractHpResult hpBuild = hpBuilder.build();
        Broadcaster.broadCast(hpBuild);
    }


}

