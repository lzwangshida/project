package com.tinygame.async;

import com.tinygame.login.db.UserEntity;
import com.tinygame.server.MainThreadProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncOperationProcessor {

        public static final AsyncOperationProcessor instance = new AsyncOperationProcessor();


        public static AsyncOperationProcessor getInstance(){
                return instance;
        }

        private  final ExecutorService[] _es = new ExecutorService[8];

        private AsyncOperationProcessor(){
            for (int i =0; i <_es.length;i++ ){
                    final  String threadName = "AsyncOperationProcessor"+i;

                  _es[i] = Executors.newSingleThreadExecutor((newRunnable)->{
                          Thread newThread = new Thread(newRunnable);
                          newThread.setName(threadName);
                          return newThread;
                  });
            }
        }




        public void processor(IAsyncOperation asyncOp){
                if (null==asyncOp){
                        return;
                }
                int bindId = asyncOp.getBindId();
                int _esBindId=bindId  % _es.length;

                _es[_esBindId].submit(()->{

                        asyncOp.doAsync();

                        MainThreadProcessor.getInstance().processor(asyncOp::doFinish);
                });

        }
}
