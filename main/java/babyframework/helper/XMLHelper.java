package babyframework.helper;

import babyframework.factory.Bean;
import babyframework.factory.BeanScope;
import babyframework.util.ReflectionUtil;
import babyframework.util.StringUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
    private static Map<String,Bean> beanContainer = new HashMap<String, Bean>();
    //保存bean的ref属性断点信息
    private static Map<Object,RefMessage> breakPointRefMessageContainer = new HashMap<Object, RefMessage>();
    //保存Map的断点信息
    private static Map<Object,MapFieldMessage> mapBreakPointRefMessageContainer = new HashMap<Object, MapFieldMessage>();
    public XMLHelper(String XMLFileName) {
        initBeanMap(initXMLReader(XMLFileName));
        injectBeanFromRefField();
        injectMapRefBean();
        clean();
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
            try {
                String beanID = bean.attributeValue("id");
                T o = (T) setBeanProperties(bean);
                Class<?> cls = Class.forName(bean.attributeValue("class"));

                String beanScope = bean.attributeValue("scope");
                BeanScope scope = BeanScope.SINGLETON;
                if(StringUtil.isNotEmpty(beanScope) && beanScope.equals("prototype")) {
                    scope = BeanScope.PROTOTYPE;
                }

                beanContainer.put(beanID,new Bean(cls,beanID,scope,o));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 清理breakPointRefMessageContainer,mapBreakPointRefMessageContainer
     */
    private void clean() {
        //Help GC
        breakPointRefMessageContainer = null;
        mapBreakPointRefMessageContainer = null;
    }

    /**
     * 获取从xml中读取的bean
     * @return
     */
    public Map<String,Bean> getXMLBeanContainer() {
        return beanContainer;
    }

    /**
     * 根据ID获取bean
     * 如果两个bean的ID冲突，则按照在XML中定义的顺序返回
     * 最后一个以此ID定义的bean
     */
    @SuppressWarnings("unchecked")
    public Object getBeanByID(String beanID) {
        return beanContainer.get(beanID);
    }

    /**
     * 根据class获取相同类型的bean
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getBeansByType(Class<?> clazz) {
        List<T> beanList = new ArrayList<T>();
        for(Map.Entry<String,Bean> entry : beanContainer.entrySet()) {
            Bean bean = entry.getValue();
            T o = (T) bean.object;
            Class oClass = o.getClass();
            if(oClass.getName().equals(clazz.getName())) {
                beanList.add(o);
            }
        }
        return beanList;
    }

    /**
     * 通过字段注入初始化bean中的属性
     * 如果有内联bean，初始化并注入
     */
    @SuppressWarnings("unchecked")
    private Object setBeanProperties(Element bean) {
        Object object = null;
        String clazz = bean.attributeValue("class");
        try {
            Class<?> cls = Class.forName(clazz);
            object = ReflectionUtil.newInstance(cls);
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
                } else if(getTagRefField(property) != null) {
                    List<String> refFieldValue = new ArrayList<String>();
                    refFieldValue.add(getTagRefField(property));
                    breakPointRefMessageContainer.put(object,new RefMessage(field,refFieldValue));
                //检查是否有list属性
                } else if(propertyMessage.list.size() > 0) {
                    resolveListField(propertyMessage,field,object);
                //检查是否有set属性
                } else if(propertyMessage.set.size() > 0) {
                    resolveSetField(propertyMessage,field,object);
                //检查是否有map属性
                } else if(propertyMessage.map.size() > 0) {
                    resolveMapField(propertyMessage,field,object);
                }
                else {
                    setFieldValue(object,field,property.attributeValue("value"));
                }


            }
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
     * 获取该tag中的ref属性
     */
    private String getTagRefField(Element tag) {
        return tag.attributeValue("ref");
    }

    /**
     * 尝试直接读取<value>xxx<value/>
     * 检查整个tagList是否含有value
     * 如果有一个含有返回true
     */
    private boolean checkTagListHasText(List<Element> valueTagList) {
        boolean result = false;
        for(Element valueTag : valueTagList) {
            if(StringUtil.isNotEmpty(valueTag.getText())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 尝试直接读取<value>xxx<value/>
     * 检查单个tag是否含有text
     * 如果有值返回true
     */
    private boolean checkTagHasText(Element tag) {
        return StringUtil.isNotEmpty(tag.getText()) ? true : false;
    }

    /**
     * 检查tag是否含有内联bean
     */
    private boolean checkTagHasInnerBean(Element tag) {
        return tag.element("bean") != null ? true : false;
    }

    /**
     * 将ref字段所依赖的bean注入
     */
    private void injectBeanFromRefField() {
        for(Map.Entry<Object,RefMessage> entry : breakPointRefMessageContainer.entrySet()) {
            Object o = entry.getKey();
            RefMessage refMessage = entry.getValue();
            Field field = refMessage.field;
            field.setAccessible(true);
            //获取field的类型
            Type fieldType = field.getGenericType();

            try {
                //如果是集合类型
                if(fieldType instanceof ParameterizedType) {
                    Object refValueObject = refMessage.refValue;

                    if(refValueObject instanceof List) {
                        List<String> refValueList = (List<String>) refValueObject;
                        List<Object> refBeanList = new ArrayList<Object>();
                        for(String refValue : refValueList) {
                            Object bean = beanContainer.get(refValue);
                            refBeanList.add(bean);
                        }
                        field.set(o,refBeanList);
                    } else if(refValueObject instanceof Set) {
                        Set<String> refValueList = (Set<String>) refValueObject;
                        Set<Object> refBeanList = new HashSet<Object>();
                        for(String refValue : refValueList) {
                            Object bean = beanContainer.get(refValue);
                            refBeanList.add(bean);
                        }
                        field.set(o,refBeanList);
                    }
                //如果是普通类型
                } else if(fieldType instanceof Class) {
                    field.set(o,beanContainer.get(refMessage.refValue.toString()));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将map类型的属性所依赖的bean注入
     */
    private void injectMapRefBean() {
        for(Map.Entry<Object,MapFieldMessage> entry : mapBreakPointRefMessageContainer.entrySet()) {
            Object o = entry.getKey();
            MapFieldMessage mapFieldMessage = entry.getValue();
            Field field = mapFieldMessage.field;
            field.setAccessible(true);
            List<MapRefMessage> mapRefMessageList = mapFieldMessage.mapRefMessages;

            Map<Object,Object> map = new HashMap<Object, Object>();

            for(MapRefMessage message : mapRefMessageList) {
                if(message.code == 3) {
                    map.put(beanContainer.get(message.key),beanContainer.get(message.value));
                } else if(message.code == 2) {
                    map.put(message.key,beanContainer.get(message.value));
                } else {
                    map.put(beanContainer.get(message.key),message.value);
                }
            }

            try {
                Map<Object,Object> tempMap = (Map<Object, Object>) field.get(o);
                tempMap.putAll(map);
                field.set(o,tempMap);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解析list类型的字段
     */
    @SuppressWarnings("unchecked")
    private void resolveListField(PropertyMessage propertyMessage,Field field,Object object) throws IllegalAccessException {
        Element listTag = propertyMessage.list.get(0);
        List<Element> valueTagList = listTag.elements("value");

        if(checkTagListHasText(valueTagList)) {
            List<String> valueList = new ArrayList<String>();
            for(Element valueTag : valueTagList) {
                valueList.add(valueTag.getText());
            }
            field.set(object,valueList);
        } else {
            List<String> refBeanList = new ArrayList<String>();
            RefMessage listRefMessage = new RefMessage(field,refBeanList);

            for(Element valueTag : valueTagList) {
                refBeanList.add(getTagRefField(valueTag));
            }
            listRefMessage.refValue = refBeanList;
            breakPointRefMessageContainer.put(object,listRefMessage);
        }
    }

    /**
     * 解析set类型字段
     */
    private void resolveSetField(PropertyMessage propertyMessage,Field field,Object object) throws IllegalAccessException {
        Element listTag = propertyMessage.set.get(0);
        List<Element> valueTagList = listTag.elements("value");

        if(checkTagListHasText(valueTagList)) {
            Set<String> valueSet = new HashSet<String>();
            for(Element valueTag : valueTagList) {
                valueSet.add(valueTag.getText());
            }
            field.set(object,valueSet);
        } else {
            Set<String> refBeanSet = new HashSet<String>();

            for(Element valueTag : valueTagList) {
                refBeanSet.add(getTagRefField(valueTag));
            }
            RefMessage listRefMessage = new RefMessage(field,refBeanSet);
            listRefMessage.refValue = refBeanSet;
            breakPointRefMessageContainer.put(object,listRefMessage);
        }
    }

    /**
     * 解析map类型字段
     */
    @SuppressWarnings("unchecked")
    private void resolveMapField(PropertyMessage propertyMessage,Field field,Object object) throws IllegalAccessException {
        Element mapTag = propertyMessage.map.get(0);
        //用来存储不含ref属性的entry
        Map<Object,Object> map = new HashMap<Object, Object>();
        List<Element> entryTagList = mapTag.elements("entry");
        //存放含有任意ref属性的数据结构
        List<MapRefMessage> mapRefMessages = new ArrayList<MapRefMessage>();
        for(Element entryTag : entryTagList) {
            Element keyTag = entryTag.element("key");
            Element valueTag = entryTag.element("value");

            int flag = 0;

            if(StringUtil.isNotEmpty(getTagRefField(keyTag))) {
                //key->ref,value->ref
                if(StringUtil.isNotEmpty(getTagRefField(valueTag))) {
                    flag = 3;
                    mapRefMessages.add(new MapRefMessage(flag,getTagRefField(keyTag),getTagRefField(valueTag)));
                //key->ref,value->String
                } else if(checkTagHasText(valueTag)) {
                    flag = 1;
                    mapRefMessages.add(new MapRefMessage(flag,getTagRefField(keyTag),valueTag.getText()));
                //key->ref,value->innerBean
                } else {
                    flag = 1;
                    Object valueTagInnerBean = setBeanProperties(valueTag.element("bean"));
                    mapRefMessages.add(new MapRefMessage(flag,getTagRefField(keyTag),valueTagInnerBean));
                }
            } else if(checkTagHasText(keyTag)) {
                //key->String,value->ref
                if(StringUtil.isNotEmpty(getTagRefField(valueTag))) {
                    flag = 2;
                    mapRefMessages.add(new MapRefMessage(flag,keyTag.getText(),getTagRefField(valueTag)));
                //key->String,value->String
                } else if(checkTagHasText(valueTag)) {
                    map.put(keyTag.getText(),valueTag.getText());
                //key->String,value->innerBean
                } else {
                    Object valueTagInnerBean = setBeanProperties(valueTag.element("bean"));
                    map.put(keyTag.getText(),valueTagInnerBean);
                }
            } else {
                //key->innerBean,value->ref
                if(StringUtil.isNotEmpty(getTagRefField(valueTag))) {
                    flag = 2;
                    Object keyTagInnerBean = setBeanProperties(keyTag.element("bean"));
                    mapRefMessages.add(new MapRefMessage(flag,keyTagInnerBean,getTagRefField(valueTag)));
                //key->innerBean,value->String
                } else if(checkTagHasText(valueTag)) {
                    Object keyTagInnerBean = setBeanProperties(keyTag.element("bean"));
                    map.put(keyTagInnerBean,valueTag.getText());
                //key->innerBean,value->innerBean
                } else {
                    Object keyTagInnerBean = setBeanProperties(keyTag.element("bean"));
                    Object valueTagInnerBean = setBeanProperties(valueTag.element("bean"));
                    map.put(keyTagInnerBean,valueTagInnerBean);
                }
            }

        }
        field.set(object,map);
        mapBreakPointRefMessageContainer.put(object,new MapFieldMessage(field,mapRefMessages));
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
        private Object refValue;

        public RefMessage(Field field, Object refValue) {
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
        private List<Element> list;
        private List<Element> set;
        private List<Element> map;

        public PropertyMessage(Element property,Field field) {
            this.property = property;
            this.innerBeans = property.elements("bean");
            this.list = property.elements("list");
            this.set = property.elements("set");
            this.map = property.elements("map");
        }
    }

    /**
     * 用来存放xml中每个entry的信息
     */
    private static class MapRefMessage {
        /**
         * 如果key和value都有ref,值为3
         * 如果key有ref,value没有ref,值为1
         * 如果key没有ref,value有ref,值为2
         */
        private int code;
        private Object key;
        private Object value;

        public MapRefMessage(int code,Object key,Object value) {
            this.code = code;
            this.key = key;
            this.value = value;
        }
    }

    private static class MapFieldMessage {
        //属性类型为map的字段
        private Field field;
        //保存该字段map信息的集合
        private List<MapRefMessage> mapRefMessages;

        public MapFieldMessage(Field field,List<MapRefMessage> mapRefMessages) {
            this.field = field;
            this.mapRefMessages = mapRefMessages;
        }
    }
}
