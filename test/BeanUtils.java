import babyframeworktest.pojo.User;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanUtils {
    public static void main(String[] args) throws IntrospectionException, ClassNotFoundException, NoSuchFieldException {
        Class cls = Class.forName("babyframeworktest.pojo.User");
        Field field = cls.getDeclaredField("age");
        System.out.println(field.getType().getName());

        System.out.println(Integer.TYPE);

        System.out.println(field.getType().getName().equals(Integer.TYPE.toString()));
    }
}
