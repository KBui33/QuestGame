package model;

import component.card.FoeCard;
import component.card.WeaponCard;

import java.util.List;

public class FoeStage extends Stage{

    private FoeCard foe; // TODO :: Stage already has stage card. Is FoeCard needed?
    private List<WeaponCard> weapons;
    private String questCardFoe;

    public FoeStage(FoeCard foe, List<WeaponCard> weapons, String questCardFoe){
        super(foe);
        this.foe = foe;
        this.weapons = weapons;
        this.questCardFoe = questCardFoe;
    }

    public int[] getFoeBattlePoints() {
        return foe.getBp();
    }

    public int getWeaponsBattlePoints() {
        int battlePoints = 0;
        for(WeaponCard weapon: weapons) {
            battlePoints += weapon.getBattlePoints();
        }

        return battlePoints;
    }

    public Integer calculateBattlePoints(){
        int[] totalBp = {foe.getBp()[0]};
        assert questCardFoe != null;
        if(questCardFoe.equals(foe.getTitle())
                || questCardFoe.equals("All")
                || (questCardFoe.equals("All Saxons") && foe.getTitle().contains("Saxon"))){
            totalBp[0] = foe.getBp()[1];
        }
        weapons.forEach(w -> totalBp[0] += w.getBattlePoints());
        return totalBp[0];
    }

    public List<WeaponCard> getWeapons() {
        return weapons;
    }

    public FoeCard getFoe(){return foe;}

}
