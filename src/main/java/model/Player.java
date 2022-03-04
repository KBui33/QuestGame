package model;

import game.components.card.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private int playerId;
    private List<Card> cards;

    // -- TODO:: Add cards array list
    // -- TODO:: Add weapons array list

    public Player() {
        cards = new ArrayList<Card>();
    }

    public Player(int playerId) {
        this();
        this.playerId = playerId;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public Card discardCard(int cardIndex) {
        return cards.remove(cardIndex);
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", cards=" + cards +
                '}';
    }
}
