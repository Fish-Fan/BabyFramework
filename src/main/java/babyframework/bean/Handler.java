package babyframework.bean;

import java.lang.reflect.Method;

public class Handler {
    /**
     * 处理该请求的controller类
     */
    private Class<?> controllerClass;
    /**
     * 处理该请求的method
     */
    private Method actionMethod;

    public Handler(Class<?> controllerClass,Method actionMethod) {
        this.controllerClass = controllerClass;
        this.actionMethod = actionMethod;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getActionMethod() {
        return actionMethod;
    }
}
