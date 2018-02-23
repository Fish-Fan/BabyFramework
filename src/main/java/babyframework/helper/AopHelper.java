package babyframework.helper;

import babyframework.annotation.Aspect;
import babyframework.proxy.AspectProxy;
import babyframework.proxy.Proxy;
import babyframework.proxy.ProxyManager;

import java.lang.annotation.Annotation;
import java.util.*;

public final class AopHelper {
    static {
        try {
            Map<Class<?>,Set<Class<?>>> proxyMap = createProxyMap();
            Map<Class<?>,List<Proxy>> targetMap = createTargetMap(proxyMap);
            for(Map.Entry<Class<?>,List<Proxy>> targetEntry : targetMap.entrySet()) {
                Class<?> targetClass = targetEntry.getKey();
                List<Proxy> proxyList = targetEntry.getValue();
                Object proxy = ProxyManager.createProxy(targetClass,proxyList);
                BeanHelper.setBean(targetClass,proxy);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static Set<Class<?>> createTargetClassSet(Aspect aspect) {
        Set<Class<?>> targetClassSet = new HashSet<>();
        Class<? extends Annotation> annotation = aspect.value();
        if(annotation != null && annotation.equals(Aspect.class)) {
            targetClassSet.addAll(ClassHelper.getClassSetByAnnotation(annotation));
        }
        return targetClassSet;
    }

    private static Map<Class<?>,Set<Class<?>>> createProxyMap() {
        Map<Class<?>,Set<Class<?>>> proxyMap = new HashMap<>();
        Set<Class<?>> classSet = ClassHelper.getClassSetBySuper(AspectProxy.class);
        for(Class<?> proxyClass : classSet) {
            if(proxyClass.isAnnotationPresent(Aspect.class)) {
                Aspect aspect = proxyClass.getAnnotation(Aspect.class);
                Set<Class<?>> targetClassSet = createTargetClassSet(aspect);
                proxyMap.put(proxyClass,targetClassSet);
            }
        }
        return proxyMap;
    }

    private static Map<Class<?>,List<Proxy>> createTargetMap(Map<Class<?>,Set<Class<?>>> proxyMap) throws IllegalAccessException, InstantiationException {
        Map<Class<?>,List<Proxy>> targetMap = new HashMap<>();
        for(Map.Entry<Class<?>,Set<Class<?>>> proxyEntry : proxyMap.entrySet()) {
            Class<?> proxyClass = proxyEntry.getKey();
            Set<Class<?>> targetClassSet = proxyEntry.getValue();
            for(Class<?> targetClass : targetClassSet) {
                Proxy proxy = (Proxy) targetClass.newInstance();
                if(targetMap.containsKey(targetClass)) {
                    targetMap.get(targetClass).add(proxy);
                } else {
                    List<Proxy> proxyList = new ArrayList<>();
                    proxyList.add(proxy);
                    targetMap.put(targetClass,proxyList);
                }
            }
        }
        return targetMap;
    }

}
