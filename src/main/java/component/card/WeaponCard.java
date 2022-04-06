package component.card;

public class WeaponCard extends Card implements Battleable{
    private Integer battlePoints;

    public WeaponCard(String title, String cardImg, Integer battlePoints) {
        super(title, cardImg);
        this.battlePoints = battlePoints;
    }

    public int getBattlePoints() {
        return battlePoints;
    }
}
