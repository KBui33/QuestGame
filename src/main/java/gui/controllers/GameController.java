package gui.controllers;

import gui.panes.GamePane;
import gui.partials.CardView;
import gui.partials.DeckView;
import javafx.scene.image.Image;

/**
 * @author James DiNovo
 *
 * Controller for manipulating GamePane view
 *
 */
public class GameController {
    public GameController (GamePane view) {
        setView(view);
    }

    public void setView(GamePane view) {
        // a lot of this is just for laying out gui will be removed later
        view.getCurrentStateText().setText("Your turn!");
        view.getShieldsView().setShields(1);

        for (int i = 1; i <= 11; i++) {
            view.getMyHand().addCard("/specials/quest_ally_" + i + ".png");
        }

        for (int i = 1; i <= 11; i++) {
            view.getDiscardedCards().addCard("/foes/quest_foe_" + i + ".png");
        }

        // set button actions for hand cards
        view.getMyHand().getList().forEach(cardView -> {
            setCardViewButtonActions(view.getMyHand(), cardView);
        });

        // set action for draw card button
        view.getDrawCardButton().setOnAction(e -> {
            // draw a card from server

            // once hand has more than 12 cards every next card drawn must be either played or discarded
            if (view.getMyHand().getList().size() < 12) {
                addCardToHand(view.getMyHand(), new Image(String.valueOf(getClass().getResource("/specials/quest_ally_4.png"))));
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
    private void addCardToHand(DeckView hand, Image card) {
        CardView newcard = new CardView(card);
        hand.addCard(newcard);
        setCardViewButtonActions(hand, newcard);
    }

    private void setCardViewButtonActions(DeckView deckView, CardView cardView) {
        cardView.getDiscardButton().setOnAction(e -> {
            // send delete signal to server and await response
            deckView.removeCard(cardView);
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
