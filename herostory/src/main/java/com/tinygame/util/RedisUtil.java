package com.tinygame.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class RedisUtil {
    /**
     * 日志对象
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    /**
     * redis 连接池
     * */
    private static JedisPool jedisPool = null;

    /**
     * 私有化
     * */
    private RedisUtil(){}

    /**
     * 初始化
     * */
    public static void init(){
        try {
            jedisPool = new JedisPool("192.168.121.11",6379);
        }catch (Exception ex){
            LOGGER.error(ex.getMessage(),ex);
        }
    }
    /**
     * 获得redis实例
     * */
    public static Jedis getJedis(){
        if (null == jedisPool){
            throw new RuntimeException("jedisPool 尚未初始化");
        }
        Jedis jedis = jedisPool.getResource();
        //jedis.auth("root");
        return jedis;
    }


}
