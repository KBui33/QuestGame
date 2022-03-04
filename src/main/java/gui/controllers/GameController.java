package gui.controllers;

import gui.panes.GamePane;
import gui.partials.CardView;
import gui.partials.DeckView;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.util.*;

/**
 * @author James DiNovo
 *
 * Controller for manipulating GamePane view
 *
 */
public class GameController {
    private ObservableList<CardView> myHand;
    private ObservableList<CardView> discarded;

    public GameController (GamePane view) {
        setView(view);
    }

    public void setView(GamePane view) {

        myHand = FXCollections.observableArrayList();
        view.getMyHand().setListViewItems(myHand);

        discarded = FXCollections.observableArrayList();
        view.getDiscardedCards().setListViewItems(discarded);

        // a lot of this is just for laying out gui will be removed later
        view.getCurrentStateText().setText("Your turn!");
        view.getShieldsView().setShields(1);

        for (int i = 1; i <= 11; i++) {
            addCardToHand(myHand, new Image(String.valueOf(getClass().getResource("/specials/quest_ally_" + i + ".png"))));
        }

        for (int i = 1; i <= 11; i++) {
            CardView cardView = new CardView(new Image(String.valueOf(getClass().getResource("/foes/quest_foe_" + i + ".png"))));
            discarded.add(0, cardView);
        }


        // set action for draw card button
        view.getDrawCardButton().setOnAction(e -> {
            // draw a card from server

            // once hand has more than 12 cards every next card drawn must be either played or discarded
            if (myHand.size() < 12) {
                addCardToHand(myHand, new Image(String.valueOf(getClass().getResource("/specials/quest_ally_4.png"))));
            } else {
                // display card with option to play it or discard it
                view.getDrawnCard().getImageView().setImage(new Image(String.valueOf(getClass().getResource("/specials/quest_ally_4.png"))));
                view.setCenter(view.getDrawnCard());
                view.getDrawCardButton().setDisable(true);
            }
        });

        view.getDrawnCard().getPlayButton().setOnAction(e -> {
            System.out.println("played card");
            view.setCenter(null);
            view.getDrawCardButton().setDisable(false);
        });

        view.getDrawnCard().getDiscardButton().setOnAction(e -> {
            System.out.println("discarded card");
            view.setCenter(null);
            view.getDrawCardButton().setDisable(false);
        });

        view.getEndTurnButton().setOnAction(e -> {
            System.out.println("Turn ended");
        });

        view.getShowHandButton().setOnAction(e -> {
            System.out.println("showing hand");
            // show and hide hand
            if (view.getBottom() != null && view.getBottom().equals(view.getMyHand().getListView())) {
                view.setBottom(null);
                view.getShowHandButton().getStyleClass().remove("caution");
            } else {
                view.setBottom(view.getMyHand().getListView());
                view.getShowHandButton().getStyleClass().add("caution");
                view.getShowDiscardedButton().getStyleClass().remove("caution");
            }
        });

        view.getShowDiscardedButton().setOnAction(e -> {
            System.out.println("showing discarded");
            // show and hide discarded
            if (view.getBottom() != null && view.getBottom().equals(view.getDiscardedCards().getListView())) {
                view.setBottom(null);
                view.getShowDiscardedButton().getStyleClass().remove("caution");
            } else {
                view.setBottom(view.getDiscardedCards().getListView());
                view.getShowDiscardedButton().getStyleClass().add("caution");
                view.getShowHandButton().getStyleClass().remove("caution");
            }
        });
    }

    // will be replaced with card object instead of image
    private void addCardToHand(ObservableList<CardView> hand, Image card) {
        CardView newcard = new CardView(card);
        setCardViewButtonActions(hand, newcard);
        myHand.add(0, newcard);
    }

    private void setCardViewButtonActions(ObservableList<CardView> deckView, CardView cardView) {
        cardView.getDiscardButton().setOnAction(e -> {
            // send delete signal to server and await response
            deckView.remove(cardView);
        });

        cardView.getPlayButton().setOnAction(e -> {
            System.out.println("Play");
        });

        cardView.setOnMouseEntered(e -> {
            cardView.getButtonBox().setVisible(true);
        });

        cardView.setOnMouseExited(e -> {
            cardView.getButtonBox().setVisible(false);
        });
    }
}
