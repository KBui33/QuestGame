package game.components.card;

import javafx.scene.image.Image;

public class QuestCard extends Card{

    private Integer stages;
    private String foe;

    public QuestCard(String title, String cardImg, Integer stages, String foe) {
        super(title, cardImg);
        this.stages = stages;
        this.foe = foe;
    }
}
