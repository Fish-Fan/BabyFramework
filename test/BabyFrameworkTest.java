import babyframework.helper.BeanHelper;
import babyframework.helper.IocHelper;
import babyframeworktest.UserController;

public class BabyFrameworkTest {
    public static void main(String[] args) {
        IocHelper iocHelper = new IocHelper();
        UserController userController = BeanHelper.getBean(UserController.class);
        userController.printUser();
    }
}
