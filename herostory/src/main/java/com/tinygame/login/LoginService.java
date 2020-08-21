package com.tinygame.login;


import com.alibaba.fastjson.JSONObject;
import com.tinygame.async.AsyncOperationProcessor;
import com.tinygame.async.IAsyncOperation;
import com.tinygame.login.db.IUserDao;
import com.tinygame.login.db.UserEntity;
import com.tinygame.server.MysqlSessionFactory;
import com.tinygame.util.RedisUtil;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.function.Function;

public class LoginService {
    static private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    public static final LoginService instance = new LoginService();

    private LoginService() {
    }

    public static LoginService getInstance() {
        return instance;
    }

    public void userLogin(String username, String password, Function<UserEntity, Void> callBack) {

        if (null == username || null == password) {
            return;
        }

        AsyncGetUser asyncGetUser = new AsyncGetUser(username,password) {

            @Override
            public int getBindId() {
                String username = this.getUsername();
                char c = username.charAt(username.length() - 1);

                return c;
            }

            @Override
            public void doFinish() {
                if (null != callBack) {
                    callBack.apply(this.getUserEntity());
                }
            }
        };

        AsyncOperationProcessor.getInstance().processor(asyncGetUser);

}
    /**
     * 更新到用户信息到reids
     * */
    private void updateUserInfoToRedisBasicInfo(UserEntity userEntity){
        if (null == userEntity){
            return;
        }
        int userId = userEntity.getUserId();
        try (Jedis jedis = RedisUtil.getJedis()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId",userId);
            jsonObject.put("userName",userEntity.getUserName());
            jsonObject.put("heroAvatar",userEntity.getHeroAvatar());

            jedis.hset("user_"+userId,"BasicInfo",jsonObject.toJSONString());
        }catch (Exception ex){
            LOGGER.error(ex.getMessage(),ex);
        }

    }


     private  class  AsyncGetUser implements IAsyncOperation{
          private   String username;
          private   String password;
          private   UserEntity _userEntity=null;

           public AsyncGetUser(){}
           public AsyncGetUser(String username ,String password){
               this.username=username;
               this.password=password;
           }


           public String getUsername(){
               return username;
           }
          public UserEntity getUserEntity(){
               return _userEntity;
          }

         @Override
         public void doAsync() {
             try(SqlSession sqlSession = MysqlSessionFactory.openSqlSession()){

                 IUserDao dao = sqlSession.getMapper(IUserDao.class);


                 LOGGER.info("登陆的线程，={}",Thread.currentThread().getName());

                 UserEntity userEntity = dao.getUserByName(username);

                 if (null!=userEntity){
                     if (!password.equals(userEntity.getPassword())){
                         LOGGER.error("用户密码错误,userId={},username={}",userEntity.getUserId(),username);
                         throw new RuntimeException("用户名密码错误");
                     }
                 }else{

                     userEntity = new UserEntity();
                     userEntity.setUserName(username);
                     userEntity.setPassword(password);
                     userEntity.setHeroAvatar("Hero_Shaman");//默认使用
                     //插入到数据库
                     dao.insertUser(userEntity);


                 }
                    _userEntity =userEntity;
                 //添加用户信息到redis
                 LoginService.getInstance().updateUserInfoToRedisBasicInfo(_userEntity);

             }catch (Exception e){
                 LOGGER.error(e.getMessage(),e);
                 return;
             }
         }
     }
}

