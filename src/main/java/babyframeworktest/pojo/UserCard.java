package babyframeworktest.pojo;

public class UserCard {
    private Card card;
    private Long id;
    private String gov;

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGov() {
        return gov;
    }

    public void setGov(String gov) {
        this.gov = gov;
    }

    @Override
    public String toString() {
        return "UserCard{" +
                "card=" + card +
                ", id=" + id +
                ", gov='" + gov + '\'' +
                '}';
    }
}
