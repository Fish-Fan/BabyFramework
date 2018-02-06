package babyframework.factory;

import babyframework.factory.xml.Bean;
import babyframework.helper.IocHelper;
import babyframework.helper.XMLHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BeanFactory {

    Map<Class<?>,Bean> beanContainer ;
    Map<String,Bean> xmlBeanContainer ;
    XMLHelper xmlHelper ;
    public BeanFactory() {
         beanContainer = IocHelper.getBeanMap();
    }

    public BeanFactory(String xmlLocation) {
        this();
        xmlHelper = new XMLHelper(xmlLocation);
        xmlBeanContainer = xmlHelper.getBeanContainer();
        mergeToBeanMap();
    }

    public Object getBeanByID(String ID) {
        return xmlHelper.getBeanByID(ID);
    }

    public <T> T getBeanByClass(Class<?> beanClass) {
        return (T) beanContainer.get(beanClass).getInstance();
    }

    public void mergeToBeanMap() {
        for(Map.Entry<String,Bean> entry : xmlBeanContainer.entrySet()) {
            Bean bean = entry.getValue();
            beanContainer.put(bean.getCls(),bean);
        }
    }


}
