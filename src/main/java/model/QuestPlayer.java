package model;

import game.components.card.Card;
import game.components.card.WeaponCard;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestPlayer extends Player implements Serializable {
    private Player player;
    private ArrayList<Card> playerQuestCardUsed;

    public QuestPlayer(Player player) {
        this.player = player;
        this.setPlayerId(player.getPlayerId());
    }

    @Override
    public int getPlayerId() {
        return player.getPlayerId();
    }

    public void setPlayerQuestCardUsed(ArrayList<Card> playerQuestCardUsed) {
        this.playerQuestCardUsed = new ArrayList<>(playerQuestCardUsed);
    }

    public ArrayList<Card> getPlayerQuestCardUsed() {
        return playerQuestCardUsed;
    }

    public int calculateBattlePoints() {
        int battlePoints = player.getShields(); // Minimum battle points

        for(Card questCardUsed: playerQuestCardUsed) { // Sum up battle points based on quest cards used
            if(questCardUsed instanceof WeaponCard) {
                battlePoints += ((WeaponCard) questCardUsed).getBattlePoints();
            }
        }

        return battlePoints;
    }

    public void resetQuestCardsUsed() {
        playerQuestCardUsed.clear();
    }

    public Player getPlayer() {
        return player;
    }
}
