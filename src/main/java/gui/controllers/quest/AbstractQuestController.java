package gui.controllers.quest;

import component.card.Card;
import component.card.WeaponCard;
import component.card.Card;
import component.card.WeaponCard;
import gui.controllers.GameController;
import gui.partials.CardView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashSet;

public abstract class AbstractQuestController {
    protected ObservableList<CardView> weaponCards;
    protected HashSet<String> weaponNames;
    protected GameController parent;


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
        // clear quest setup
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
    }

}
