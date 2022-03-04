package game.components.card;

import javafx.scene.image.Image;

import java.util.Map;

public class FoeCard extends Card{

    private String extra; // Special description for foe (See for_11.png)
    private Map<Integer, Integer> dmg;

    public FoeCard(String title, String cardImg, String spDesc, Map<Integer, Integer> dmg) {
        super(title, cardImg);
        this.dmg = dmg;
        this.extra = spDesc;
    }
}
