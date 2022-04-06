package component.card;

import java.io.Serializable;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class Card implements Serializable {

    private static int cardCount = 0;
    private int id;
    private String title;
    private String cardImg;

    public Card(String title, String cardImg) {
        this.id = cardCount++;
        this.title = title;
        this.cardImg = cardImg;
    }

    public int getId() {
        return id;
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

    @Override
    public boolean equals(Object o){
        if(o == null) return false;
        if(o == this) return true;
        if(!(o instanceof Card)) return false;
        Card c = (Card) o;
        return c.cardImg.equals(this.cardImg) && c.title.equals(this.title) && c.id == this.id;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(cardImg).append(title).toHashCode();
    }
}
