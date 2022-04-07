package model;

import component.card.AllyCard;
import component.card.Card;
import component.card.Rank;
import component.card.RankCard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Player implements Playable, Serializable, Comparable {
    private int playerId;
    private int playerNumber;
    private final List<Card> cards;
    private RankCard rankCard;
    private int shields;

    public Player() {
        cards = new ArrayList<>();
        rankCard = new RankCard(Rank.SQUIRE);
        shields = 0;
    }

    public Player(int playerId) {
        this();
        this.playerId = playerId;
    }

    public boolean addCard(Card card) {
        return this.cards.add(card);
    }

    public boolean addCards(ArrayList<Card> cards) {
        return this.cards.addAll(cards);
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

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
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

    public int getBattlePoints() { return rankCard.getBattlePoints() + handBattlePoints(); }

    public void setShields(int shields) {
        this.shields = shields;
        this.incrementRank();
    }

    public void incrementShields(int inc) {
        System.out.println("== Before: " + shields + " Received: " + inc);
        shields += inc;
        this.incrementRank();
        System.out.println("== After: " + shields);
    }

    public void decrementShields(int dec){
        System.out.println("== Before: " + shields + " Received: " + dec);
        shields -= dec;
        this.incrementRank();
        System.out.println("== After: " + shields);
    }

    public void incrementRank() {
        Rank currentRank = rankCard.getRank();
        boolean shouldIncrementRank = false;
        switch (currentRank) {
            case SQUIRE -> {
                if (this.shields >= 5) {
                    shouldIncrementRank = true;
                    this.shields -= 5;
                }
            }
            case KNIGHT -> {
                if (this.shields >= 7) {
                    shouldIncrementRank = true;
                    this.shields -= 7;
                }
            }
            case CHAMPION_KNIGHT -> {
                if (this.shields >= 10) {
                    shouldIncrementRank = true;
                    this.shields -= 10;
                }
            }
        }

        if(shouldIncrementRank) this.rankCard.setRank(RankCard.getNextRank(currentRank));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Player p)) return false;
        return p.playerId == this.playerId;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", cards=" + cards +
                '}';
    }

    /**
     * Computes the total battle points of the cards in a player's hand
     * @return an int, representing the battle points of all battleable cards in a player's hand (Only allies for now)
     */
    private int handBattlePoints() {
        int bp = 0;
//        for (Card card: cards) {
//            if (card.getClass() == AllyCard.class) bp += ((AllyCard) card).getBattlePoints();
//        }

        return bp;
    }

    @Override
    public int compareTo(Object o) {
        Player otherPlayer = (Player) o;
        // compare players based on rank first
        int res = this.getRank().ordinal() - otherPlayer.getRank().ordinal();
        if (res == 0) {
            // if their ranks are identical use shields
            return this.getShields() - otherPlayer.getShields();
        } else {
            return res;
        }
    }
}
