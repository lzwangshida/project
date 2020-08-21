package com.tinygame.async.mq;

import lombok.Data;

/**
 * 战斗消息
 * */
@Data
public class VictorMsg {

    /**
     * 胜利者id
     * */
    private int winnerId;

    /**
     * 失败者id
     * */
    private int loserId;
}
