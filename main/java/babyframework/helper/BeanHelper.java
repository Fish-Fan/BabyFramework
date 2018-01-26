package babyframework.helper;

import babyframework.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class BeanHelper {
    private static final Map<Class<?>,Object> BEAN_MAP = new HashMap<Class<?>, Object>();

    static {
        Set<Class<?>> classSet = ClassHelper.getClassSet();
        for(Class<?> clazz : classSet) {
            Object o = ReflectionUtil.newInstance(clazz);
            BEAN_MAP.put(clazz,o);
        }
    }

    /**
     * 获取Bean映射
     */
    public static Map<Class<?>,Object> getBeanMap() {
        return BEAN_MAP;
    }

    /**
     * 根据Class对象获取Bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        if(!BEAN_MAP.containsKey(clazz)) {
            throw new RuntimeException("can not get bean by class : " + clazz);
        }
        return (T) BEAN_MAP.get(clazz);
    }


}
