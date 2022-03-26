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
    private Player currentTurnPlayer;

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

    public Player getCurrentTurnPlayer() {
        return currentTurnPlayer;
    }

    public void setCurrentTurnPlayer(Player currentTurnPlayer) {
        this.currentTurnPlayer = currentTurnPlayer;
    }

    public void startQuest() {
        this.currentQuestPlayers = new ArrayList<>(questPlayers);
    }

    public QuestCard getQuestCard() {
        return questCard;
    }

    public String getTitle() { return questCard.getTitle(); }

    public String getQuestFoe() {
        return questCard.getFoe();
    }

    public Stage getCurrentStage() {
        return stages.get(currentStageIndex);
    }

    public void incrementStage() {
        if (this.currentStageIndex < this.getStages().size() - 1) {
            this.currentStageIndex++;
        }
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
        Map<Integer, Boolean> stageResults = new HashMap<>();
        if (stage instanceof FoeStage) {
            int stageBattlePoints = computeFoeStageBattlePoints((FoeStage) stage);
            System.out.println("== Stage battle points: " + stageBattlePoints);
            for (QuestPlayer questPlayer : currentQuestPlayers) {
                System.out.println("== Player " + questPlayer.getPlayerId() + " battle points: " + questPlayer.calculateBattlePoints());
                if (questPlayer.calculateBattlePoints() >= stageBattlePoints) {
                    stageResults.put(questPlayer.getPlayerId(), true);
                    continue;
                }
                stageResults.put(questPlayer.getPlayerId(), false);
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
                    if (s instanceof FoeStage) {
                        cardsForSponsor[0] += 1 + ((FoeStage) s).getWeapons().size(); // The foe card itself and weapons if any
                    } else {
                        cardsForSponsor[0] += 1; // The test card itself
                    }
                }
        );
        cardsForSponsor[0] += stages.size(); //add the amount of stages to total amount of cards the sponsor gets
        //int difference = (sponsor.getCards().size() + cardsForSponsor[0]) - 12; -- THIS WILL BE HANDLED ON THE GUI
        //cardsForSponsor[0] = cardsForSponsor[0] > 0 ? cardsForSponsor[0] - difference : cardsForSponsor[0]; -- THIS WILL BE HANDLED ON THE GUI
        return cardsForSponsor[0];
    }


    public int computeFoeStageBattlePoints(FoeStage stage) {
        int battlePoints = 0;

        battlePoints += stage.getWeaponsBattlePoints();
        int[] foeBP = stage.getFoeBattlePoints();

        String currentFoe = questCard.getFoe() == null ? "none" : questCard.getFoe().toLowerCase();
        String stageFoe = stage.getStageCard().getTitle().toLowerCase();

        // Add higher/lower foe battle points based on foe name and quest title
        // or if the foe is all
        if(foeBP.length == 2 && (currentFoe.equals(stageFoe)
                || currentFoe.equals("all"))
                || (stageFoe.contains("saxon") && currentFoe.equals("all saxons"))){
            battlePoints += Integer.max(foeBP[0], foeBP[1]);
        }else{
            battlePoints += foeBP[0];
        }

        return battlePoints;
    }

    /**
     * Distribute shields accordingly to quest winners.
     * Winners receive as many shields as there are stages in the quest
     */
    public void distributeShieldsToWinners() {
        for (QuestPlayer questPlayer : currentQuestPlayers) {
            questPlayer.incrementShields(stages.size());
        }
    }

    public ArrayList<Card> getAllQuestCards(boolean includeQuestCard) {
        ArrayList<Card> stageCards = getAllStageCards();
        if (includeQuestCard) stageCards.add(questCard);
        return stageCards;
    }

    public ArrayList<Card> getAllStageCards() {
        ArrayList<Card> stageCards = new ArrayList<>();
        for (Stage stage : stages) {
            stageCards.add(stage.getStageCard());

            if (stage instanceof FoeStage) {
                stage = (FoeStage) stage;
                for (WeaponCard weaponCard : ((FoeStage) stage).getWeapons()) {
                    stageCards.add(weaponCard);
                }
            }
        }

        return stageCards;
    }
}
