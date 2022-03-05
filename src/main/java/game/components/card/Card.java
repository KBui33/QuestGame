package game.components.card;

import javafx.scene.image.Image;

import java.io.Serializable;
import java.util.Map;

public abstract class Card implements Serializable {

    private String title;
    private String cardImg;

    public Card(String title, String cardImg) {
        this.title = title;
        this.cardImg = cardImg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCardImg() {
        return cardImg;
    }

    public void setCardImg(String cardImg) {
        this.cardImg = cardImg;
    }

}
