package component.card;

public class AllyCard extends Card implements Battleable{
    private String extra;
    private String buff;
    public AllyCard(String title, String cardImg, String extra, String buff) {
        super(title, cardImg);
        this.extra = extra;
        this.buff = buff;
    }

    @Override
    public int getBattlePoints() {
        return Integer.parseInt(this.buff);
    }
}
