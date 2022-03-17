package model;

import game.components.card.Card;
import game.components.card.Rank;
import game.components.card.RankCard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private int playerId;
    private List<Card> cards;
    private RankCard rankCard;
    private int shields;

    // -- TODO:: Add cards array list
    // -- TODO:: Add weapons array list

    public Player() {
        cards = new ArrayList<Card>();
        rankCard = new RankCard("", "", Rank.SQUIRE);
        shields = rankCard.getShields();
    }

    public Player(int playerId) {
        this();
        this.playerId = playerId;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void addCards(ArrayList<Card> cards) { cards.addAll(cards); }

    public Card discardCard(int cardIndex) {
        return cards.remove(cardIndex);
    }

    public boolean discardCard(Card card) {
        return cards.remove(card);
    }

    public boolean discardCards(ArrayList<Card> cards) {
        return cards.removeAll(cards);
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setRankCard(RankCard rankCard) {
        this.rankCard = rankCard;
    }

    public RankCard getRankCard() {
        return rankCard;
    }

    public int getShields() {
        return shields;
    }

    public void setShields(int shields) {
        this.shields = shields;
    }

    public void incrementShields(int inc) {
        shields += inc;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", cards=" + cards +
                '}';
    }
}
