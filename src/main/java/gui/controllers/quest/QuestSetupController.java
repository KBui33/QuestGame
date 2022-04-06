package gui.controllers.quest;

import component.card.*;
import gui.controllers.AbstractFightController;
import gui.controllers.GameController;
import gui.other.AlertBox;
import gui.partials.CardView;
import gui.partials.quest.QuestSetupView;
import gui.partials.quest.StageSetupView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import model.FoeStage;
import model.Quest;
import utils.Callback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * @author James DiNovo
 *
 * Controls QuestSetupView and StageSetupView to allow users to set up quests
 */
public class QuestSetupController extends AbstractFightController {
    private QuestSetupView questSetupView;
    private ArrayList<FoeStage> stages;
    private int prevStageBp;
    private Quest quest;

    public QuestSetupController(GameController parent, QuestCard questCard, Callback<Quest> callback) {
        this.parent = parent;
        this.quest = new Quest(questCard);
        this.questSetupView = new QuestSetupView(questCard);
        this.weaponCards = FXCollections.observableArrayList();
        this.weaponNames = new HashSet<>();
        this.stages = new ArrayList<>();
        prevStageBp = 0;

        this.questSetupView.getPromptText().setText(QuestSetupView.STAGE_PROMPT + (stages.size() + 1));

        // if they choose to sponsor allow them to pick a foe and weapons for that foe for each stage...
        ObservableList<CardView> foesOrTests = parent.getMyHandList().filtered(c -> c.getCard() instanceof FoeCard || c.getCard() instanceof TestCard);
        ObservableList<CardView> weapons = parent.getMyHandList().filtered(c -> c.getCard() instanceof WeaponCard);


        ObservableList<CardView> addedWeapons = FXCollections.observableArrayList();

        setUpCards(foesOrTests, weapons, addedWeapons);

        questSetupView.getBackButton().setOnAction(e -> {
            returnToPreviousStage(foesOrTests, weapons, addedWeapons);

            if (stages.isEmpty()) {
                questSetupView.getBackButton().setVisible(false);
            }
            this.questSetupView.getPromptText().setText(QuestSetupView.STAGE_PROMPT + (stages.size() + 1));
        });

        questSetupView.getNextStageButton().setOnAction(e -> {
            if (questSetupView.getStageSetupView().getStageCard().getCard() instanceof FoeCard) {
                List<WeaponCard> wl = new ArrayList<>();
                for (CardView cv : weaponCards) {
                    wl.add((WeaponCard) cv.getCard());
                }
                // not fully implemented...
                // could end up in situations where players do not have enough cards to complete setting up quest
                // requires back button implementation and previous stage states stored
                FoeStage curStage = new FoeStage((FoeCard) questSetupView.getStageSetupView().getStageCard().getCard(),  wl, quest.getQuestFoe());
                int curBp = quest.computeFoeStageBattlePoints(curStage);

                if (curBp > prevStageBp) {
                    // we can add stage
                    stages.add(curStage);
                    prevStageBp = curBp;
                    questSetupView.getBackButton().setVisible(true);
                } else {
                    // we cant add stage player must rearrange cards
                    AlertBox.alert("The battle points for this stage must be greater than the previous foe stage." +
                            "\nCurrent Stage Battle Points: " + curBp + "\nPrevious Stage Battle Points: " + prevStageBp);
                    return;
                }

//                quest.addStage(new FoeStage((FoeCard) currentStage.getStageCard().getCard(),  wl, quest.getQuestFoe()));
            }
            addedWeapons.clear();
            weaponNames.clear();
            weaponCards.clear();

            clearStage();

            if (stages.size() == questCard.getStages()) {
                // quest set up complete
//                parent.questSetupComplete(quest);
                cleanUpGui();
                stages.forEach(s -> {
                    quest.addStage(s);
                });
                callback.call(quest);
            } else {
                if (questCard.getStages() - stages.size() < 1) {
                    questSetupView.getNextStageButton().setText("Finish");
                }
                this.questSetupView.getPromptText().setText(QuestSetupView.STAGE_PROMPT + (stages.size() + 1));

                parent.getView().getHud().getMyHand().setListViewItems(foesOrTests);
                parent.showHand();
            }

        });
    }

    public QuestSetupView getView() {
        return questSetupView;
    }

    private void setUpCards(ObservableList<CardView> foesOrTests, ObservableList<CardView> weapons, ObservableList<CardView> addedWeapons) {
        addedWeapons.clear();
        weaponNames.clear();
        weaponCards.clear();

        for (CardView f : foesOrTests) {
            f.getPlayButton().setVisible(true);
            f.getDiscardButton().setVisible(false);
            f.getPlayButton().setOnAction(e1 -> {
                // once foe is chosen remove it from hand
                parent.getMyHandList().remove(f);
                // add it to quest display
                this.addStage(f.getCard(), e2 -> {
                    this.clearStage();
                    parent.getMyHandList().add(f);
                    parent.getMyHandList().addAll(addedWeapons);
                    addedWeapons.clear();
                    weaponNames.clear();
                    parent.getView().getHud().getMyHand().setListViewItems(foesOrTests);
                    parent.showHand();
                });

                if (f.getCard() instanceof FoeCard) {
                    parent.getView().getHud().getMyHand().setListViewItems(weapons);
                }
            });
        }

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
                    AlertBox.alert(w.getCard().getTitle() + " has already been added to this foe.", Alert.AlertType.WARNING);
                }

            });
        }

        parent.getView().getHud().getMyHand().setListViewItems(foesOrTests);
        parent.showHand();
    }

    private void returnToPreviousStage(ObservableList<CardView> foesOrTests, ObservableList<CardView> weapons, ObservableList<CardView> addedWeapons) {
        ArrayList<CardView> cardsToAdd = new ArrayList<>();
        // handle any already selected cards
        if (questSetupView.getStageSetupView().getStageCard().getCard() != null && !Objects.equals(questSetupView.getStageSetupView().getStageCard().getCard().getCardImg(), "")) {
            cardsToAdd.add(new CardView(questSetupView.getStageSetupView().getStageCard().getCard()));
        }
        if (!questSetupView.getStageSetupView().getWeaponsView().getListView().getItems().isEmpty()) {
            questSetupView.getStageSetupView().getWeaponsView().getListView().getItems().forEach(c -> {
                cardsToAdd.add(new CardView(c.getCard()));
            });
        }

        FoeStage prevStage = stages.remove(stages.size() - 1);
        if (prevStage != null) {
            // need to add these cards back into hand
            cardsToAdd.add(new CardView(prevStage.getFoe()));
            prevStage.getWeapons().forEach(wp -> {
                cardsToAdd.add(new CardView(wp));
            });

            prevStageBp -= quest.computeFoeStageBattlePoints(prevStage);
        } else {
            prevStageBp = 0;
        }

        if (prevStageBp < 0) {
            prevStageBp = 0;
        }


        clearStage();
        // reset cards
        cardsToAdd.forEach(c -> {
            parent.setCardViewButtonActions(c);
        });
        parent.getMyHandList().addAll(cardsToAdd);
        setUpCards(foesOrTests, weapons, addedWeapons);
    }

    public void addStage(Card card, EventHandler<ActionEvent> e) {
        questSetupView.setStageSetupView(card);
        questSetupView.getStageSetupView().getStageCard().getDiscardButton().setOnAction(e);
        weaponCards.clear();
        if (card instanceof FoeCard) {
            questSetupView.getStageSetupView().getWeaponsView().setListViewItems(weaponCards);
        }
        questSetupView.getNextStageButton().setVisible(true);
    }

    public void clearStage() {
        questSetupView.getNextStageButton().setVisible(false);
        questSetupView.clearStage();
    }

}
