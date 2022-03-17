package gui.controllers;

import game.components.card.Card;
import game.components.card.QuestCard;
import game.components.card.WeaponCard;
import gui.other.AlertBox;
import gui.partials.CardView;
import gui.partials.QuestView;
import gui.partials.StageCompleteView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.Quest;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author James DiNovo
 *
 * Control QuestView and handle interactions
 */
public class QuestController {
    private QuestView questView;
    private StageCompleteView currentStage;
    private int stageCount;
    private QuestCard quest;
    private ObservableList<CardView> weaponCards;
    private HashSet<String> weaponNames;

    public QuestController(Quest quest) {
        this.questView = new QuestView(quest);

        questView.clearStage();
        stageCount = 1;

        this.weaponNames = new HashSet<String>();
        this.weaponCards = FXCollections.observableArrayList();

        // take in quest

        // show player current stage

        // let player choose weapon cards if its a foe to raise their battle points

        // cycle through other quest players

        // show results to player and then move to next quest player or after each player has chosen cards?

        // LOTS OF DUPLICATE CODE WILL NEED MAJOR REFACTORING
    }

    private void cleanUpGui(GameController parent) {
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
        parent.setMyHandList(tmp);
        parent.getView().getHud().getMyHand().setListViewItems(parent.getMyHandList());
    }

    public void pickCards(GameController parent) {
        questView.mode(true);
        // TODO show the current stage number
        questView.getStageText().setText(QuestView.STAGE_TEXT + stageCount);

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

            cleanUpGui(parent);

            parent.playerStageCardsPicked(wl);
        });

    }

    public boolean canAddWeapon(Card card) {
        if (card instanceof WeaponCard) {
            return !weaponNames.contains(card.getTitle());
        }
        return false;
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

    public StageCompleteView getCurrentStageView() {
        return currentStage;
    }

    public QuestView getQuestView() {
        return questView;
    }
}
