package model;

import game.components.card.Card;
import game.components.card.FoeCard;
import game.components.card.WeaponCard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FoeStage extends Stage implements Serializable {

    private List<WeaponCard> weapons;

    public FoeStage(FoeCard foe, List<WeaponCard> weapons){
        super(foe);
        this.weapons = weapons;
    }

    public void calculateBattlePoints(){

    }

    public List<WeaponCard> getWeapons() {
        return weapons;
    }

}
