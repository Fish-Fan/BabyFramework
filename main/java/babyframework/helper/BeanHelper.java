package babyframework.helper;

import babyframework.factory.Bean;
import babyframework.factory.BeanScope;
import babyframework.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class BeanHelper {
    private static final Map<Class<?>,Bean> BEAN_MAP = new HashMap<Class<?>, Bean>();

    static {
        Set<Class<?>> classSet = ClassHelper.getClassSet();
        for(Class<?> clazz : classSet) {
            Object o = ReflectionUtil.newInstance(clazz);
            BEAN_MAP.put(clazz,new Bean(clazz,clazz.getName(), BeanScope.SINGLETON,o));
        }
    }

    /**
     * 获取BEAN_MAP
     */
    public static Map<Class<?>,Bean> getBeanMap() {
        return BEAN_MAP;
    }


}
