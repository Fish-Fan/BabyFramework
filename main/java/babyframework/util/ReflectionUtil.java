package babyframework.util;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public final class ReflectionUtil {
//    private static Logger logger = (Logger) LoggerFactory.getLogger(ReflectionUtil.class);

    /**
     * 创建实例
     */
    public static Object newInstance(Class<?> clazz) {
        Object o = null;
        try {
            o = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    /**
     * 调用方法
     */
    public static Object invokeMethod(Object o, Method method,Object...args) {
        Object result = null;
        method.setAccessible(true);

        try {
            result = method.invoke(o,args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 设置成员变量的值
     */
    public static void setField(Object o, Field field,Object value) {
        field.setAccessible(true);
        try {
            field.set(o,value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
