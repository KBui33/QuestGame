package model;

import game.components.card.Card;
import game.components.card.FoeCard;
import game.components.card.QuestCard;
import game.components.card.WeaponCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Quest {

    private QuestCard quest;
    private ArrayList<Stage> stages;
    private ArrayList<QuestPlayer> questPlayers;

    public Quest(QuestCard quest) {
        this.stages = new ArrayList<>();
        this.questPlayers = new ArrayList<>();
        this.quest = quest;
    }

    public void setupQuest(Map<Card, List<Card>> stageCards){
        // Adding stages to the current quest
        for(int i = 0; i < quest.getStages(); i++){
            stageCards
                    .forEach((key, value) -> {
                        if (key.getClass() == FoeCard.class) {
                            List<WeaponCard>
                                    stageWeapons =
                                    value
                                            .stream()
                                            .map(c -> (WeaponCard) c)
                                            .collect(Collectors.toList());
                            FoeStage s = new FoeStage((FoeCard) key, stageWeapons, quest.getFoe());
                            stages.add(s);
                        } else {
                            // Add Test Stage
                        }
                    });
        }
    }

    public void startQuest(){}
}
