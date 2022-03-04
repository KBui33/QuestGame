package game.components.card;


import javafx.scene.image.Image;

public class EventCard extends Card{
    public EventCard(Image cardImg, String title, String desc) {
        this.setCardImg(cardImg);
        this.setTitle(title);
        this.setDescription(desc);
    }

}
