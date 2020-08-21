package com.tinygame.login.db;

import lombok.Data;

@Data
public class UserEntity {


    /**
     * 用户 Id
     */
    private int userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 英雄形象
     */
    private String heroAvatar;
}
