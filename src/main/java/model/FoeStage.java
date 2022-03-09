package model;

import game.components.card.Card;

import java.util.ArrayList;

public class FoeStage extends Stage{

    private Card foe;
    private ArrayList<Card> weapons;

    public FoeStage(Card foe){
        this.foe = foe;
        this.weapons = new ArrayList<>();
    }

    public void calculateBattlePoints(){

    }

}
