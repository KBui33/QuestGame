package gui.controllers;

import game.components.card.AllyCard;
import game.components.card.Card;
import gui.panes.GamePane;
import gui.partials.CardView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.GameCommand;
import model.Player;
import networking.client.Client;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author James DiNovo
 *
 * Controller for manipulating GamePane view
 *
 */
public class GameController {

    private Client client;
    private Player player;
    private ObservableList<CardView> myHand;
    private ObservableList<CardView> discarded;

    public GameController (GamePane view) {
        myHand = FXCollections.observableArrayList();
        view.getMyHand().setListViewItems(myHand);

        discarded = FXCollections.observableArrayList();
        view.getDiscardedCards().setListViewItems(discarded);

        // Fetch corresponding player
        try {
            client = Client.getInstance();
            GameCommand getAttachedPlayerCommand = new GameCommand(GameCommand.Command.GET_ATTACHED_PLAYER);
            getAttachedPlayerCommand.setPlayerId(client.getPlayerId());
            GameCommand returnedAttachedPlayerCommand = client.sendCommand(getAttachedPlayerCommand);
            player = (Player) returnedAttachedPlayerCommand.getPlayer();
            System.out.println(player);

        } catch(IOException err) {
            err.printStackTrace();
        }

        setView(view);
    }

    public void setView(GamePane view) {

        // Add player cards to gui cards
        for (Card card : player.getCards()) {
            addCardToHand(myHand, card);
        }

        // a lot of this is just for laying out gui will be removed later
        view.getCurrentStateText().setText("Your turn!");
        view.getShieldsView().setShields(1);


        // set action for draw card button
        view.getDrawCardButton().setOnAction(e -> {
            // draw a card from server
            // hardcoded for testing
            Random r = new Random();
            Card drawnCard = new AllyCard(
                    "card",
                    "/specials/quest_ally_" + (r.nextInt(10) + 1) + ".png",
                    "");


            // once hand has more than 12 cards every next card drawn must be either played or discarded
            if (myHand.size() < 12) {
                addCardToHand(myHand, drawnCard);
            } else {
                // display card with option to play it or discard it
                view.getDrawnCard().setCard(drawnCard);
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

            // temp behaviour for testing/demo
            discarded.add(new CardView(view.getDrawnCard().getCard()));
        });

        view.getEndTurnButton().setOnAction(e -> {
            System.out.println("Turn ended");
            disableView(view, true);
            view.getCurrentStateText().setText("Player " + client.getPlayerId() + "'s turn...");
            // just for demonstration
            Timer t = new Timer();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    disableView(view, false);
                    view.getCurrentStateText().setText("Your turn!");
                }
            };

            t.schedule(tt, 5000);

        });

        view.getShowHandButton().setOnAction(e -> {
            System.out.println("showing hand");
            // show and hide hand
            clearCardViewButtonsHighlight(view);
            if (view.getBottom() != null && view.getBottom().equals(view.getMyHand().getListView())) {
                view.setBottom(null);
            } else {
                view.setBottom(view.getMyHand().getListView());
                view.getShowHandButton().getStyleClass().add("caution");
            }
        });

        view.getShowDiscardedButton().setOnAction(e -> {
            System.out.println("showing discarded");
            // show and hide discarded
            clearCardViewButtonsHighlight(view);
            if (view.getBottom() != null && view.getBottom().equals(view.getDiscardedCards().getListView())) {
                view.setBottom(null);
            } else {
                view.setBottom(view.getDiscardedCards().getListView());
                view.getShowDiscardedButton().getStyleClass().add("caution");
            }
        });
    }

    private void disableView(GamePane view, Boolean disable) {
        if (disable) {
            view.setBottom(null);
            view.getDeckButtons().setVisible(false);
            view.getCardButtons().setVisible(false);
            clearCardViewButtonsHighlight(view);
        } else {
            view.getDeckButtons().setVisible(true);
            view.getCardButtons().setVisible(true);
        }
    }

    private void clearCardViewButtonsHighlight(GamePane view) {
        view.getShowDiscardedButton().getStyleClass().remove("caution");
        view.getShowHandButton().getStyleClass().remove("caution");
    }

    private void addCardToHand(ObservableList<CardView> hand, Card card) {
        CardView newcard = new CardView(card);
        setCardViewButtonActions(hand, newcard);
        hand.add(0, newcard);
    }

    private void setCardViewButtonActions(ObservableList<CardView> deckView, CardView cardView) {
        cardView.getDiscardButton().setOnAction(e -> {
            // send delete signal to server and await response
            deckView.remove(cardView);

            // TEMPORARY BEHAVIOUR FOR LAYOUT TESTING
            discarded.add(new CardView(cardView.getCard()));
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
