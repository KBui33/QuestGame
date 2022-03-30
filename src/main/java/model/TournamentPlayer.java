package model;

import component.card.AllyCard;
import component.card.Card;
import component.card.WeaponCard;

import java.io.Serializable;
import java.util.ArrayList;

public class TournamentPlayer extends Player implements Serializable {
    private Player player;
    private ArrayList<Card> playerCardsUsed;

    public TournamentPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Card> getPlayerCardsUsed() {
        return playerCardsUsed;
    }

    public int calculateBattlePoints(){
        int battlePoints = player.getBattlePoints();

        for(Card card: playerCardsUsed){
            if(card instanceof WeaponCard){
                battlePoints += ((WeaponCard) card).getBattlePoints();
            }else if (card instanceof AllyCard){
                // Impl later
            }
        }

        return battlePoints;
    }
}
