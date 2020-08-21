package com.tinygame.rank;

import lombok.Data;

@Data
public class RankItem {

    /**
     * 排名id
     * */
    private int rankId;

    /**
     * 用户id
     * */
    private int userId;

    /**
     * 用户名
    * */
    private String userName;

    /**
     * 用户形象
     * */
    private String heroAvatar;

    /**
     *胜利次数
     * */
    private int win;
}
