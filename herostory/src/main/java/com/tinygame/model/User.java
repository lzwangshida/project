package com.tinygame.model;


import lombok.Data;

@Data
public class User {

    /**
     * 用户id
     * */
    private Integer userId;

    /**
     * 用户名
     * */
    private String userName;


    /**
    * 英雄类型
    * */
    private String heroAvatar;

    /**
     * 当前血量
     * */
    private int currHp ;

    /**
     * 移动状态
     * */
    private final MoveState moveState = new MoveState();

}
