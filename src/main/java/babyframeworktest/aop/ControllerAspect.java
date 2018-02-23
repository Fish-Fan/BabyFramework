package babyframeworktest.aop;

import babyframework.annotation.Aspect;
import babyframework.annotation.Controller;
import babyframework.proxy.AspectProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@Aspect(Controller.class)
public class ControllerAspect extends AspectProxy {
    private static final Logger logger = LoggerFactory.getLogger(ControllerAspect.class);

    private long begin;
    public void before(Class<?> cls, Method method,Object[] params) {
        logger.debug("--------------");
        logger.debug(cls.getName());
        logger.debug(method.getName());
        begin = System.currentTimeMillis();
    }

    public void after(Class<?> cls,Method method,Object[] params,Object result) {
        logger.debug("time : " + (System.currentTimeMillis() - begin));
        logger.debug("-----end------");
    }
}
