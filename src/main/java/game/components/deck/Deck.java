package game.components.deck;

import game.components.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Deck {

    List<Card> cards;

    public Deck() {
        cards = new ArrayList<Card>();
    }

    abstract void init();

    public void shuffle(){
        Collections.shuffle(cards);
    }

    public Card draw(){
        return cards.get(cards.size() - 1);
    }
}
