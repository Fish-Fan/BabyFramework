package babyframework.helper;

import babyframework.annotation.Inject;
import babyframework.factory.Bean;
import babyframework.factory.BeanScope;
import babyframework.util.ArrayUtil;
import babyframework.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

public final class IocHelper {
    private static Map<Class<?>,Bean> BEAN_MAP;
    static {
         BEAN_MAP = BeanHelper.getBeanMap();
        for(Map.Entry<Class<?>,Bean> beanEntry : BEAN_MAP.entrySet()) {
            Class<?> beanClass = beanEntry.getKey();
            Object beanInstance = beanEntry.getValue().object;
            Field[] fields = beanClass.getDeclaredFields();
            if(ArrayUtil.isNotEmpty(fields)) {
                for(Field beanField : fields) {
                    Class<?> beanFieldClass = beanField.getType();
                    if(beanField.isAnnotationPresent(Inject.class)) {
                        Object beanFieldInstance = BEAN_MAP.get(beanFieldClass).object;
                        if(beanFieldInstance != null) {
                            //通过反射初始化类的字段
                            ReflectionUtil.setField(beanInstance,beanField,beanFieldInstance);
                        }
                    }
                }
            }
        }
    }

    public static Map<Class<?>,Bean> getBeanMap() {
        return BEAN_MAP;
    }


}
