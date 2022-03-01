package networking.game.components.card;

import javafx.scene.image.Image;

import java.util.Map;

public abstract class Card {

    private String title;
    private String stages;
    private Map<Integer, Integer> dmg;
    private Image cardImg;

//    abstract void prepare();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStages() {
        return stages;
    }

    public void setStages(String stages) {
        this.stages = stages;
    }

    public Map<Integer, Integer> getDmg() {
        return dmg;
    }

    public void setDmg(Map<Integer, Integer> dmg) {
        this.dmg = dmg;
    }

    public Image getCardImg() {
        return cardImg;
    }

    public void setCardImg(Image cardImg) {
        this.cardImg = cardImg;
    }
}
