package model;

import component.card.Card;
import component.card.FoeCard;
import component.card.QuestCard;
import component.card.WeaponCard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Quest implements Serializable {

    private QuestCard questCard; //Current sponsored quest
    private ArrayList<Stage> stages; // Total amount of stages
    private ArrayList<QuestPlayer> currentQuestPlayers; // Players that are still participating in quest
    private ArrayList<QuestPlayer> questPlayers; // All players that joined quest
    private Player sponsor;
    private int currentStageIndex = 0;

    public Quest() {
        this.stages = new ArrayList<>();
        this.questPlayers = new ArrayList<>();
        this.currentQuestPlayers = new ArrayList<>();
    }

    public Quest(QuestCard quest) {
       this();
       this.questCard = quest;
    }

    public Quest(QuestCard quest, Player sponsor) {
        this(quest);
        this.sponsor = sponsor;
    }


    public boolean addPlayer(Player player) {
        return questPlayers.add((QuestPlayer) player);
    }

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
        this.currentQuestPlayers = new ArrayList<>(questPlayers);
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

    public void incrementStage() {
        this.currentStageIndex++;
    }

    // note this is the number of the current stage not the index in the array
    public int getCurrentStageNumber() {
        return this.currentStageIndex + 1;
    }

    public ArrayList<Stage> getStages() {
        return stages;
    }

    public ArrayList<QuestPlayer> getCurrentQuestPlayers() {
        return currentQuestPlayers;
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
        Map<String, String> stageResults = new HashMap<>();
        if (stage instanceof FoeStage) {
            int stageBattlePoints = ((FoeStage) stage).calculateBattlePoints();
            System.out.println("== Stage battle points: " + stageBattlePoints);
            for (QuestPlayer questPlayer : currentQuestPlayers) {
                System.out.println("== Player " + questPlayer.getPlayerId() + " battle points: " + questPlayer.calculateBattlePoints());
                if (questPlayer.calculateBattlePoints() >= stageBattlePoints) {
                    stageResults.put(Integer.toString(questPlayer.getPlayerId()), "won");
                    continue;
                }
                stageResults.put(Integer.toString(questPlayer.getPlayerId()), "lost");
                stageLosers.add(questPlayer);
            }
        }

        for (QuestPlayer questPlayer : stageLosers) {
            currentQuestPlayers.remove(questPlayer);
        }

        stage.setStageResults(stageResults);

        return stageLosers;
    }

    /**
     * Sets up the quest
     */
    public void setupQuest(Map<Card, List<Card>> stageCards) {
        // Adding stages to the current quest
        for (int i = 0; i < questCard.getStages(); i++) {
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
    public int distributeToSponsor() {
        int[] cardsForSponsor = {0}; // amount of cards the sponsor gets
        stages.forEach(
                s -> {
                    if (s instanceof FoeStage stage) {
                        cardsForSponsor[0] += 1 + stage.getWeapons().size(); // The foe card itself and weapons if any
                    } else {
                        cardsForSponsor[0] += 1; // The test card itself
                    }
                }
        );
        cardsForSponsor[0] += stages.size(); // Add total amount of stages
        if((sponsor.getCards().size() + cardsForSponsor[0]) > 12) return 12 - sponsor.getCards().size();
        return cardsForSponsor[0];
    }

    /**
     * Distribute shields accordingly to quest winners.
     * Winners receive as many shields as there are stages in the quest
     */
    public void distributeShieldsToWinners() {
        for(QuestPlayer questPlayer: currentQuestPlayers) {
            questPlayer.incrementShields(stages.size());
        }
    }
}
