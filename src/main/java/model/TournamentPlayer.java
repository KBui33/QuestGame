package model;

import component.card.AllyCard;
import component.card.Card;
import component.card.WeaponCard;

import java.io.Serializable;
import java.util.ArrayList;

public class TournamentPlayer extends Player implements Serializable {
    private Player player;
    private ArrayList<Card> cardsUsed;

    public TournamentPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public int getPlayerId() {
        return player.getPlayerId();
    }

    public void setCardsUsed(ArrayList<Card> playerQuestCardsUsed) {
        this.cardsUsed = new ArrayList<>(playerQuestCardsUsed);
        System.out.println("== Used: " + this.cardsUsed.size());
    }

    public ArrayList<Card> getCardsUsed() {
        return cardsUsed;
    }

    public int calculateBattlePoints(){
        int battlePoints = player.getBattlePoints();

        for(Card card: cardsUsed){
            if(card instanceof WeaponCard){
                battlePoints += ((WeaponCard) card).getBattlePoints();
            }else if (card instanceof AllyCard){
                // Impl later
            }
        }

        System.out.println("== BP: " + battlePoints);
        return battlePoints;
    }

    public void resetCardsUsed() {
        cardsUsed.clear();
    }

    public void addCard(Card card) {
        this.player.addCard(card);
    }

    @Override
    public void incrementShields(int inc) {
        this.player.incrementShields(inc);
    }


}
