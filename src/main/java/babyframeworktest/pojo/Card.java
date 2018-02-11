package babyframeworktest.pojo;

public class Card {
    private String material;

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    @Override
    public String toString() {
        return "Card{" +
                "material='" + material + '\'' +
                '}';
    }
}
