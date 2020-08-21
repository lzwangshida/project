package com.tinygame.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import com.tinygame.util.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class CmdHandlerFactory {

    static private final Logger LOGGER = LoggerFactory.getLogger(CmdHandlerFactory.class);

    /**
     * 消息字典
     * */
    static private final Map<Class<?>,ICmdHandler<? extends GeneratedMessageV3>> _handlerMap = new HashMap<>();

    private CmdHandlerFactory(){}

    public static void init(){
        String pageName = ICmdHandler.class.getPackage().getName();
        Set<Class<?>> classes = PackageUtil.listSubClazz(pageName, true, ICmdHandler.class);
        for (Class<?> clazz : classes) {
            if ((clazz.getModifiers()& Modifier.ABSTRACT)!=0){
                //为抽象类跳过
                continue;
            }

            Method[] currentArrays = clazz.getDeclaredMethods();

            //声明消息类型
            Class<?> msgType = null ;

            for(Method currentMethod : currentArrays){

                if ( !currentMethod.getName().equals("handler") ){
                    continue;
                }

                Class<?>[] parameterTypes = currentMethod.getParameterTypes();

                if (parameterTypes.length<2 ||
                        parameterTypes[1] ==GeneratedMessageV3.class ||
                    ! GeneratedMessageV3.class.isAssignableFrom(parameterTypes[1])
                ){
                    continue;
                }

                msgType=parameterTypes[1];

                break;
            }

            if (null == msgType){
                continue;
            }

            try {

                ICmdHandler<?> newHandler = (ICmdHandler<?>) clazz.newInstance();

                LOGGER.info("关联 {} <==> {}",msgType.getName(),clazz.getName());

                _handlerMap.put(msgType,newHandler);

            } catch (Exception e) {
                LOGGER.error(e.getMessage(),e);
            }

        }
    }

    public static  ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClass){
        if (null==msgClass){
            return  null;
        }
        return _handlerMap.get(msgClass);
    }

}
