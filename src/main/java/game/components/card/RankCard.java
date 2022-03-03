package game.components.card;

import javafx.scene.image.Image;

public class RankCard extends Card{

    private Integer rank;

    public RankCard(Image cardImg, String title, Integer rank) {
        this.setCardImg(cardImg);
        this.setTitle(title);
        this.rank = rank;
    }
}
