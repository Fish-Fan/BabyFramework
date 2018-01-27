import babyframework.util.ClassUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class LoadPropTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("baby.properties");
        Properties prop = new Properties();
        prop.load(is);
        String jdbc_driver = prop.getProperty("baby.framework.jdbc.driver");

        System.out.println(jdbc_driver);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<Class<?>> classSet = ClassUtil.getClassSet("babyframeworktest");
        for(Class<?> clazz : classSet ) {
            System.out.println(clazz);
        }

        String fileName = "hello.class";
        String className = fileName.substring(0,fileName.lastIndexOf("."));
        System.out.println("className is " + className);

        Class clazz = classLoader.loadClass("java.lang.Integer");
        System.out.println(clazz.getName());
    }
}
