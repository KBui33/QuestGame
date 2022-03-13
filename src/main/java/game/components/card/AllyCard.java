package game.components.card;

public class AllyCard extends Card{
    private String extra;
    private String buff;
    public AllyCard(String title, String cardImg, String extra, String buff) {
        super(title, cardImg);
        this.extra = extra;
        this.buff = buff;
    }
}
