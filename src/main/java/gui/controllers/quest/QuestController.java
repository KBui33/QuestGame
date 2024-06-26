package gui.controllers.quest;

import component.card.Card;
import gui.controllers.AbstractFightController;
import gui.controllers.CardsReceivedController;
import gui.controllers.GameController;
import gui.partials.quest.QuestCompleteView;
import gui.partials.quest.QuestView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Player;
import model.Quest;
import utils.Callback;
import utils.CallbackEmpty;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * @author James DiNovo
 *
 * Control QuestView and handle interactions
 */
public class QuestController extends AbstractFightController {
    private QuestView questView;
    private Quest quest;
    private boolean questStarted = false;

    public QuestController(Quest quest, GameController parent) {
        this.parent = parent;
        this.questView = new QuestView();
        updateQuest(quest);

        questView.clearStage();

        this.weaponNames = new HashSet<String>();
        this.weaponCards = FXCollections.observableArrayList();
        // take in quest

        // show player current stage

        // let player choose weapon cards if its a foe to raise their battle points

        // cycle through other quest players

        // show results to player and then move to next quest player or after each player has chosen cards?

    }

    public void stageComplete(Quest quest, boolean passed, CallbackEmpty callback) {
        showGui();
        updateQuest(quest);
        questView.setStageCompleted(quest.getCurrentStage(), passed);
        questView.mode(QuestView.Mode.SHOW_RESULTS);

        parent.hideDecks();

        questView.getStageCompletedView().getContinueButton().setOnAction(e -> {
            // Send continue command to server

            cleanUpGui();
            callback.call();
        });


    }

    public void pickCards(Quest quest, Callback<ArrayList<Card>> callback) {
        showGui();
        updateQuest(quest);
        this.questStarted = true;
        this.questView.mode(QuestView.Mode.PICK_CARDS);

        pickWeapons(questView.getStageCardSelectionView(), wl -> {
            cleanUpGui();
            questView.clearStage();

            callback.call(wl);
        });

    }

    public void questComplete(Quest quest, Player player, CallbackEmpty callback) {
        showGui();
        updateQuest(quest);
        this.questView.mode(QuestView.Mode.COMPLETE);

        ObservableList<String> players = FXCollections.observableArrayList();
        ObservableList<String> outcomes = FXCollections.observableArrayList();

        // get all players and when they failed or if they succeeded
        quest.getPlayers().forEach(p -> {
            players.add("Player " + p.getPlayer().getPlayerNumber());
            System.out.println();
            Boolean res = quest.getCurrentStage().getStageResults().get(p.getPlayerId());
            outcomes.add(res != null && res ? "Passed" : "Failed");
        });

        this.questView.getQuestCompleteView().getPlayers().setItems(players);
        this.questView.getQuestCompleteView().getOutcomes().setItems(outcomes);

        // if this player succeeded, add shields and display that
        // if this player failed, display that
        Boolean res = quest.getCurrentStage().getStageResults().get(player.getPlayerId());
        if (res != null && res) {
            this.questView.getQuestCompleteView().getInfoText().setText(QuestCompleteView.SHIELDS_STRING + quest.getStages().size());
        } else {
            this.questView.getQuestCompleteView().getInfoText().setText("You were defeated. No shields earned.");
        }

        this.questView.getQuestCompleteView().getContinueButton().setOnAction(e -> {
            cleanUpGui();
            callback.call();
        });

    }

    public void sponsorQuestRewards(Quest quest, ArrayList<Card> cards, Callback<ArrayList<Card>> callback) {
        showGui();
        updateQuest(quest);
        this.questView.mode(QuestView.Mode.SPONSOR_CARDS);

        CardsReceivedController cardsReceivedController = new CardsReceivedController(parent, questView.getCardsReceivedView());

        cardsReceivedController.receiveCards(cards, chosen -> {
            cleanUpGui();
            callback.call(chosen);
        });
    }

    private void showGui() {
        parent.getView().getMainPane().clear();
        parent.getView().getMainPane().add(this.getQuestView());
    }
    public QuestView getQuestView() {
        return questView;
    }

    public void updateQuest(Quest q) {
        this.quest = q;
        this.questView.getQuestCard().setCard(q.getQuestCard());

        this.questView.getStageText().setText(QuestView.STAGE_TEXT + q.getCurrentStageNumber());
    }

    public boolean hasQuestStarted() {
        return questStarted;
    }
}
