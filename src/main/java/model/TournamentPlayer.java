package model;

import component.card.AllyCard;
import component.card.Card;
import component.card.RankCard;
import component.card.WeaponCard;

import java.io.Serializable;
import java.util.ArrayList;

public class TournamentPlayer extends PlayerDecorator implements Serializable {
//    private Player player;
//    private ArrayList<Card> cardsUsed;
//
    public TournamentPlayer(Player player) {
        super(player);
    }

//    public Player getPlayer() {
//        return player;
//    }
//
//    @Override
//    public int getPlayerId() {
//        return player.getPlayerId();
//    }

//    public RankCard getRankCard() {
//        return player.getRankCard();
//    }
//
//    public void setCardsUsed(ArrayList<Card> playerQuestCardsUsed) {
//        this.cardsUsed = new ArrayList<>(playerQuestCardsUsed);
//    }

//    public ArrayList<Card> getCardsUsed() {
//        return cardsUsed;
//    }

    public int calculateBattlePoints(){
        int battlePoints = player.getBattlePoints();

        for(Card card: cardsUsed){
            if(card instanceof WeaponCard){
                battlePoints += ((WeaponCard) card).getBattlePoints();
            }else if (card instanceof AllyCard){
                // Impl later
            }
        }
        return battlePoints;
    }

//    public void resetCardsUsed() {
//        cardsUsed.clear();
//    }
//
//    public boolean addCard(Card card) {
//        this.player.addCard(card);
//    }
//
//    @Override
//    public void incrementShields(int inc) {
//        this.player.incrementShields(inc);
//    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TournamentPlayer)) return false;
        return super.equals(o);
    }


}
