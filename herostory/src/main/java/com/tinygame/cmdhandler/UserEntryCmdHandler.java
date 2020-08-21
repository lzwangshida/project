package com.tinygame.cmdhandler;

import com.tinygame.model.User;
import com.tinygame.model.UserManager;
import com.tinygame.msg.GameMsgProtocol;
import com.tinygame.server.Broadcaster;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
        static  private  final Logger LOGGER = LoggerFactory.getLogger(UserEntryCmdHandler.class);

      @Override
     public void handler(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
          if (null==ctx||null==cmd){
              return;
          }

          Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
          if (null==userId){

              return;
          }

          User user = UserManager.getUserById(userId);
          if (null==user){
              LOGGER.error("用户不存在，userId={}",userId);
              return;
          }

          GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
          resultBuilder.setUserId(user.getUserId());
          resultBuilder.setUserName(user.getUserName());
          resultBuilder.setHeroAvatar(user.getHeroAvatar());


        ctx.channel().attr(AttributeKey.valueOf("heroAvatar")).set(user.getHeroAvatar());
        //构建结果并发送
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();

        Broadcaster.broadCast(newResult);

    }
    }
