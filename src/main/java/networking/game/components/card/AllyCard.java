package networking.game.components.card;

import javafx.scene.image.Image;

public class AllyCard extends Card{
    public AllyCard(Image cardImg, String title, String desc) {
        this.setCardImg(cardImg);
        this.setTitle(title);
        this.setDescription(desc);
    }
}
