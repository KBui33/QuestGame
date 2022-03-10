package game.components.card;

public class WeaponCard extends Card{
    private Integer battlePoints;

    public WeaponCard(String title, String cardImg, Integer battlePoints) {
        super(title, cardImg);
        this.battlePoints = battlePoints;
    }

    public Integer getBattlePoints() {
        return battlePoints;
    }
}
