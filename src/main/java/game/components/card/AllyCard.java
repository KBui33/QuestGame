package game.components.card;

public class AllyCard extends Card{
    private String description;
    public AllyCard( String title, String cardImg, String desc) {
        super(title, cardImg);
        this.description = desc;
    }
}
