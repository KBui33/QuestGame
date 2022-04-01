package model;

import component.card.Card;
import component.card.WeaponCard;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestPlayer extends Player implements Serializable {
    private Player player;
    private ArrayList<Card> cardsUsed;

    public QuestPlayer(Player player) {
        this.player = player;
    }

    @Override
    public int getPlayerId() {
        return player.getPlayerId();
    }

    public void setCardsUsed(ArrayList<Card> cardsUsed) {
        this.cardsUsed = new ArrayList<>(cardsUsed);
    }

    public ArrayList<Card> getCardsUsed() {
        return cardsUsed;
    }

    public int calculateBattlePoints() {
        int battlePoints = player.getBattlePoints(); // Minimum battle points

        for(Card questCardUsed: cardsUsed) { // Sum up battle points based on quest cards used
            if(questCardUsed instanceof WeaponCard) {
                battlePoints += ((WeaponCard) questCardUsed).getBattlePoints();
            }
        }

        return battlePoints;
    }

    public void resetCardsUsed() {
        cardsUsed.clear();
    }

    public Player getPlayer() {
        return player;
    }

    public void addCard(Card card) {
        this.player.addCard(card);
    }

    @Override
    public void incrementShields(int inc) {
        this.player.incrementShields(inc);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof QuestPlayer)) return false;
        QuestPlayer p = (QuestPlayer) o;
        return p.player.equals(this.player);
    }

}
