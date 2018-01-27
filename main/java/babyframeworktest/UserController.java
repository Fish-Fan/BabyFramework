package babyframeworktest;

import babyframework.annotation.Controller;
import babyframework.annotation.Inject;
import babyframeworktest.pojo.User;

@Controller
public class UserController {
    @Inject
    private User user;

    public void printUser() {
        user.setUsername("tom");
        user.setAge(20);
        System.out.println(user);
    }
}
