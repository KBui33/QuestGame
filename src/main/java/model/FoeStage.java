package model;

import game.components.card.Card;
import game.components.card.FoeCard;
import game.components.card.WeaponCard;

import java.util.ArrayList;
import java.util.List;

public class FoeStage extends Stage{

    private FoeCard foe;
    private List<WeaponCard> weapons;
    private String questCardFoe;

    public FoeStage(FoeCard foe, List<WeaponCard> weapons, String questCardFoe){
        super(foe);
        this.foe = foe;
        this.weapons = weapons;
        this.questCardFoe = questCardFoe;
    }

    public Integer calculateBattlePoints(){
        int[] totalBp = {foe.getBp()[0]};
        if(questCardFoe != null && questCardFoe.equals(foe.getTitle())){totalBp[0] = foe.getBp()[1];}
        weapons.forEach(w -> totalBp[0] += w.getBattlePoints());
        return totalBp[0];
    }

    public List<WeaponCard> getWeapons() {
        return weapons;
    }

    public FoeCard getFoe(){return foe;}

}
