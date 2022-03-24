package model;

import component.card.Card;
import component.card.Rank;
import component.card.RankCard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private int playerId;
    private List<Card> cards;
    private RankCard rankCard;
    private int shields;

    public Player() {
        cards = new ArrayList<Card>();
        rankCard = new RankCard("", "", Rank.SQUIRE);
        shields = 0;
    }

    public Player(int playerId) {
        this();
        this.playerId = playerId;
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public void addCards(ArrayList<Card> cards) {
        this.cards.addAll(cards);
    }

    public Card discardCard(int cardIndex) {
        return this.cards.remove(cardIndex);
    }

    public boolean discardCard(Card card) {
        return this.cards.remove(card);
    }

    public boolean discardCards(ArrayList<Card> cards) {
        return this.cards.removeAll(cards);
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

    public Rank getRank() { return rankCard.getRank(); }

    public int getShields() {
        return shields;
    }

    public void setShields(int shields) {
        this.shields = shields;
        this.incrementRank();
    }

    public void incrementShields(int inc) {
        shields += inc;
        this.incrementRank();
    }

    public void incrementRank() {
        Rank currentRank = rankCard.getRank();
        boolean shouldIncrementRank = false;
        switch (currentRank) {
            case SQUIRE: {
                if(this.shields >= 5) {
                    shouldIncrementRank = true;
                    this.shields -= 5;
                }
                break;
            } case KNIGHT: {
                if(this.shields >= 7) {
                    shouldIncrementRank = true;
                    this.shields -= 7;
                }
                break;
            } case CHAMPION_KNIGHT: {
                if(this.shields >= 10) {
                    shouldIncrementRank = true;
                    this.shields -= 10;
                }
                break;
            }
        }

        if(shouldIncrementRank) this.rankCard.setRank(RankCard.getNextRank(currentRank));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Player)) return false;
        Player p = (Player) o;
        return p.playerId == this.playerId;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", cards=" + cards +
                '}';
    }
}
