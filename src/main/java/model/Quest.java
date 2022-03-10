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

    private QuestCard quest; //Current sponsored quest
    private List<Stage> stages; // Total amount of stages
    private List<QuestPlayer> questPlayers; //Players that participate in the quest

    public Quest(QuestCard quest, List<QuestPlayer> questPlayers) {
        this.stages = new ArrayList<>();
        this.questPlayers = questPlayers;
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

    public void startQuest(){
        /*
        * - Sponsor hands one card to players in quest(from adventure deck)
        * - Players who participate must place card(s) against the quest card(s)
        *  for each stage
        *        flip the card(s)
        *        if FOE (and weapons)
        *           for each player participating
        *                flip their card(s)
        *                if player card(s) >= stage card(s)
        *                   - player proceeds to next stage
        *                   - draw one card (from adventure)
        *                else
        *                   - player cannot go to next stage
        *       else TEST
        *            *** Impl later ***
        *
        * - Discard all cards in stage and sponsor gains new cards
        * */
    }
}
