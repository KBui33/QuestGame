package component.card;

import java.io.Serializable;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o){
        if(o == null) return false;
        if(o == this) return true;
        if(!(o instanceof Card)) return false;
        Card c = (Card) o;
        return c.cardImg.equals(this.cardImg) && c.title.equals(this.title);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(cardImg).append(title).toHashCode();
    }

}
