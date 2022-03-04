package game.components.deck;

import game.components.card.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Deck  implements Serializable {

    List<Card> cards;

    public Deck() {
        cards = new ArrayList<Card>();
    }

    public abstract void init();

    public void shuffle(){
        Collections.shuffle(cards);
    }

    public Card draw(){
        return cards.remove(cards.size() - 1);
    }


}
