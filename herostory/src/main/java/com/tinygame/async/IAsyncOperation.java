package com.tinygame.async;

public interface IAsyncOperation {
    /**
     * 获取绑定id
     * */
    default int getBindId(){
        return 0;
    }


    /**
    * 执行异步操作
    * */
    void doAsync();

    /**
     * 执行完成逻辑
     * */
    default  void doFinish(){

    };
}
