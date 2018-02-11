package babyframework.util;

import babyframework.factory.xml.Bean;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
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
     * 创建实例，并完成各种属性的初始化
     * @return
     */
    public static Object newInstance(Class<?> clazz, List<Bean.Property> properties) {
        Object instance = null;
        try {
            instance = clazz.newInstance();
            for(Bean.Property property : properties) {
                String name = property.getName();
                //获取set方法
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(name,clazz,StringUtil.getGetterMethod(name),StringUtil.getSetterMethod(name));
                Method method = propertyDescriptor.getWriteMethod();

                invokeMethod(instance,method,property.getObject());
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return instance;
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
