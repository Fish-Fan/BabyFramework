package babyframework.proxy;

import java.lang.reflect.Method;

public abstract class AspectProxy implements Proxy{
    public final Object doProxy(ProxyChain proxyChain) {
        Object result = null;

        Class<?> cls = proxyChain.getTargetClass();
        Method method = proxyChain.getTargetMethod();
        Object[] params = proxyChain.getMethodParams();

        begin();
        try {
            if(intercept(cls,method,params)) {
                before(cls,method,params);
                result = proxyChain.doProxyChain();
                after(cls,method,params,result);
            } else {
                result = proxyChain.doProxyChain();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            end();
        }

        return result;
    }

    public void begin(){

    }

    public boolean intercept(Class<?> cls,Method method,Object[] params) {
        return true;
    }

    public void before(Class<?> cls,Method method,Object[] params) {

    }

    public void after(Class<?> cls,Method method,Object[] params,Object result) {

    }

    public void error(Class<?> cls,Method method,Object[] params) {

    }

    public void end() {

    }
}
