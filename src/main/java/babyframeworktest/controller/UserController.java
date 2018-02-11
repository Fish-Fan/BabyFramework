package babyframeworktest.controller;

import babyframework.annotation.Action;
import babyframework.annotation.Controller;
import babyframework.bean.Data;
import babyframework.bean.Param;
import babyframework.bean.View;
import babyframework.util.JsonUtil;
import babyframeworktest.pojo.User;

import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    @Action("get:/hello")
    public void hello() {
        System.out.println("hello");
    }

    @Action("get:/user")
    public Data getUser(Param param) {
        Map<String,Object> map = param.getParamMap();
        String username = (String) map.get("username");
        int age = Integer.parseInt((String) map.get("age"));
        User user = new User();
        user.setUsername(username);
        user.setAge(age);
        Data data = new Data(JsonUtil.toJson(user));
        return data;
    }

    @Action("post:/user")
    public View getUserPost(Param param) {
        Map<String,Object> map = param.getParamMap();
        String username = (String) map.get("username");
        int age = Integer.parseInt((String) map.get("age"));
        User user = new User();
        user.setUsername(username);
        user.setAge(age);
        System.out.println(user.getUsername());
        System.out.println(user.getAge());
        View view = new View("user");
        view.addModel("username",username);
        view.addModel("age",age);
        return view;
    }

    @Action("get:/register")
    public View registerVIP() {
        View view = new View("register");
        return view;
    }

    @Action("post:/register")
    public View afterRegister(Param param) {
        View view = new View("afterRegister");
        Map<String,Object> map = param.getParamMap();
        String username = (String) map.get("username");
        int age = Integer.parseInt((String) map.get("age"));

        User user = new User();
        user.setUsername(username);
        user.setAge(age);

        view.addModel("user",user);
        return view;
    }
}
