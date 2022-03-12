package gui.controllers;

import game.components.card.*;
import gui.panes.GamePane;
import gui.partials.CardView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import model.ExternalGameState;
import model.GameCommand;
import model.Player;
import model.Quest;
import networking.client.Client;
import networking.client.ClientEventListener;

import java.io.IOException;

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
    private GamePane view;

    public GameController (GamePane view) {
        myHand = FXCollections.observableArrayList();
        discarded = FXCollections.observableArrayList();
        this.view = view;

        try {
            client = Client.getInstance();

            // Fetch corresponding player
            GameCommand getAttachedPlayerCommand = new GameCommand(GameCommand.Command.GET_ATTACHED_PLAYER);
            getAttachedPlayerCommand.setPlayerId(client.getPlayerId());
            GameCommand returnedAttachedPlayerCommand = client.sendCommand(getAttachedPlayerCommand);
            player = (Player) returnedAttachedPlayerCommand.getPlayer();
            System.out.println("== My Player: \n\t" + player);

                        // Subscribe to command updates
            client.clientEvents.subscribe(Client.ClientEvent.GAME_COMMAND_RECEIVED, new ClientEventListener() {
                @Override
                public void update(Client.ClientEvent eventType, Object o) {
                    GameCommand receivedCommand = (GameCommand) o;
                    System.out.println("== Game Controller command update says: " + receivedCommand);
                    if(receivedCommand.getCommand().equals(GameCommand.Command.PLAYER_TURN) && receivedCommand.getPlayerId() == client.getPlayerId()) { // Take turn if it's player's turn
                        System.out.println("== It's my turn. Player: " + receivedCommand.getPlayerId());
                        view.getCurrentStateText().setText("Take your turn!");

                        // get drawn story card from server
                        // handle it
                        Platform.runLater(() -> {
                            handleDrawnCard(new QuestCard("test", "/quests/quest_quest_1.png", 3, "Evil Knight"));
                        });
                        disableView(false);
                    }
                }
            });

            // Subscribe to game state updates
            client.clientEvents.subscribe(Client.ClientEvent.EXTERNAL_GAME_STATE_UPDATED, new ClientEventListener() {
                @Override
                public void update(Client.ClientEvent eventType, Object o) {
                    ExternalGameState externalGameState = (ExternalGameState) o;
                    Platform.runLater(() -> {
                        discarded.clear();
                        for (Card card : externalGameState.getDiscardedCards()) {
                            discarded.add(new CardView(card));
                        }
                    });
                }
            });

        } catch(IOException err) {
            err.printStackTrace();
        }

        setView();
    }

    public void setView() {

        // link lists to listviews
        view.getMyHand().setListViewItems(myHand);
        view.getDiscardedCards().setListViewItems(discarded);

        // Add player cards to gui cards
        for (Card card : player.getCards()) {
            addCardToHand(myHand, card);
        }

        disableView(true);
        view.getCurrentStateText().setText("Wait for your turn!");

        // a lot of this is just for laying out gui will be removed later
        view.getShieldsView().setShields(1);


        // set action for draw card button
//        view.getDrawCardButton().setOnAction(e -> {
//            // draw a card from server
//            // hardcoded for testing
//            Random r = new Random();
//            Card drawnCard = new AllyCard(
//                    "card",
//                    "/specials/quest_ally_" + (r.nextInt(10) + 1) + ".png",
//                    "", "");
//
//
//            // once hand has more than 12 cards every next card drawn must be either played or discarded
//            if (myHand.size() < 12) {
//                addCardToHand(myHand, drawnCard);
//            } else {
//                // display card with option to play it or discard it
//                view.getDrawnCard().setCard(drawnCard);
//                view.addToCenterScreen(view.getDrawnCard(), Pos.CENTER, 100);
//                view.getDrawCardButton().setDisable(true);
//            }
//        });

        view.getEndTurnButton().setOnAction(e -> {
            System.out.println("Turn ended");
            // Send end turn command
            GameCommand endTurnCommand = new GameCommand(GameCommand.Command.END_TURN);
            endTurnCommand.setPlayerId(client.getPlayerId());
            endTurnCommand.setPlayer(player);
            client.sendCommand(endTurnCommand);

            disableView(true);
            view.getCurrentStateText().setText("Wait for your turn");

        });

        view.getShowHandButton().setOnAction(e -> {
            System.out.println("showing hand");
            // show and hide hand
            clearCardViewButtonsHighlight();
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
            clearCardViewButtonsHighlight();
            if (view.getBottom() != null && view.getBottom().equals(view.getDiscardedCards().getListView())) {
                view.setBottom(null);
            } else {
                view.setBottom(view.getDiscardedCards().getListView());
                view.getShowDiscardedButton().getStyleClass().add("caution");
            }
        });
    }

    public ObservableList<CardView> getDiscardedList() {
        return discarded;
    }

    public ObservableList<CardView> getMyHandList() {
        return myHand;
    }

    public GamePane getView() {
        return view;
    }

    private void handleDrawnCard(Card card) {
        view.getDrawnCard().setCard(card);
        view.getDrawnCard().getPlayButton().setText("Sponsor");
        view.getDrawnCard().getDiscardButton().setText("Decline");

        view.addToCenterScreen(view.getDrawnCard(), Pos.CENTER, 100);

        // if it is a quest card offer player option to sponsor or decline card
        if (view.getDrawnCard().getCard() instanceof QuestCard) {
            view.getDrawnCard().getPlayButton().setOnAction(e -> {
                System.out.println("played card");
                view.removeFromCenterScreen(view.getDrawnCard());
                questSetup((QuestCard) view.getDrawnCard().getCard());
            });

            view.getDrawnCard().getDiscardButton().setOnAction(e -> {
                System.out.println("discarded card");
                view.removeFromCenterScreen(view.getDrawnCard());
                // send decline to server
            });
        }
    }

    private void discardCard(CardView card) {
        // Send discard command
        // Send discard card command
        GameCommand discardCardCommand = new GameCommand(GameCommand.Command.DISCARD_CARD);
        discardCardCommand.setPlayerId(client.getPlayerId());
        discardCardCommand.setPlayer(player);
        discardCardCommand.setCard(card.getCard());
        GameCommand discardedCardCommand =  client.sendCommand(discardCardCommand);
        player = discardedCardCommand.getPlayer();

        myHand.remove(card);
    }

    private void disableView(Boolean disable) {
        if (disable) {
            view.getCardButtons().setVisible(false);
            clearCardViewButtonsHighlight();
        } else {
            view.getCardButtons().setVisible(true);
        }
        disableDecks(disable);
        hideDecks();
    }

    private void disableDecks(Boolean disable) {
        if (disable) {
            view.getDeckButtons().setVisible(false);
        } else {
            view.getDeckButtons().setVisible(true);
        }
    }

    public void hideDecks() {
        view.setBottom(null);
        clearCardViewButtonsHighlight();
    }

    public void showHand() {
        clearCardViewButtonsHighlight();
        view.setBottom(view.getMyHand().getListView());
        view.getShowHandButton().getStyleClass().add("caution");
    }

    private void clearCardViewButtonsHighlight() {
        view.getShowDiscardedButton().getStyleClass().remove("caution");
        view.getShowHandButton().getStyleClass().remove("caution");
    }

    private void addCardToHand(ObservableList<CardView> hand, Card card) {
        CardView newcard = new CardView(card);
        setCardViewButtonActions(hand, newcard);
        hand.add(0, newcard);
    }

    private void questSetup(QuestCard questCard) {
        view.getEndTurnButton().setVisible(false);
        QuestSetupController qsc = new QuestSetupController(this, questCard);
        view.addToCenterScreen(qsc.getView(), Pos.CENTER, 1);
    }

    public void questSetupComplete(Quest quest) {

        // return quest to server

    }

    private void setCardViewButtonActions(ObservableList<CardView> deckView, CardView cardView) {
        cardView.getDiscardButton().setOnAction(e -> {
            // send delete signal to server and await response
            System.out.println("Discarding card");

            // Send discard card command
            discardCard(cardView);

            // TEMPORARY BEHAVIOUR FOR LAYOUT TESTING
            //discarded.add(new CardView(cardView.getCard()));
        });

        cardView.getPlayButton().setVisible(false);

        cardView.setOnMouseEntered(e -> {
            cardView.getButtonBox().setVisible(true);
        });

        cardView.setOnMouseExited(e -> {
            cardView.getButtonBox().setVisible(false);
        });
    }
}
