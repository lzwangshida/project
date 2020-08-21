package com.tinygame.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager{

    static private final Map<Integer,User> _userMap = new ConcurrentHashMap<>();
    private UserManager(){

    }

    static public void addUser(User user){
        if (null!=user){
            _userMap.put(user.getUserId(),user);
        }
    }

    static public void removeUserById(Integer id){
        if (null!=id){
            _userMap.remove(id);
        }
    }
    static  public Collection<User> listUser(){
        return _userMap.values();
    }

    static  public User getUserById(Integer id){
        return _userMap.get(id);
    }

}
