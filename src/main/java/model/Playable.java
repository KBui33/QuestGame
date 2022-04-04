package model;

import component.card.Card;
import component.card.RankCard;

import java.util.ArrayList;

public interface Playable {

    boolean addCard(Card card);
    boolean addCards(ArrayList<Card> cards);
    Card discardCard(int cardIndex);
    boolean discardCard(Card card);
    boolean discardCards(ArrayList<Card> cards);
    int getPlayerId();
    RankCard getRankCard();
    void incrementShields(int inc);

}
