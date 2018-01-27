
import babyframework.helper.XMLHelper;
import babyframeworktest.pojo.User;

import java.util.List;

public class LoadXMLTest {
    public static void main(String[] args) {
        long current = System.currentTimeMillis();
        XMLHelper xmlHelper = new XMLHelper("main/resources/diff.xml");

        List<User> user = xmlHelper.getBeansByType(User.class);
        System.out.println(user);
        long time = System.currentTimeMillis() - current;
        System.out.println("共耗时: " + time + " ms");
    }
}
