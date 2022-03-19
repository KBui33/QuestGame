package gui.controllers.quest;

import game.components.card.Card;
import game.components.card.WeaponCard;
import gui.partials.CardView;
import javafx.collections.ObservableList;

import java.util.HashSet;

public abstract class AbstractQuestController {
    protected ObservableList<CardView> weaponCards;
    protected HashSet<String> weaponNames;


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

    public boolean canAddWeapon(Card card) {
        if (card instanceof WeaponCard) {
            return !weaponNames.contains(card.getTitle());
        }
        return false;
    }

}