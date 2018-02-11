package babyframework.helper;

import babyframework.factory.xml.Bean;
import babyframework.factory.xml.BeanScope;
import babyframework.util.ReflectionUtil;
import babyframework.util.StringUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


public final class XMLHelper {
    private static Map<String,Bean> beanContainer = new HashMap<String, Bean>();

    /**
     * 1. 读取所有的bean,并且设置bean的属性，如果碰到属性的类型为内联bean，
     那么创建这个内联bean的实例，如果读取到属性的类型为ref(引用其他bean的实例)，那么创建
     一个Ref对象，保存该引用的信息。最后将所有的bean保存至beanContainer<String,Bean>
     容器中，其中String为bean的ID,Bean为保存xml信息的对象。
     2. 通过Bean对象中定义的信息，创建bean的实例
     3. 如果容器中存在ref(引用其他bean的实例)，那么从beanContainer容器中获取该实例，并保存至
     Bean对象中的property属性中。
     4. 刷新beanContainer容器，即将ref类型的bean通过反射设置为bean实例的属性。
     */
    public XMLHelper(String xmlLocation) {
        initBeanContainer(getXMLReader(xmlLocation));
        initBeanInstance();
        injectBean();
        refreshContainer();
    }

    /**
     * 根据ID获取Bean
     * @param ID
     * @return
     */
    public Object getBeanByID (String ID) {
        Bean bean = beanContainer.get(ID);
        if(bean.getScope() == BeanScope.SINGLETON) {
            return beanContainer.get(ID).getInstance();
        } else if(bean.getScope() == BeanScope.PROTOTYPE) {
            Object object = ReflectionUtil.newInstance(bean.getCls(),bean.getProperties());
            return object;
        }
        return null;
    }

    /**
     * 根据class获取Bean
     * @param cls
     * @return
     */
    public List<Object> getBeanByClass(Class<?> cls) {
        List<Object> objects = new ArrayList<Object>();
        for(Map.Entry<String,Bean> entry : beanContainer.entrySet()) {
            Bean bean = entry.getValue();
            if(bean.getCls() == cls) {
                objects.add(bean.getInstance());
            }
        }
        return objects;
    }

    /**
     * 获取xml配置容器
     * @return
     */
    public Map<String,Bean> getBeanContainer() {
        return beanContainer;
    }

    /**
     * 初始化读取XML文件的reader
     * @param xmlLocation
     * @return
     */
    private Document getXMLReader(String xmlLocation) {
        Document doc = null;
        try {
            SAXReader reader = new SAXReader();
            doc = reader.read(xmlLocation);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * 扫描XML文件中的所有bean，并完成初始化
     * @param doc
     */
    @SuppressWarnings("unchecked")
    private void initBeanContainer(Document doc) {
        List<Element> beanEleList = doc.getRootElement().elements("bean");
        for(Element beanEle : beanEleList) {
            try {
                Class<?> cls = Class.forName(getTagByProperty(beanEle,"class"));
                String beanName = getTagByProperty(beanEle,"id");
                String beanScope = getTagByProperty(beanEle,"scope");
                BeanScope scope = BeanScope.SINGLETON;
                if(StringUtil.isNotEmpty(beanScope) && beanScope.equals("prototype")) {
                    scope = BeanScope.PROTOTYPE;
                }

                Bean bean = new Bean(cls,beanName,scope);
                //设置bean的各项属性
                bean.setProperties(setBeanProperty(beanEle,bean));
                beanContainer.put(beanName,bean);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 刷新容器，将ref通过属性注入至instance
     */
    private void refreshContainer() {
        for(Map.Entry<String,Bean> entry : beanContainer.entrySet()) {
            Bean bean = entry.getValue();
            Object instance = bean.getInstance();
            for(Bean.Property property : bean.getProperties()) {
                if(property.getRef() != null) {
                    Class cls = bean.getCls();
                    String setterMethod = StringUtil.getSetterMethod(property.getName());
                    String getterMethod = StringUtil.getGetterMethod(property.getName());
                    try {
                        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(property.getName(),cls,getterMethod,setterMethod);
                        Method method = propertyDescriptor.getWriteMethod();
                        ReflectionUtil.invokeMethod(instance,method,property.getObject());
                    }  catch (IntrospectionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * bean扫描完成后创建实例
     */
    private void initBeanInstance() {
        for(Map.Entry<String,Bean> entry : beanContainer.entrySet()) {
            Bean bean = entry.getValue();
            bean.setInstance(ReflectionUtil.newInstance(bean.getCls(),bean.getProperties()));
        }
    }

    /**
     * 获取合适的bean注入至ref字段中
     */
    private void injectBean() {
        for(Map.Entry<String,Bean> entry : beanContainer.entrySet()) {
            Bean bean = entry.getValue();
            doInjectBean(bean.getProperties());
        }
    }

    /**
     * 注入ref属性关联的bean
     * @param properties
     */
    private void doInjectBean(List<Bean.Property> properties) {
        for(Bean.Property property : properties) {
            if(property.getRef() != null) {
                Bean.Ref ref = property.getRef();
                if(ref.getRefEle() != null) {

                }
                else if(ref.getList() != null) {
                    processListField(ref,property);
                } else if(ref.getSet() != null) {
                    processSetField(ref,property);
                } else if(ref.getMap() != null) {
                    processMapField(ref,property);
                }

            }
        }
    }


    /**
     * 设置bean的各项属性
     * @param beanEle
     * @param bean
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Bean.Property> setBeanProperty(Element beanEle, Bean bean) {
        List<Bean.Property> properties = new ArrayList<Bean.Property>();
        List<Element> propertyEleList = beanEle.elements("property");
        for(Element propertyEle : propertyEleList) {

            try {
                String propertyName = getTagByProperty(propertyEle,"name");
                String propertyValue = getTagByProperty(propertyEle,"value");

                Class cls = bean.getCls();
                Field field = cls.getDeclaredField(propertyName);
                Class propertyCls = field.getType();

                Bean.Property property = new Bean.Property(propertyName,propertyEle,propertyCls);
                //如果property含有value字段
                if(StringUtil.isNotEmpty(propertyValue)) {
                    property.setValue(propertyValue);
                    Object propertyObject = TypeConvert(field,propertyValue);
                    property.setObject(propertyObject);
                    //如果property含有内联bean
                } else if(checkTagHasInnerBean(propertyEle) != null){
                    Object innerObject = getPropertyInnerBean(propertyEle,property);
                    property.setObject(innerObject);
                    //如果property含有ref标签
                } else if(checkTagHasRefField(propertyEle) != null) {
                    Bean.Ref ref = new Bean.Ref(propertyEle,field);
                    property.setRef(ref);
                    //如果property含有list标签
                } else if(checkTagHasListField(propertyEle) != null) {
                    //将所有的value标签暂时保存在内存中
                    Bean.Ref ref = new Bean.Ref(propertyEle,field);
                    property.setRef(ref);
                    //如果含有set标签
                } else if(checkTagHasSetField(propertyEle) != null) {
                    Bean.Ref ref = new Bean.Ref(propertyEle,field);
                    property.setRef(ref);
                    //如果含有map标签
                } else if(checkTagHasMapField(propertyEle) != null) {
                    Bean.Ref ref = new Bean.Ref(propertyEle,field);
                    property.setRef(ref);
                }

                properties.add(property);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    /**
     * 处理ref标签
     * @param ref
     * @param property
     */
    private void processRefField(Bean.Ref ref, Bean.Property property) {
        String refValue = ref.getRefEle().attributeValue("bean");
        Object object = beanContainer.get(refValue).getInstance();
        property.setObject(object);
    }

    /**
     * 处理list标签，支持内联bean,ref引用，字符串
     * @param ref
     * @param property
     */
    private void processListField(Bean.Ref ref, Bean.Property property) {
        Element listEle = ref.getList();
        List<Element> valueEleList = listEle.elements("value");
        List<Object> propertyObject = new ArrayList<Object>();
        for(Element valueEle : valueEleList) {
            if(StringUtil.isNotEmpty(valueEle.getText())) {
                propertyObject.add(valueEle.getText());
            } else if(checkTagHasRefField(valueEle) != null) {
                Element refEle = valueEle.element("ref");
                String refBeanName = refEle.attributeValue("bean");
                propertyObject.add(beanContainer.get(refBeanName).getInstance());
            } else if(checkTagHasInnerBean(valueEle) != null) {
                Object innerObject = getPropertyInnerBean(valueEle,property);
                propertyObject.add(innerObject);
            }
        }
        property.setObject(propertyObject);
    }

    /**
     * 处理set标签，支持内联bean,ref引用，字符串
     * @param ref
     * @param property
     */
    private void processSetField(Bean.Ref ref, Bean.Property property) {
        Element setEle = ref.getSet();
        List<Element> valueEleList = setEle.elements("value");
        Set<Object> propertyObject = new HashSet<Object>();
        for(Element valueEle : valueEleList) {
            if(StringUtil.isNotEmpty(valueEle.getText())) {
                propertyObject.add(valueEle.getText());
            } else if(checkTagHasRefField(valueEle) != null) {
                Element refEle = valueEle.element("ref");
                String refBeanName = refEle.attributeValue("bean");
                propertyObject.add(beanContainer.get(refBeanName).getInstance());
            } else if(checkTagHasInnerBean(valueEle) != null) {
                Object innerObject = getPropertyInnerBean(valueEle,property);
                propertyObject.add(innerObject);
            }
        }
        property.setObject(propertyObject);
    }

    /**
     * 处理map标签,支持内联bean,ref引用，字符串
     * @param ref
     * @param property
     */
    private void processMapField(Bean.Ref ref, Bean.Property property) {
        Element mapEle = ref.getMap();
        List<Element> entryEleList = mapEle.elements("entry");
        Map<Object,Object> map = new HashMap<Object, Object>();
        for(Element entryEle : entryEleList) {
            Element keyEle = entryEle.element("key");
            Element valueEle = entryEle.element("value");
            if(StringUtil.isNotEmpty(keyEle.getText())) {
                String keyEleText = keyEle.getText();
                if(StringUtil.isNotEmpty(valueEle.getText())) {
                    //key -> String,value -> String
                    String valueEleText = valueEle.getText();
                    map.put(keyEleText,valueEleText);
                } else if(checkTagHasRefField(valueEle) != null) {
                    //key -> String,value -> ref
                    String valueEleRef = valueEle.element("ref").attributeValue("bean");
                    map.put(keyEleText,beanContainer.get(valueEleRef).getInstance());
                } else if(checkTagHasInnerBean(valueEle) != null) {
                    //key -> String,value -> innerBean
                    Object innerObject = getPropertyInnerBean(valueEle,property);
                    map.put(keyEleText,innerObject);
                }
            } else if(checkTagHasRefField(keyEle) != null){
                String keyEleRef = keyEle.attributeValue("bean");
                if(StringUtil.isNotEmpty(valueEle.getText())) {
                    //key -> ref,value -> String
                    String valueEleText = valueEle.getText();
                    map.put(beanContainer.get(keyEleRef).getInstance(),valueEleText);
                } else if(checkTagHasRefField(valueEle) != null) {
                    //key -> ref,value -> ref
                    String valueEleRef = valueEle.element("ref").attributeValue("bean");
                    map.put(beanContainer.get(keyEleRef).getInstance(),beanContainer.get(valueEleRef).getInstance());
                } else if(checkTagHasInnerBean(valueEle) != null) {
                    //key -> ref,value -> innerBean
                    Object innerObject = getPropertyInnerBean(keyEle,property);
                    map.put(beanContainer.get(keyEleRef).getInstance(),innerObject);
                }
            } else if(checkTagHasInnerBean(keyEle) != null) {
                Object innerObject = getPropertyInnerBean(keyEle,property);
                if(StringUtil.isNotEmpty(valueEle.getText())) {
                    //key -> innerBean, value -> String
                    String valueEleText = valueEle.getText();
                    map.put(innerObject,valueEleText);
                } else if(checkTagHasRefField(valueEle) != null) {
                    //key -> innerBean,value -> ref
                    String valueEleRef = valueEle.element("ref").attributeValue("beam");
                    map.put(innerObject,beanContainer.get(valueEleRef).getInstance());
                } else if(checkTagHasInnerBean(valueEle) != null) {
                    //key -> innerBean,value -> innerBean
                    Object valueTagInnerObject = getPropertyInnerBean(valueEle,property);
                    map.put(innerObject,valueTagInnerObject);
                }
            }
        }
        property.setObject(map);
    }

    /**
     * 获取property的内联bean
     * @param propertyEle
     * @param property
     * @return
     */
    private Object getPropertyInnerBean(Element propertyEle, Bean.Property property) {
        if(propertyEle.getName() != "property")
            throw new IllegalArgumentException("argument must be property tag");
        try {
            Element innerBeanEle = propertyEle.element("bean");

            Class<?> innerBeanClass = Class.forName(getTagByProperty(innerBeanEle,"class"));
            Bean propertyInnerBean = new Bean(innerBeanClass,BeanScope.SINGLETON);
            property.setInnerBean(propertyInnerBean);
            List<Bean.Property> innerBeanProperties = setBeanProperty(innerBeanEle,propertyInnerBean);
            //实例化内联bean
            Object innerObject = ReflectionUtil.newInstance(innerBeanClass,innerBeanProperties);
            return innerObject;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取tag内的内容
     * @param tag
     * @return
     */
    private String getTagText(Element tag) {
        return tag.getText();
    }

    /**
     * 获取某个element的property
     * @param ele
     * @param property
     * @return
     */
    private String getTagByProperty(Element ele,String property) {
        return ele.attributeValue(property);
    }

    /**
     * 检查tag是否含有内联bean
     * @param tag
     * @return
     */
    private Element checkTagHasInnerBean(Element tag) {
        return tag.element("bean");
    }

    /**
     * 检查tag是否含有ref属性
     * @param tag
     * @return
     */
    private Element checkTagHasRefField(Element tag) {
        return tag.element("ref");
    }

    /**
     * 检查tag是否含有list标签
     * @param tag
     * @return
     */
    private Element checkTagHasListField(Element tag) {
        return tag.element("list");
    }

    /**
     * 检查tag是否含有set标签
     * @param tag
     * @return
     */
    private Element checkTagHasSetField(Element tag) {
        return tag.element("set");
    }

    /**
     * 检查tag是否含有map标签
     * @param tag
     * @return
     */
    private Element checkTagHasMapField(Element tag) {
        return tag.element("map");
    }

    /**
     * 类型转换
     * xml -> T
     */
    private Object TypeConvert(Field field,String value) {
        field.setAccessible(true);
        //如果字段是int类型
        if(field.getType().getName().equals(Integer.TYPE.toString()) || field.getType().getName().equals(Integer.class.getName())) {
            Integer intValue = Integer.parseInt(value);
            return intValue;
            //如果字段是float类型
        } else if(field.getType().getName().equals(Float.TYPE.toString()) || field.getType().getName().equals(Float.class.getName())) {
            Float floatValue = Float.parseFloat(value);
            return floatValue;
            //如果字段是bool类型
        } else if(field.getType().getName().equals(Boolean.TYPE.toString()) || field.getType().getName().equals(Boolean.class.getName())) {
            if(value.equals("true"))
                return new Boolean(true);
            else
                return new Boolean(false);
            //如果字段是long类型
        } else if(field.getType().getName().equals(Long.TYPE.toString()) || field.getType().getName().equals(Long.class.getName())) {
            Long longValue = Long.parseLong(value);
            return longValue;
            //如果字段是double类型
        } else if(field.getType().getName().equals(Double.TYPE.toString()) || field.getType().getName().equals(Double.class.getName())) {
            Double doubleValue = Double.parseDouble(value);
            return doubleValue;
            //如果字段是String类型
        } else {
            return value;
        }
    }




}
