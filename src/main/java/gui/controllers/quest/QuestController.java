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

    public void pickCards(GameController parent, Quest quest, Callback<ArrayList<Card>> callback) {
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

    public void questComplete(GameController parent, Quest quest, Player player, CallbackEmpty callback) {

        updateQuest(quest);
        this.questView.mode(QuestView.Mode.COMPLETE);

        ObservableList<String> players = FXCollections.observableArrayList();
        ObservableList<String> outcomes = FXCollections.observableArrayList();

        // get all players and when they failed or if they succeeded
        quest.getQuestPlayers().forEach(p -> {
            players.add("Player " + p.getPlayerId());
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
            parent.cleanUpGui();
            parent.getView().getMainPane().clear();
            callback.call();
        });

    }

    public void sponsorQuestRewards(GameController parent, Quest quest, ArrayList<Card> cards, Callback<ArrayList<Card>> callback) {
        updateQuest(quest);
        this.questView.mode(QuestView.Mode.SPONSOR_CARDS);

        ObservableList<CardView> cardsAwarded = FXCollections.observableArrayList();

        cards.forEach(card -> {
            CardView tmp = new CardView(card);
            if (parent.getMyHandList().size() + cards.size() > 12) {
                tmp.getButtonBox().setVisible(true);
                tmp.getPlayButton().setVisible(false);
                tmp.getDiscardButton().setOnAction(e -> {
                    parent.sponsorDiscardRewardCard(tmp.getCard());
                    cardsAwarded.remove(tmp);
                });
            }
            cardsAwarded.add(tmp);
        });

        this.questView.getQuestSponsorCardsView().getDeckView().setListViewItems(cardsAwarded);

        this.questView.getQuestSponsorCardsView().getAcceptButton().setOnAction(e -> {
            if (cardsAwarded.size() + parent.getMyHandList().size() > 12) {
                AlertBox.alert("You cannot have more than 12 cards in your hand. You must choose "
                        + ((cardsAwarded.size() + parent.getMyHandList().size()) - 12) + " cards to discard from your " +
                        "reward.", Alert.AlertType.WARNING);
            } else {
                parent.cleanUpGui();
                parent.getView().getMainPane().clear();
                ArrayList<Card> cardsKept = new ArrayList<>();
                cardsAwarded.forEach(card -> {
                    cardsKept.add(card.getCard());
                });
                callback.call(cardsKept);
            }
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
