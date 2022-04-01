package model;

import component.card.Card;
import component.card.RankCard;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class PlayerDecorator implements Playable, Serializable {
    protected Player player;
    protected ArrayList<Card> cardsUsed;

    public PlayerDecorator() {
        this.cardsUsed = new ArrayList<>();
    }

    public PlayerDecorator(Player player) {
        this();
        this.player = player;
    }

    @Override
    public boolean addCard(Card card) {
        return this.player.addCard(card);
    }

    @Override
    public boolean addCards(ArrayList<Card> cards) {
        return this.player.addCards(cards);
    }

    @Override
    public Card discardCard(int cardIndex) {
        return this.player.discardCard(cardIndex);
    }

    @Override
    public boolean discardCard(Card card) {
        return this.player.discardCard(card);
    }

    @Override
    public boolean discardCards(ArrayList<Card> cards) {
        return this.player.discardCards(cards);
    }

    @Override
    public int getPlayerId() {
        return this.player.getPlayerId();
    }

    @Override
    public RankCard getRankCard() {
        return this.player.getRankCard();
    }

    @Override
    public void incrementShields(int inc) {
        this.player.incrementShields(inc);
    }

    public abstract int calculateBattlePoints();

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setCardsUsed(ArrayList<Card> cardsUsed) {
        this.cardsUsed = new ArrayList<>(cardsUsed);
    }

    public ArrayList<Card> getCardsUsed() {
        return cardsUsed;
    }

    public void resetCardsUsed() {
        cardsUsed.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (o.getClass() != getClass()) return false;
        PlayerDecorator p = (PlayerDecorator) o;
        return p.player.equals(this.player);
    }

}
