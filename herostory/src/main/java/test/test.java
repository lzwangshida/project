package test;

import com.tinygame.cmdhandler.ICmdHandler;
import com.tinygame.util.PackageUtil;

import java.lang.reflect.Modifier;
import java.util.Set;

public class test {
    public static void main(String[] args) {
        Set<Class<?>> classes = PackageUtil.listSubClazz(ICmdHandler.class.getPackage().getName(), true, ICmdHandler.class);

        for (Class<?> c : classes){
            if ((c.getModifiers()& Modifier.ABSTRACT)==0) {
                System.out.println(c.getName());
            }
        }
    }
}
