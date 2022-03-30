package component.deck;

import component.card.Card;
import component.card.EventCard;
import model.Event;

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

//        //Testing
//        if(!(cards.get(0).getTitle().equals("Queen's Favor")) & cards.get(0) instanceof EventCard){
//            EventCard queen = (EventCard) cards.stream()
//                    .filter(card -> "Queen's Favor".equals(card.getTitle()))
//                    .findFirst()
//                    .orElse(null);
//
//            int queenIndex = cards.indexOf(queen);
//
//            Card temp = cards.get(0);
//
//            cards.set(0, queen);
//            cards.set(queenIndex - 1, temp);
//        }
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

    public void clear() {
        this.cards.clear();
        this.discarded.clear();
    }

}
