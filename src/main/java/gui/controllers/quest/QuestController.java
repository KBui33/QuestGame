package gui.controllers.quest;

import game.components.card.Card;
import game.components.card.WeaponCard;
import gui.controllers.GameController;
import gui.other.AlertBox;
import gui.partials.CardView;
import gui.partials.quest.QuestCompleteView;
import gui.partials.quest.QuestView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
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
public class QuestController extends AbstractQuestController {
    private QuestView questView;
    private Quest quest;
    private boolean questStarted = false;

    public QuestController(Quest quest) {
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

    public void stageComplete(GameController parent, Quest quest, boolean passed, CallbackEmpty callback) {
        updateQuest(quest);
        questView.setStageCompleted(quest.getCurrentStage(), passed);
        questView.mode(QuestView.Mode.SHOW_RESULTS);

        parent.hideDecks();

        questView.getStageCompletedView().getContinueButton().setOnAction(e -> {
            // Send continue command to server

            parent.cleanUpGui();
            callback.call();
        });


    }

    public void pickCards(GameController parent, Quest quest, Callback<Object> callback) {
        updateQuest(quest);
        this.questStarted = true;
        this.questView.mode(QuestView.Mode.PICK_CARDS);

        ObservableList<CardView> weapons = parent.getMyHandList().filtered(c -> c.getCard() instanceof WeaponCard);
        ObservableList<CardView> addedWeapons = FXCollections.observableArrayList();

        for (CardView w : weapons) {
            w.getPlayButton().setVisible(true);
            w.getPlayButton().setText("Add Weapon");
            w.getDiscardButton().setVisible(false);
            w.getPlayButton().setOnAction(e1 -> {
                if (canAddWeapon(w.getCard())) {
                    // once foe is chosen remove it from hand
                    parent.getMyHandList().remove(w);
                    addedWeapons.add(w);
                    parent.hideDecks();
                    // add it to stage
                    CardView weap = addWeapon((WeaponCard) w.getCard());
                    weap.getDiscardButton().setOnAction(e2 -> {
                        removeWeapon(weap);
                        parent.getMyHandList().add(w);
                        addedWeapons.remove(w);
                        parent.showHand();
                    });
                } else {
                    AlertBox.alert(w.getCard().getTitle() + " has already been added to your selection.", Alert.AlertType.WARNING);
                }
            });
        }
        parent.getView().getHud().getMyHand().setListViewItems(weapons);
        parent.showHand();

        questView.getStageCardSelectionView().getWeaponsView().setListViewItems(weaponCards);

        getQuestView().getStageCardSelectionView().getDoneButton().setOnAction(e -> {
            ArrayList<Card> wl = new ArrayList<>();
            for (CardView cv : weaponCards) {
                wl.add((WeaponCard) cv.getCard());
            }

            addedWeapons.clear();
            weaponNames.clear();
            weaponCards.clear();

            parent.cleanUpGui();
            questView.clearStage();

            callback.call(wl);
        });

    }

    public void questComplete(GameController parent, Quest quest, CallbackEmpty callback) {

        updateQuest(quest);

        ObservableList<String> players = FXCollections.observableArrayList();
        ObservableList<String> outcomes = FXCollections.observableArrayList();

        // get all players and when they failed or if they succeeded
        quest.getCurrentQuestPlayers().forEach(p -> {
            players.add("Player " + p.getPlayerId());
            // TODO :: - get player outcomes
//            outcomes.add(p.failed ? "Failed" : "Passed");
        });

        this.questView.getQuestCompleteView().getPlayers().setItems(players);
        this.questView.getQuestCompleteView().getOutcomes().setItems(outcomes);

        // if this player succeeded, add shields and display that
        // if this player failed, display that
        this.questView.getQuestCompleteView().getInfoText().setText(QuestCompleteView.SHIELDS_STRING + 3);

        this.questView.getQuestCompleteView().getContinueButton().setOnAction(e -> {
            parent.cleanUpGui();
            callback.call();
        });

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
