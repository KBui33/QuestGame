package gui.controllers;

import game.components.card.*;
import gui.other.AlertBox;
import gui.partials.CardView;
import gui.partials.QuestSetupView;
import gui.partials.StageSetupView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import model.FoeStage;
import model.Quest;
import model.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author James DiNovo
 *
 * Controls QuestSetupView and StageSetupView to allow users to set up quests
 */
public class QuestSetupController {
    private QuestSetupView questSetupView;
    private StageSetupView currentStage;
    private ObservableList<CardView> weaponCards;
    private HashSet<String> weaponNames;
    private ArrayList<Stage> stages;
    private GameController parent;
    private Quest quest;

    public QuestSetupController(GameController parent, QuestCard questCard) {
        this.parent = parent;
        this.quest = new Quest(questCard);
        this.questSetupView = new QuestSetupView(questCard);
        this.weaponCards = FXCollections.observableArrayList();
        this.weaponNames = new HashSet<>();

        this.questSetupView.getPromptText().setText(QuestSetupView.STAGE_PROMPT + (quest.currentStageCount() + 1));

        // if they choose to sponsor allow them to pick a foe and weapons for that foe for each stage...
        ObservableList<CardView> foesOrTests = parent.getMyHandList().filtered(c -> c.getCard() instanceof FoeCard || c.getCard() instanceof TestCard);
        ObservableList<CardView> weapons = parent.getMyHandList().filtered(c -> c.getCard() instanceof WeaponCard);

        // check player has enough cards
        if (foesOrTests.size() < questCard.getStages()) {
            AlertBox.alert("INSUFFICIENT CARDS IN HAND", Alert.AlertType.WARNING, e -> {
                cleanUpGui();
            });
        } else {

            ObservableList<CardView> addedWeapons = FXCollections.observableArrayList();
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
                        AlertBox.alert("WEAPON OF TYPE ALREADY ADDED TO THIS FOE", Alert.AlertType.WARNING);
                    }

                });
            }
            parent.getView().getHud().getMyHand().setListViewItems(foesOrTests);
            parent.showHand();

            questSetupView.getNextStageButton().setOnAction(e -> {
                if (currentStage.getStageCard().getCard() instanceof FoeCard) {
                    List<WeaponCard> wl = new ArrayList<>();
                    for (CardView cv : weaponCards) {
                        wl.add((WeaponCard) cv.getCard());
                    }
                    quest.addStage(new FoeStage((FoeCard) currentStage.getStageCard().getCard(), wl));
                }
                addedWeapons.clear();
                weaponNames.clear();
                weaponCards.clear();

                clearStage();

                if (quest.currentStageCount() == questCard.getStages()) {
                    // quest set up complete
                    parent.questSetupComplete(quest);
                    cleanUpGui();
                } else {
                    this.questSetupView.getPromptText().setText(QuestSetupView.STAGE_PROMPT + (quest.currentStageCount() + 1));

                    parent.getView().getHud().getMyHand().setListViewItems(foesOrTests);
                    parent.showHand();
                }

            });
        }
    }

    private void cleanUpGui() {
        // clear quest setup
        parent.getView().getMainPane().getChildren().clear();

        // reset view
        parent.getView().getHud().getEndTurnButton().setVisible(true);

        // fix list view - need better fix at some point
        ObservableList<CardView> tmp = FXCollections.observableArrayList();

        parent.getMyHandList().forEach(c -> {
            CardView n = new CardView(c.getCard());
            parent.setCardViewButtonActions(n);
            tmp.add(n);
        });
        parent.getMyHandList().clear();
        parent.getMyHandList().addAll(tmp);
        parent.getView().getHud().getMyHand().setListViewItems(parent.getMyHandList());
    }

    public boolean canAddWeapon(Card card) {
        if (card instanceof WeaponCard) {
            return !weaponNames.contains(card.getTitle());
        }
        return false;
    }

    public QuestSetupView getView() {
        return questSetupView;
    }

    public StageSetupView addStage(Card card, EventHandler<ActionEvent> e) {
        currentStage = questSetupView.setStageSetupView(card);
        this.currentStage.getStageCard().getDiscardButton().setOnAction(e);
        weaponCards.clear();
        if (card instanceof FoeCard) {
            currentStage.getWeaponsView().setListViewItems(weaponCards);
        }
        questSetupView.getNextStageButton().setVisible(true);
        return currentStage;
    }

    public void clearStage() {
        questSetupView.getNextStageButton().setVisible(false);
        questSetupView.clearStage();

    }

    public CardView addWeapon(WeaponCard card) {
        CardView tmp = new CardView(card);
        tmp.getButtonBox().setVisible(true);
        tmp.getPlayButton().setVisible(false);
        tmp.getDiscardButton().setText("Remove");
        tmp.setSize(200);
        weaponCards.add(tmp);
        weaponNames.add(tmp.getCard().getTitle());


        return tmp;
    }

    public void removeWeapon(CardView cardView) {
        weaponCards.remove(cardView);
        weaponNames.remove(cardView.getCard().getTitle());
    }

    private void setStageView(StageSetupView ssv) {

    }

}
