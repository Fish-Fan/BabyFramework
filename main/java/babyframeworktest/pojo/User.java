package babyframeworktest.pojo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class User {
    private String username;
    private int age;
    private Map<String,UserCard> userCard;
    private Set<String> nickName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Map<String, UserCard> getUserCard() {
        return userCard;
    }

    public void setUserCard(Map<String, UserCard> userCard) {
        this.userCard = userCard;
    }

    public Set<String> getNickName() {
        return nickName;
    }

    public void setNickName(Set<String> nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", age=" + age +
                ", userCard=" + userCard.size() +
                ", nickName=" + nickName +
                '}';
    }
}
