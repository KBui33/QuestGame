package model;

import game.components.card.Card;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestPlayer extends Player implements Serializable {
    private Player player;
    private ArrayList<Card> playerQuestCardUsed;

    public QuestPlayer(Player player) {
        this.player = player;
    }

    @Override
    public int getPlayerId() {
        return player.getPlayerId();
    }
}
