package gui.controllers;

import component.card.Card;
import gui.other.AlertBox;
import gui.partials.CardView;
import gui.partials.CardsReceivedView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import utils.Callback;

import java.util.ArrayList;

public class CardsReceivedController {

    private CardsReceivedView cardsReceivedView;
    private GameController parent;

    public CardsReceivedController(GameController parent, CardsReceivedView cardsReceivedView) {
        this.parent = parent;
        this.cardsReceivedView = cardsReceivedView;
    }

    public void receiveCards (ArrayList<Card> cards, Callback<ArrayList<Card>> callback) {
        ObservableList<CardView> cardsAwarded = FXCollections.observableArrayList();

        cards.forEach(card -> {
            CardView tmp = new CardView(card);
            tmp.setSize(300);
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

        cardsReceivedView.getDeckView().setListViewItems(cardsAwarded);

        cardsReceivedView.getAcceptButton().setOnAction(e -> {
            if (cardsAwarded.size() + parent.getMyHandList().size() > 12) {
                AlertBox.alert("You cannot have more than 12 cards in your hand. You must choose "
                        + ((cardsAwarded.size() + parent.getMyHandList().size()) - 12) + " cards to discard from your " +
                        "reward.", Alert.AlertType.WARNING);
            } else {
                ArrayList<Card> cardsKept = new ArrayList<>();
                cardsAwarded.forEach(card -> {
                    cardsKept.add(card.getCard());
                });
                callback.call(cardsKept);
            }
        });
    }
}
