package com.tinygame.cmdhandler;

import com.tinygame.login.LoginService;
import com.tinygame.login.db.UserEntity;
import com.tinygame.model.User;
import com.tinygame.model.UserManager;
import com.tinygame.msg.GameMsgProtocol;
import com.tinygame.server.Broadcaster;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    static private  final Logger LOGGER= LoggerFactory.getLogger(UserLoginCmdHandler.class);


    @Override
    public void handler(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
            if (null==ctx || null == cmd ){
                return;
            }

         String userName = cmd.getUserName();
         String password = cmd.getPassword();

         LOGGER.info("用户登陆username={},password={}",userName,password);


        try {
           LoginService.getInstance().userLogin(userName, password,(userEntity)->{


               if (null==userEntity){
                   LOGGER.error("用户登录失败,username={}",userName);
                   return null;
               }

               LOGGER.info(
                       "用户登陆成功, userId = {}, userName = {}",
                       userEntity.getUserId(),
                       userEntity.getUserName()
               );

               User user = new User();
               user.setUserId(userEntity.getUserId());
               user.setUserName(userEntity.getUserName());
               user.setHeroAvatar(userEntity.getHeroAvatar());
               user.setCurrHp(200);

               UserManager.addUser(user);

               //将用户id附着到管道
               ctx.channel().attr(AttributeKey.valueOf("userId")).set(user.getUserId());

               GameMsgProtocol.UserLoginResult.Builder loginBuilder = GameMsgProtocol.UserLoginResult.newBuilder();
               loginBuilder.setUserId(user.getUserId());
               loginBuilder.setUserName(user.getUserName());
               loginBuilder.setHeroAvatar(user.getHeroAvatar());

               GameMsgProtocol.UserLoginResult build = loginBuilder.build();
               ctx.writeAndFlush(build);
               return null;
            });
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            return;
        }


    }
}
