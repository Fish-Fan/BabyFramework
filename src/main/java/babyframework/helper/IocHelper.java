package babyframework.helper;

import babyframework.annotation.Inject;
import babyframework.factory.xml.Bean;
import babyframework.util.ArrayUtil;
import babyframework.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 通过BeanHelper获取容器，扫描容器
 * 如果某个bean的某个属性property存在依赖注入情况
 * 将所需要的injectBean注入至属性property中
 */
public final class IocHelper {
    private static Map<Class<?>,Bean> BEAN_MAP;
    static {
        //获取容器
         BEAN_MAP = BeanHelper.getBeanMap();
         //扫描容器
        for(Map.Entry<Class<?>,Bean> beanEntry : BEAN_MAP.entrySet()) {
            Class<?> beanClass = beanEntry.getKey();
            Object beanInstance = beanEntry.getValue().getInstance();
            Field[] fields = beanClass.getDeclaredFields();
            if(ArrayUtil.isNotEmpty(fields)) {
                for(Field beanField : fields) {
                    Class<?> beanFieldClass = beanField.getType();
                    if(beanField.isAnnotationPresent(Inject.class)) {
                        Object beanFieldInstance = BEAN_MAP.get(beanFieldClass).getInstance();
                        if(beanFieldInstance != null) {
                            //通过反射初始化类的字段
                            ReflectionUtil.setField(beanInstance,beanField,beanFieldInstance);
                        }
                    }
                }
            }
        }
    }

    //返回容器，至此容器所有Bean全部初始化完成
    public static Map<Class<?>,Bean> getBeanMap() {
        return BEAN_MAP;
    }


}
