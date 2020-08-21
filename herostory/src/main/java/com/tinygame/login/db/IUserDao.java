package com.tinygame.login.db;

public interface IUserDao {

    UserEntity getUserByName(String name);
    void insertUser(UserEntity userEntity);
}
