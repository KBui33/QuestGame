package model;

import game.components.card.Card;
import game.components.card.FoeCard;
import game.components.card.QuestCard;
import game.components.card.WeaponCard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Quest implements Serializable {

    private QuestCard questCard; //Current sponsored quest
    private ArrayList<Stage> stages; // Total amount of stages
    private ArrayList<QuestPlayer> questPlayers; //Players that participate in the quest
    // handles the current quest state
    // int state

    public Quest(QuestCard quest) {
        this.stages = new ArrayList<>();
        this.questPlayers = new ArrayList<>();
        this.questCard = quest;
    }

    public void setupQuest(Map<Card, List<Card>> stageCards){
        // Adding stages to the current quest
        for(int i = 0; i < questCard.getStages(); i++){
            stageCards
                    .forEach((key, value) -> {
                        if (key.getClass() == FoeCard.class) {
                            List<WeaponCard>
                                    stageWeapons =
                                    value
                                            .stream()
                                            .map(c -> (WeaponCard) c)
                                            .collect(Collectors.toList());
                            FoeStage s = new FoeStage((FoeCard) key, (ArrayList<WeaponCard>) stageWeapons, questCard.getFoe());
                            stages.add(s);
                        } else {
                            // Add Test Stage
                        }
                    });
        }
    }

    // get the current and next stage

//    public void startQuest(InternalGameState internalGameState){
//        /*
//        * - Sponsor hands one card to players in quest(from adventure deck)
//        *   for each player participating
//        *        hand out one card to their hand
//        *
//        * - Players who participate must place card(s) against the quest card(s)
//        *
//        *  wait for players
//        *
//        *  for each stage
//        *        flip the card(s)
//        *        if FOE (and weapons)
//        *           for each player participating
//        *                flip their card(s)
//        *                if player card(s) >= stage card(s)
//        *                   - player proceeds to next stage
//        *                   - draw one card (from adventure)
//        *                else
//        *                   - player cannot go to next stage
//        *       else TEST
//        *            *** Impl later ***
//        *
//        * - Discard all cards in quest
//        * */
//
//
//        questPlayers.forEach(
//                p ->{
//
//                });
//    }

    public boolean addPlayer(Player player){return questPlayers.add((QuestPlayer) player);}
    public void addStage(Stage stage) {
        this.stages.add(stage);
    }

    public boolean addQuestPlayer(Player player) { return this.questPlayers.add(new QuestPlayer(player)); }

    public void addQuestPlayer( int index, Player player) { this.questPlayers.add(index, new QuestPlayer(player)); }

    public int currentStageCount() {
        return this.stages.size();
    }

    public void startQuest(){}

    public QuestCard getQuestCard() {
        return questCard;
    }

    public String getQuestFoe() {
        return questCard.getFoe();
    }

    public Stage getCurrentStage() {
        return stages.get(0);
    }

    public ArrayList<Stage> getStages() {
        return stages;
    }

    public ArrayList<QuestPlayer> getQuestPlayers() {
        return questPlayers;
    }

    public QuestPlayer getQuestPlayer(int index) {
        return questPlayers.get(index);
    }

    /**
     * Find the players set to move forward to next stage based on battle points
     */
    public void computeStageWinners() {

    }
}
