package babyframework.factory.xml;

import org.dom4j.Element;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class Bean {
    private Class<?> cls;
    private String name;
    private BeanScope scope;
    private List<Property> properties;
    private Element bean;
    private Object instance;
    private Method[] methods;
    private Ref ref;

    public Bean() {

    }

    public Bean(Class<?> cls, BeanScope scope) {
        this.cls = cls;
        this.scope = scope;
    }

    public Bean(Class<?> cls, String name, BeanScope scope) {
        this.cls = cls;
        this.name = name;
        this.scope = scope;
    }

    public Bean(Class<?> cls,String name,BeanScope scope,Object instance) {
        this.cls = cls;
        this.name = name;
        this.scope = scope;
        this.instance = instance;
    }

    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BeanScope getScope() {
        return scope;
    }

    public void setScope(BeanScope scope) {
        this.scope = scope;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public Element getBean() {
        return bean;
    }

    public void setBean(Element bean) {
        this.bean = bean;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method[] getMethods() {
        return methods;
    }

    public void setMethods(Method[] methods) {
        this.methods = methods;
    }

    /**
     * 存放xml中property标签信息
     */
     public static class Property<T> {
        private Class<?> cls;
        private T object;
        private String name;
        private String value;
        private Ref ref;
        private Element property;
        private Bean innerBean;

        public Property() {
        }

        public Property(String name, Element property,Class<?> cls) {
            this.name = name;
            this.property = property;
            this.cls = cls;
        }

        public Property(String name, String value, Element property) {
            this.name = name;
            this.value = value;
            this.property = property;
        }

        public Property(String name, String value, Element property,Class<?> cls) {
            this.name = name;
            this.value = value;
            this.property = property;
            this.cls = cls;
        }

        public Class<?> getCls() {
            return cls;
        }

        public void setCls(Class<?> cls) {
            this.cls = cls;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Ref getRef() {
            return ref;
        }

        public void setRef(Ref ref) {
            this.ref = ref;
        }

        public Element getProperty() {
            return property;
        }

        public void setProperty(Element property) {
            this.property = property;
        }

        public Bean getInnerBean() {
            return innerBean;
        }

        public void setInnerBean(Bean innerBean) {
            this.innerBean = innerBean;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(T object) {
            this.object = object;
        }
    }

    /**
     * 存放xml中ref属性信息
     */
    public static class Ref {
        private Element property;
        private Field field;
        private Element refEle;
        private Element list;
        private Element set;
        private Element map;

        public Ref(Element property,Field field) {
            this.property = property;
            this.refEle = property.element("ref");
            this.field = field;
            this.list = property.element("list");
            this.set = property.element("set");
            this.map = property.element("map");
        }

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        public Element getRefEle() {
            return refEle;
        }

        public void setRefEle(Element refEle) {
            this.refEle = refEle;
        }



        public Element getProperty() {
            return property;
        }

        public void setProperty(Element property) {
            this.property = property;
        }

        public Element getList() {
            return list;
        }

        public void setList(Element list) {
            this.list = list;
        }

        public Element getSet() {
            return set;
        }

        public void setSet(Element set) {
            this.set = set;
        }

        public Element getMap() {
            return map;
        }

        public void setMap(Element map) {
            this.map = map;
        }
    }

}
