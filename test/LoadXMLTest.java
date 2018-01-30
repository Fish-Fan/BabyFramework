
import babyframework.factory.BeanFactory;
import babyframework.helper.XMLHelper;
import babyframeworktest.pojo.User;

import java.util.List;

public class LoadXMLTest {
    public static void main(String[] args) {
        long current = System.currentTimeMillis();
        BeanFactory factory = new BeanFactory("main/resources/diff.xml");
        List<User> userList = factory.getBeanByClass(User.class);
        for(User user : userList) {
            System.out.println(user);
        }
        long time = System.currentTimeMillis() - current;
        System.out.println("共耗时: " + time + " ms");
    }
}
