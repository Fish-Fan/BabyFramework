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
    private Map<String,Object> beanMap = new HashMap<String, Object>();

    public XMLHelper(String XMLFileName) {
        initBeanMap(initXMLReader(XMLFileName));
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
    private void initBeanMap(Document doc) {
        Element root = doc.getRootElement();
        List<Element> beanList = root.elements();
        for(Element bean : beanList) {
            String beanID = bean.attributeValue("id");
            T o = setBeanProperty(bean);
            beanMap.put(beanID,o);
        }
    }

    /**
     * 根据ID获取bean
     * 如果两个bean的ID冲突，则按照在XML中定义的顺序返回
     * 最后一个以此ID定义的bean
     */
    public T getBeanByID(String beanID) {
        return (T) beanMap.get(beanID);
    }

    /**
     * 根据class获取相同类型的bean
     */
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
     * 获取property内嵌的bean
     */
    public Object getBean(Element rootProperty) {
        Object object = null;
        List<Element> elementList = rootProperty.elements();
        if(elementList.size() > 0) {
            //遍历property内置bean
            for(Element bean : elementList) {
                String clazz = bean.attributeValue("class");
                try {
                    Class<?> cls = Class.forName(clazz);
                    object = cls.newInstance();
                    List<Element> properties = bean.elements();

                    for(Element property : properties) {
                        Field field = cls.getDeclaredField(property.attributeValue("name"));
                        field.setAccessible(true);
                        List<Element> childElements = property.elements();
                        if(childElements.size() > 0) {
                            Object childObject = getBean(property);
                            field.set(object,childObject);
                        } else {
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
            }
        }
        return object;
    }

    /**
     * 初始化bean中的各种属性，直接通过
     * Field来设置，不属于属性注入，也不属于
     * 构造函数注入，更不属于工厂方法注入
     */
    private <T> T setBeanProperty(Element bean) {
        Object o = null;
        try {
            Class clazz = Class.forName(bean.attributeValue("class"));
            o = clazz.newInstance();
            List<Element> properties = bean.elements();
            if(properties.size() > 0) {
                for(Element property : properties) {
                    String key = property.attributeValue("name");
                    Field field = clazz.getDeclaredField(key);

                    List<Element> childElements = property.elements();
                    if(childElements.size() > 0) {
                        Object childElement = getBean(property);
                        field.setAccessible(true);
                        field.set(o,childElement);
                    } else {
                        setFieldValue(o,field,property.attributeValue("value"));
                    }

                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return (T) o;
    }


    /**
     * 设置不同类型字段的值
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
}
