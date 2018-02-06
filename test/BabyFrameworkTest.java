import babyframework.helper.XMLHelper;
import babyframeworktest.pojo.User;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class BabyFrameworkTest {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        System.out.println(Integer.class.getName());

        User user = new User();
        Class<?> cls = user.getClass();

        Field ageField = cls.getDeclaredField("age");
        System.out.println("field age : " + ageField.getType().getName());


        Field field = cls.getDeclaredField("nickName");
        ParameterizedType type = (ParameterizedType) field.getGenericType();
        Type[] types = type.getActualTypeArguments();

        for(Type item : types) {
            Class innerClass = item.getClass();
            System.out.println(innerClass);
        }



    }
}
