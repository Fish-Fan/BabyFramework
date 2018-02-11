package babyframework.helper;

import babyframework.annotation.Action;
import babyframework.bean.Handler;
import babyframework.bean.Request;
import babyframework.util.ArrayUtil;
import babyframework.util.ClassUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ControllerHelper {
    private static Map<Request,Handler> ACTION_MAP = new HashMap<Request, Handler>();

    static {
        Set<Class<?>> controllerClassSet = ClassUtil.getControllerClass();
        for(Class<?> controllerCls : controllerClassSet) {
            Method[] methods = controllerCls.getDeclaredMethods();
            for(Method method : methods) {
                if(method.isAnnotationPresent(Action.class)) {
                    Action action = method.getAnnotation(Action.class);
                    String mapping = action.value();

                    String[] array =  mapping.split(":");
                    if(ArrayUtil.isNotEmpty(array) && array.length == 2) {
                        String requestMethod = array[0].toUpperCase();
                        String requestPath = array[1];
                        Request request = new Request(requestMethod,requestPath);
                        Handler handler = new Handler(controllerCls,method);
                        ACTION_MAP.put(request,handler);
                    }

                }
            }
        }
    }

    /**
     * 返回handler
     * @param requestMethod
     * @param requestPath
     * @return
     */
    public static Handler getHandler(String requestMethod,String requestPath) {
        Request request = new Request(requestMethod,requestPath);
        return ACTION_MAP.get(request);
    }
}
