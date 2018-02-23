package babyframework.helper;

import babyframework.annotation.Controller;
import babyframework.annotation.Service;
import babyframework.util.ClassUtil;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * 获取指定包下的所有class类
 */
public final class ClassHelper {
    private static final Set<Class<?>> CLASS_SET;

    static {
        String basePackage = ConfigHelper.getAppBasePackage();
        CLASS_SET = ClassUtil.getClassSet(basePackage);
    }

    /**
     * 获取应用包下所有类
     * @return
     */
    public static Set<Class<?>> getClassSet() {
        return CLASS_SET;
    }

    /**
     * 获取应用包下所有Controller类
     */
    public static Set<Class<?>> getControllerClassSet() {
        Set<Class<?>> subClassSet = new HashSet<Class<?>>();
        for(Class<?> clazz : CLASS_SET) {
            if(clazz.isAnnotationPresent(Controller.class)) {
                subClassSet.add(clazz);
            }
        }
        return subClassSet;
    }

    /**
     * 获取应用包下所有Service类
     */
    public static Set<Class<?>> getServiceClassSet() {
        Set<Class<?>> subClassSet = new HashSet<Class<?>>();
        for(Class<?> clazz : CLASS_SET) {
            if(clazz.isAnnotationPresent(Service.class)) {
                subClassSet.add(clazz);
            }
        }
        return subClassSet;
    }

    /**
     * 获取应用包下所有Bean
     * 包含所有的被Controller,Service注解标记的类
     */
    public static Set<Class<?>> getBeanClassSet() {
        Set<Class<?>> beanClassSet = new HashSet<Class<?>>();
        beanClassSet.addAll(getControllerClassSet());
        beanClassSet.addAll(getServiceClassSet());
        return beanClassSet;
    }

    /**
     * 获取应用包下某父类(或接口)的所有子类(实现类)
     */
    public static Set<Class<?>> getClassSetBySuper(Class<?> superClass) {
        Set<Class<?>> subClassSet = new HashSet<Class<?>>();
        for(Class clazz : CLASS_SET) {
            if(superClass.isAssignableFrom(clazz) && !superClass.equals(clazz)) {
                subClassSet.add(clazz);
            }
        }
        return subClassSet;
    }

    /**
     * 获取应用包下带有某注解的所有类
     */
    public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass) {
        Set<Class<?>> subClassSet = new HashSet<Class<?>>();
        for(Class clazz : CLASS_SET) {
            if(clazz.isAnnotationPresent(annotationClass)) {
                subClassSet.add(clazz);
            }
        }
        return subClassSet;
    }


}
