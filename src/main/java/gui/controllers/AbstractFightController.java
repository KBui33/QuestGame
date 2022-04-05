package gui.controllers;

import component.card.Card;
import component.card.WeaponCard;
import gui.other.AlertBox;
import gui.partials.CardSelectionView;
import gui.partials.CardView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import utils.Callback;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author James DiNovo
 *
 * Shared controller for quests and tournaments to allow the selection of weapons for battle
 */
public abstract class AbstractFightController {
    protected ObservableList<CardView> weaponCards;
    protected HashSet<String> weaponNames;
    protected GameController parent;

    public void pickWeapons(CardSelectionView view, Callback<ArrayList<Card>> callback) {
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

        view.getWeaponsView().setListViewItems(weaponCards);

        view.getDoneButton().setOnAction(e -> {
            ArrayList<Card> wl = new ArrayList<>();
            for (CardView cv : weaponCards) {
                wl.add((WeaponCard) cv.getCard());
            }

            addedWeapons.clear();
            weaponNames.clear();
            weaponCards.clear();

            callback.call(wl);
        });
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

    public CardView addWeapon(WeaponCard card, boolean removable) {
        CardView tmp = addWeapon(card);

        tmp.getButtonBox().setVisible(removable);

        return tmp;
    }

    public void removeWeapon(CardView cardView) {
        weaponCards.remove(cardView);
        weaponNames.remove(cardView.getCard().getTitle());
    }

    public boolean canAddWeapon(Card card) {
        if (card instanceof WeaponCard) {
            return !weaponNames.contains(card.getTitle());
        }
        return false;
    }

    public void cleanUpGui() {
        // clear view
        parent.getView().getMainPane().clear();

        // reset view
//        getView().getHud().getEndTurnButton().setVisible(true);

        // fix list view - need better fix at some point
        ObservableList<CardView> tmp = FXCollections.observableArrayList();

        parent.getMyHandList().forEach(c -> {
            CardView n = new CardView(c.getCard());
            parent.setCardViewButtonActions(n);
            tmp.add(n);
        });
        parent.setMyHandList(tmp);
        parent.getView().getHud().getMyHand().setListViewItems(parent.getMyHandList());
        parent.hideDecks();
    }

}
