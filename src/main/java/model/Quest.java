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
    private Player sponsor;
    private int currentStageIndex = 0;

    public Quest() {
        this.stages = new ArrayList<>();
        this.questPlayers = new ArrayList<>();
    }

    public Quest(QuestCard quest) {
        this.stages = new ArrayList<>();
        this.questPlayers = new ArrayList<>();
        this.questCard = quest;
    }

    public Quest(QuestCard quest, Player sponsor) {
        this.stages = new ArrayList<>();
        this.questPlayers = new ArrayList<>();
        this.sponsor = sponsor;
        this.questCard = quest;
    }


    public boolean addPlayer(Player player){return questPlayers.add((QuestPlayer) player);}
    public void addStage(Stage stage) {
        this.stages.add(stage);
    }

    public boolean addQuestPlayer(Player player) {
        return this.questPlayers.add(new QuestPlayer(player));
    }

    public void addQuestPlayer(int index, Player player) {
        this.questPlayers.add(index, new QuestPlayer(player));
    }

    public int currentStageCount() {
        return this.stages.size();
    }

    public void startQuest() {
    }

    public QuestCard getQuestCard() {
        return questCard;
    }

    public String getQuestFoe() {
        return questCard.getFoe();
    }

    public Stage getCurrentStage() {
        return stages.get(currentStageIndex);
    }

    public void incrementStage() { this.currentStageIndex++; }

    public ArrayList<Stage> getStages() {
        return stages;
    }

    public ArrayList<QuestPlayer> getQuestPlayers() {
        return questPlayers;
    }

    public QuestPlayer getQuestPlayer(int index) {
        return questPlayers.get(index);
    }

    public QuestPlayer getQuestPlayerByPlayerId(int playerId) {
        for (QuestPlayer questPlayer : questPlayers) {
            if (questPlayer.getPlayerId() == playerId) return questPlayer;
        }

        return null;
    }

    public void setSponsor(Player sponsor) {
        this.sponsor = sponsor;
    }

    public Player getSponsor() {
        return sponsor;
    }

    /**
     * Find the players set to move forward to next stage based on battle points
     */
    public ArrayList<QuestPlayer> computeStageWinners(Stage stage) {
        ArrayList<QuestPlayer> stageLosers = new ArrayList<>();
        if (stage instanceof FoeStage) {
            int stageBattlePoints = ((FoeStage) stage).calculateBattlePoints();
            System.out.println("== Stage battle points: " + stageBattlePoints);
            for (QuestPlayer questPlayer : questPlayers) {
                System.out.println("== Player " + questPlayer.getPlayerId() + " battle points: " + questPlayer.calculateBattlePoints());
                if (questPlayer.calculateBattlePoints() >= stageBattlePoints) continue;
                stageLosers.add(questPlayer);
            }
        }

        for (QuestPlayer questPlayer : stageLosers) {
            questPlayers.remove(questPlayer);
        }

        return stageLosers;
    }

    /**
     * Sets up the quest
     * */
    public void setupQuest(Map<Card, List<Card>> stageCards){
        // Adding stages to the current quest
        for(int i = 0; i < questCard.getStages(); i++){
            stageCards
                    .forEach((key, value) -> {
                        if (key instanceof WeaponCard) {
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

    /**
     * Return the amount of cards the sponsor gets
     */
    public int distributeToSponsor(){
        int[] cardsForSponsor = {0}; // amount of cards the sponsor gets
        stages.forEach(
                s -> {
                    if(s instanceof FoeStage stage){
                        cardsForSponsor[0] += 1 + stage.getWeapons().size(); // The foe card itself and weapons if any
                    }else{
                        cardsForSponsor[0] += 1; // The test card itself
                    }
                }
        );
        cardsForSponsor[0] += stages.size(); //add the amount of stages to total amount of cards the sponsor gets
        int difference = (sponsor.getCards().size() + cardsForSponsor[0]) - 12;
        cardsForSponsor[0] = cardsForSponsor[0] > 0 ? cardsForSponsor[0] - difference : cardsForSponsor[0];
        return cardsForSponsor[0];
    }
}
