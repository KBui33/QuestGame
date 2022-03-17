package model;

import game.components.card.Card;
import game.components.card.WeaponCard;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestPlayer extends Player implements Serializable {
    private Player player;
    private ArrayList<Card> playerQuestCardsUsed;

    public QuestPlayer(Player player) {
        this.player = player;
        this.setPlayerId(player.getPlayerId());
    }

    @Override
    public int getPlayerId() {
        return player.getPlayerId();
    }

    public void setPlayerQuestCardsUsed(ArrayList<Card> playerQuestCardsUsed) {
        this.playerQuestCardsUsed = new ArrayList<>(playerQuestCardsUsed);
    }

    public ArrayList<Card> getPlayerQuestCardsUsed() {
        return playerQuestCardsUsed;
    }

    public int calculateBattlePoints() {
        int battlePoints = player.getShields(); // Minimum battle points

        for(Card questCardUsed: playerQuestCardsUsed) { // Sum up battle points based on quest cards used
            if(questCardUsed instanceof WeaponCard) {
                battlePoints += ((WeaponCard) questCardUsed).getBattlePoints();
            }
        }

        return battlePoints;
    }

    public void resetQuestCardsUsed() {
        playerQuestCardsUsed.clear();
    }

    public Player getPlayer() {
        return player;
    }

    public void addCard(Card card) {
        this.player.addCard(card);
    }

}
