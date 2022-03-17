package component.deck;

import component.card.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Deck implements Serializable {

    protected List<Card>  cards;
    protected List<Card> discarded;

    public Deck() {
        cards = new ArrayList<Card>();
        discarded = new ArrayList<Card>();
    }

    public abstract void init();

    public void shuffle(){
        Collections.shuffle(cards);
    }

    public Card draw(){
        if (cards.size() == 0) {
            cards = new ArrayList<Card>(discarded);
            discarded.clear();
            shuffle();
        }
        return cards.remove(cards.size() - 1);
    }

    public void discard(Card card) {
        discarded.add(0, card);
    }

    public List<Card> getCards() {
        return List.copyOf(cards);
    }

    public List<Card> getDiscarded() {
        return List.copyOf(discarded);
    }

}
