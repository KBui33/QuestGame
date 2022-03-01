package networking.game.components.card;

import javafx.scene.image.Image;

import java.util.Map;

public class FoeCard extends Card{
    public FoeCard(Image cardImg, String title, Map<Integer, Integer> dmg) {
        this.setCardImg(cardImg);
        this.setTitle(title);
        this.setDmg(dmg);
    }
}
