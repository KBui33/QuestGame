package game.components.card;

import javafx.scene.image.Image;

public class RankCard extends Card{

    private Integer rank;

    public RankCard(String title, String cardImg, Integer rank) {
        super(title, cardImg);
        this.rank = rank;
    }
}
