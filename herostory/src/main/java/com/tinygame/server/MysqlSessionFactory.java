package com.tinygame.server;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public final class MysqlSessionFactory {

    /**
    * 单列
    * */
    private static SqlSessionFactory _sqlSessionFactory;

    private MysqlSessionFactory(){}

    /**
     * 初始化
    * */
    public static void init(){
        try {
            _sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(Resources.getResourceAsStream("MyBatisConfig.xml"));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


/**
 * 开启会话
* */
    public static SqlSession openSqlSession(){
        if (null==_sqlSessionFactory){
            throw new RuntimeException("_sqlSessionFactory未初始化");
        }
        return _sqlSessionFactory.openSession(true);
    }

}
