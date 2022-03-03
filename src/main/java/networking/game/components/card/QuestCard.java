package networking.game.components.card;

import javafx.scene.image.Image;

public class QuestCard extends Card{

    private Integer stages;
    private String foe;

    public QuestCard(Image cardImg, String title, Integer stages, String foe) {
        this.setCardImg(cardImg);
        this.setTitle(title);
        this.stages = stages;
        this.foe = foe;
    }
}
