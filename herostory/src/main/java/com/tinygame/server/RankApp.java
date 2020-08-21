package com.tinygame.server;

import com.tinygame.async.mq.MQConsumer;
import com.tinygame.rank.RankService;
import com.tinygame.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RankApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(RankApp.class);

    public static void main(String[] args) {
        MQConsumer.init();
        RedisUtil.init();

        LOGGER.info("RankApp启动成功");
    }
}
