package babyframework.helper;

import babyframework.factory.xml.Bean;
import babyframework.factory.xml.BeanScope;
import babyframework.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 将扫描好的class对象依次创建实例，并装入至容器中
 */
public final class BeanHelper {
    private static final Map<Class<?>,Bean> BEAN_MAP = new HashMap<Class<?>, Bean>();

    static {
        Set<Class<?>> classSet = ClassHelper.getBeanClassSet();
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

    /**
     * 将Bean装入至容器中
     * @param cls
     * @param object
     */
    public static void setBean(Class<?> cls,Object object) {
        try {
            BEAN_MAP.put(cls,new Bean(cls,cls.getName(),BeanScope.SINGLETON,cls.newInstance()));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


}
