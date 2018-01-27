package babyframework.helper;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

/**
 * create by fanyank
 * 通过XML读取用户自定义的bean信息，用户需要为
 * 每一个bean设置一个唯一的ID，以便于在获取和引用
 * bean的时候获取到唯一的bean,支持在XML中配置
 * bean的属性为集合类型，如list,set,map
 */

public class XMLHelper<T> {
    private static Logger logger = LoggerFactory.getLogger(XMLHelper.class);
    //装载bean的容器
    private Map<String,Object> beanMap = new HashMap<String, Object>();
    //保存bean的ref属性断点信息
    private Map<Object,RefMessage> beanRefMessageMap = new HashMap<Object, RefMessage>();

    public XMLHelper(String XMLFileName) {
        initBeanMap(initXMLReader(XMLFileName));
        injectBeanFromRefField();
    }

    /**
     * 初始化读取XML文件的reader
     * @param XMLFileName
     * @return
     */
    private Document initXMLReader(String XMLFileName) {
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(new File(XMLFileName));
        } catch (DocumentException e) {
            logger.error("无法正确读取{}XML文件，请确认XML文件格式是否正确",XMLFileName);
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * 扫描XML文件中的所有bean，并完成初始化
     * @param doc
     */
    @SuppressWarnings("unchecked")
    private void initBeanMap(Document doc) {
        Element root = doc.getRootElement();
        List<Element> beanList = root.elements("bean");
        for(Element bean : beanList) {
            String beanID = bean.attributeValue("id");
            T o = (T) setBeanProperties(bean);
            beanMap.put(beanID,o);
        }
    }

    /**
     * 根据ID获取bean
     * 如果两个bean的ID冲突，则按照在XML中定义的顺序返回
     * 最后一个以此ID定义的bean
     */
    @SuppressWarnings("unchecked")
    public T getBeanByID(String beanID) {
        return (T) beanMap.get(beanID);
    }

    /**
     * 根据class获取相同类型的bean
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getBeansByType(Class<?> clazz) {
        List<T> beanList = new ArrayList<T>();
        for(Map.Entry<String,Object> entry : beanMap.entrySet()) {
            T o = (T) entry.getValue();
            Class oClass = o.getClass();
            if(oClass.getName().equals(clazz.getName())) {
                beanList.add(o);
            }
        }
        return beanList;
    }

    /**
     * 通过字段注入初始化bean中的属性
     */
    @SuppressWarnings("unchecked")
    private Object setBeanProperties(Element bean) {
        Object object = null;
        String clazz = bean.attributeValue("class");
        try {
            Class<?> cls = Class.forName(clazz);
            object = cls.newInstance();
            List<Element> properties = bean.elements("property");

            for(Element property : properties) {
                Field field = cls.getDeclaredField(property.attributeValue("name"));
                field.setAccessible(true);
                //初始化propertyMessage对象，与property有关的所有信息从这里获取
                PropertyMessage propertyMessage = new PropertyMessage(property,field);

                //检查是否有内联bean
                if(propertyMessage.innerBeans.size() > 0) {
                    //递归初始化内联bean的属性
                    Object childObject = setBeanProperties(propertyMessage.innerBeans.get(0));
                    field.set(object,childObject);
                //检查是否有ref属性
                } else if(propertyMessage.refMessage.refValue != null) {
                    beanRefMessageMap.put(object,new RefMessage(field,propertyMessage.refMessage.refValue));
                //检查是否有list属性
                } else if(propertyMessage.list.size() > 0) {
                    Element list = propertyMessage.list.get(0);
                    List<Element> valueList = list.elements("value");
                }
                else {
                    setFieldValue(object,field,property.attributeValue("value"));
                }


            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return object;
    }


    /**
     * 将从XML中读取的value转为bean中属性所需要的类型
     */
    private void setFieldValue(Object o,Field field,String value) {
        field.setAccessible(true);
        try {
            //如果字段是int类型
            if(field.getType().getName().equals("int") || field.getType().getName().equals("Integer")) {
                Integer intValue = Integer.parseInt(value);
                field.set(o,intValue);
                //如果字段是float类型
            } else if(field.getType().getName().equals("float") || field.getType().getName().equals("Float")) {
                Float floatValue = Float.parseFloat(value);
                //如果字段是bool类型
            } else if(field.getType().getName().equals("boolean") || field.getType().getName().equals("Boolean")) {
                if(value.equals("true"))
                    field.set(o,true);
                else
                    field.set(o,false);
                //如果字段是long类型
            } else if(field.getType().getName().equals("long") || field.getType().getName().equals("Long")) {
                Long longValue = Long.parseLong(value);
                field.set(o,longValue);
                //如果字段是double类型
            } else if(field.getType().getName().equals("double") || field.getType().getName().equals("Double")) {
                Double doubleValue = Double.parseDouble(value);
                field.set(o,doubleValue);
                //如果字段是String类型
            } else {
                field.set(o,value);
            }
        } catch (IllegalAccessException e) {
            logger.error("设置字段值时发生错误，请检查类型转换是否在可支持的范围内");
            e.printStackTrace();
        }
    }

    /**
     * 获取该property中的ref属性
     */
    private String getRefField(Element property) {
        return property.attributeValue("ref");
    }

    /**
     * 将ref字段所依赖的bean注入
     */
    private void injectBeanFromRefField() {
        for(Map.Entry<Object,RefMessage> entry : beanRefMessageMap.entrySet()) {
            Object o = entry.getKey();
            RefMessage refMessage = entry.getValue();
            Field field = refMessage.field;
            field.setAccessible(true);
            try {
                field.set(o,beanMap.get(refMessage.refValue));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 内部静态类，用于存放ref字段的信息
     * 当整个xml文件被读取完毕之后，检查
     * 所有ref字段，并将对应的bean注入
     */
    private static class RefMessage {
        /**
         * 字段
         */
        private Field field;
        /**
         * 字段对应的bean的ID
         */
        private String refValue;

        public RefMessage(Field field, String refValue) {
            this.field = field;
            this.refValue = refValue;
        }
    }

    /**
     * 内部静态类，用于存放property的各种信息
     */
    @SuppressWarnings("unchecked")
    private static class PropertyMessage {
        private Element property;
        private List<Element> innerBeans;
        private RefMessage refMessage;
        private List<Element> list;
        private List<Element> set;
        private List<Element> map;

        public PropertyMessage(Element property,Field field) {
            this.property = property;
            this.innerBeans = property.elements("bean");
            this.refMessage = new RefMessage(field,property.attributeValue("ref"));
            this.list = property.elements("list");
            this.set = property.elements("set");
            this.map = property.elements("map");
        }
    }
}
