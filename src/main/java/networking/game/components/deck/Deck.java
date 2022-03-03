package networking.game.components.deck;

import networking.game.components.card.Card;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Deck {

    ArrayList<Card> cards;

    abstract void init();

    public void shuffle(){
        Collections.shuffle(cards);
    }

    public Card draw(){
        return cards.get(cards.size() - 1);
    }
}
