package gui.controllers;

import game.components.card.AllyCard;
import game.components.card.Card;
import game.components.card.QuestCard;
import gui.main.ClientApplication;
import gui.panes.GamePane;
import gui.partials.CardView;
import gui.scenes.GameScene;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import model.ExternalGameState;
import model.GameCommand;
import model.Player;
import networking.client.Client;
import networking.client.ClientEventListener;

import java.io.IOException;
import java.util.ArrayList;

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

        // Fetch corresponding player
        try {
            Client client = Client.getInstance();

            GameCommand getAttachedPlayerCommand = new GameCommand(GameCommand.Command.GET_ATTACHED_PLAYER);
            getAttachedPlayerCommand.setPlayerId(client.getPlayerId());
            GameCommand returnedAttachedPlayerCommand = client.sendCommand(getAttachedPlayerCommand);
            Player player = (Player) returnedAttachedPlayerCommand.getPlayer();
            System.out.println("== My Player: \n\t" + player);

            // Subscribe to command updates
            client.clientEvents.subscribe(Client.ClientEvent.GAME_COMMAND_RECEIVED, new ClientEventListener() {
                @Override
                public void update(Client.ClientEvent eventType, Object o) {
                    GameCommand receivedCommand = (GameCommand) o;
                    System.out.println("== Game Controller command update says: " + receivedCommand);
                    if(receivedCommand.getCommand().equals(GameCommand.Command.PLAYER_TURN) && receivedCommand.getPlayerId() == client.getPlayerId()) { // Take turn if it's player's turn
                        System.out.println("== It's my turn. Player: " + receivedCommand.getPlayerId());
                    }
                }
            });

            // Subscribe to game state updates
            client.clientEvents.subscribe(Client.ClientEvent.EXTERNAL_GAME_STATE_UPDATED, new ClientEventListener() {
                @Override
                public void update(Client.ClientEvent eventType, Object o) {
                    ExternalGameState externalGameState = (ExternalGameState) o;
                    System.out.println("== Game Controller game state update says: " + externalGameState);
                }
            });

        } catch(IOException err) {
            err.printStackTrace();
        }

        myHand = FXCollections.observableArrayList();
        view.getMyHand().setListViewItems(myHand);

        discarded = FXCollections.observableArrayList();
        view.getDiscardedCards().setListViewItems(discarded);

        // a lot of this is just for laying out gui will be removed later
        view.getCurrentStateText().setText("Your turn!");
        view.getShieldsView().setShields(1);

//        for (int i = 1; i <= 11; i++) {
//            addCardToHand(myHand, new AllyCard(
//                    new Image(String.valueOf(getClass().getResource("/specials/quest_ally_" + i + ".png"))),
//                    "card",
//                    ""));
//        }
//
//        for (int i = 1; i <= 10; i++) {
//            CardView cardView = new CardView(new QuestCard(
//                    new Image(String.valueOf(getClass().getResource("/quests/quest_quest_" + i + ".png"))),
//                    "card", 3, ""));
//            discarded.add(0, cardView);
//        }


        // set action for draw card button
        view.getDrawCardButton().setOnAction(e -> {
            // draw a card from server
//            Card drawnCard = new AllyCard(
//                    new Image(String.valueOf(getClass().getResource("/specials/quest_ally_3.png"))),
//                    "card",
//                    "");

            // once hand has more than 12 cards every next card drawn must be either played or discarded
            if (myHand.size() < 12) {
//                addCardToHand(myHand, drawnCard);
            } else {
                // display card with option to play it or discard it
//                view.getDrawnCard().setCard(drawnCard);
//                view.setCenter(view.getDrawnCard());
//                view.getDrawCardButton().setDisable(true);
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

    private void addCardToHand(ObservableList<CardView> hand, Card card) {
        CardView newcard = new CardView(card);
        setCardViewButtonActions(hand, newcard);
        hand.add(0, newcard);
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
