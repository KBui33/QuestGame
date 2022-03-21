package component.card;

public class TestCard extends Card{
    private String description;

    public TestCard(String title, String cardImg, String description) {
        super(title, cardImg);
        this.description = description;
    }
}
