package com.tinygame.rank;

import com.alibaba.fastjson.JSONObject;
import com.tinygame.async.AsyncOperationProcessor;
import com.tinygame.async.IAsyncOperation;
import com.tinygame.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class RankService {
    /**
     * 日志对象
     * */
    private static final Logger LOGGER = LoggerFactory.getLogger(RankService.class);

    /**
     * 单例
    * */
    private static final RankService instance = new RankService();

    /**
     * 私有化
     * */
    private RankService(){}

    /**
     * 获取单例
    * */
    public static RankService getInstance(){
        return instance;
    }

    /**
     * 获取排行
     * @param  callback 回调函数
     * */
    public void getRank(Function<List<RankItem>,Void> callback){
        if (null == callback ){
            return;
        }

        AsyncGetRank asyncGetRank = new AsyncGetRank(){
            @Override
            public void doFinish() {
                callback.apply(this.get_rankItemList());
            }
        };
        AsyncOperationProcessor.getInstance().processor(asyncGetRank);

    }
    /**
     * 内部类，使用异步方式获取排名
     */
    private class AsyncGetRank implements IAsyncOperation{
        /**
         * 排名列表
         * */
        private  List<RankItem> _rankItemList = null;

        public List<RankItem> get_rankItemList(){
            return _rankItemList;
        }

        @Override
        public void doAsync() {

            try(Jedis jedis = RedisUtil.getJedis()){
                /**
                 * 获取排名集合
                 * */
                Set<Tuple> set = jedis.zrevrangeWithScores("Rank", 0, 9);


                _rankItemList = new ArrayList<>();

                int rankId = 0;
                for(Tuple t : set){
                   //获取userId
                    int userId = Integer.parseInt(t.getElement());
                    LOGGER.info("用户ID={}",userId);
                    //根据userId获取用户基本信息
                    String userInfo = jedis.hget("user_" + userId, "BasicInfo");
                    LOGGER.info("用户信息={}",userInfo);

                    RankItem rankItem = new RankItem();
                    rankItem.setRankId(++rankId);
                    rankItem.setUserId(userId);
                    rankItem.setWin((int)t.getScore());
                    JSONObject jsonObject = JSONObject.parseObject(userInfo);
                    rankItem.setUserName(jsonObject.getString("userName"));
                    rankItem.setHeroAvatar(jsonObject.getString("heroAvatar"));

                    _rankItemList.add(rankItem);
                }
            }catch (Exception ex){
                LOGGER.error(ex.getMessage(),ex);
            }

        }
    }
    /**
     * 刷新排名
     * */
    public void refreshRank(int winnerId , int loserId){
        try(Jedis jedis = RedisUtil.getJedis()) {
            //增加用户胜利次数和失败次数
            jedis.hincrBy("user_"+winnerId,"Win",1);
            jedis.hincrBy("user_"+loserId,"Lose",1);

            String win =jedis.hget("user_"+winnerId,"Win");
            LOGGER.info("赢家的获胜场数，==={}",win);

            Integer winInt = Integer.parseInt(win);

            jedis.zadd("Rank",winInt,String.valueOf(winnerId));
        }catch (Exception ex){
            LOGGER.error(ex.getMessage(),ex);
        }
    }


}
