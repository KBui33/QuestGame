package game.components.card;

import javafx.scene.image.Image;

import java.util.Map;

public abstract class Card {

    private String title;
    private String description;
    private Image cardImg;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Image getCardImg() {
        return cardImg;
    }

    public void setCardImg(Image cardImg) {
        this.cardImg = cardImg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
