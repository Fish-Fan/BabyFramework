
import babyframework.factory.BeanFactory;
import babyframework.helper.XMLHelper;
import babyframeworktest.pojo.User;
import babyframeworktest.pojo.UserCard;

import java.util.List;

public class LoadXMLTest {
    public static void main(String[] args) {
        long current = System.currentTimeMillis();
//        XMLHelper xmlHelper = new XMLHelper("main/resources/diff.xml");
        //UserCard userCard = (UserCard) xmlHelper.getBeanByID("JacksonUserCard");
        BeanFactory factory = new BeanFactory("main/resources/diff.xml");
        User user = (User) factory.getBeanByID("user");
        User user1 = (User) factory.getBeanByID("user");
        User user2 = factory.getBeanByClass(User.class);
        System.out.println("user.hashcode : " + user.hashCode());
        System.out.println("user1.hashcode : " + user1.hashCode());
        System.out.println(user2);
        long time = System.currentTimeMillis() - current;
        System.out.println("共耗时: " + time + " ms");
    }
}
