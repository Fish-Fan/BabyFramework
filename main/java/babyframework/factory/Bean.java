package babyframework.factory;

public class Bean {
    public Class<?> cls;
    public String name;
    public BeanScope scope;
    public Object object;

    public Bean(Class<?> cls, String name, BeanScope scope,Object object) {
        this.cls = cls;
        this.name = name;
        this.scope = scope;
        this.object = object;
    }
}
