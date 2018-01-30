package babyframework.factory;

import babyframework.helper.IocHelper;
import babyframework.helper.XMLHelper;
import babyframework.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeanFactory {

    Map<Class<?>,Bean> beanMap ;
    Map<String,Bean> xmlBeanMap ;
    XMLHelper xmlHelper ;
    public BeanFactory() {
         beanMap = IocHelper.getBeanMap();
    }

    public BeanFactory(String xmlLocation) {
        this();
        xmlHelper = new XMLHelper(xmlLocation);
        xmlBeanMap = xmlHelper.getXMLBeanContainer();
        mergeToBeanMap();
    }

    public Object getBeanByID(String ID) {
        Object o = null;
        Bean bean =  xmlBeanMap.get(ID);
        if(bean.scope != BeanScope.SINGLETON) {
            o = ReflectionUtil.newInstance(bean.cls);
        } else {
            o = bean.object;
        }
        return o;
    }

    public <T> List<T> getBeanByClass(Class<?> beanClass) {
        List<T> objectList = new ArrayList<T>();
        objectList.add((T) beanMap.get(beanClass).object);
        objectList.addAll(xmlHelper.<T>getBeansByType(beanClass));
        return objectList;
    }

    public void mergeToBeanMap() {
        for(Map.Entry<String,Bean> entry : xmlBeanMap.entrySet()) {
            Bean bean = entry.getValue();
            beanMap.put(bean.cls,bean);
        }
    }


}
