package networking.game.components;

import java.awt.image.BufferedImage;

public abstract class Card {
    private String type;
    private Integer dmg;
    private String stages;
    private BufferedImage cardImg;

    abstract void prepare();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDmg() {
        return dmg;
    }

    public void setDmg(Integer dmg) {
        this.dmg = dmg;
    }

    public String getStages() {
        return stages;
    }

    public void setStages(String stages) {
        this.stages = stages;
    }

    public BufferedImage getCardImg() {
        return cardImg;
    }

    public void setCardImg(BufferedImage cardImg) {
        this.cardImg = cardImg;
    }
}
