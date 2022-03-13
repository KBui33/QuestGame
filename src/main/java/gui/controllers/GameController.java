package gui.controllers;

import game.components.card.*;
import gui.panes.GamePane;
import gui.partials.CardView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import model.Command;
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
            GameCommand getAttachedPlayerCommand = new GameCommand(Command.GET_ATTACHED_PLAYER);
            getAttachedPlayerCommand.setPlayerId(client.getPlayerId());
            GameCommand returnedAttachedPlayerCommand = client.sendCommand(getAttachedPlayerCommand);
            player = (Player) returnedAttachedPlayerCommand.getPlayer();
            System.out.println("== My Player: \n\t" + player);

                        // Subscribe to command updates
            client.clientEvents.subscribe(Client.ClientEvent.GAME_COMMAND_RECEIVED, new ClientEventListener() {
                @Override
                public void update(Client.ClientEvent eventType, Object o) {
                    GameCommand receivedCommand = (GameCommand) o;
                    Command command = receivedCommand.getCommand();
                    System.out.println("== Game Controller command update says: " + receivedCommand);
                    if(command.equals(Command.PLAYER_TURN) && receivedCommand.getPlayerId() == client.getPlayerId()) { // Take turn if it's player's turn
                        System.out.println("== It's my turn. Player: " + receivedCommand.getPlayerId());
                        view.getHud().getCurrentStateText().setText("Take your turn!");
                        disableView(false);
                        view.getHud().getEndTurnButton().setVisible(true);
                    } else if(command.equals(Command.SHOULD_SPONSOR_QUEST)) { // Prompt player to sponsor quest
                        System.out.println("== It's my turn to decide to sponsor the quest");
                        Card questCard = receivedCommand.getCard();
                        Platform.runLater(() -> {
                            view.getHud().getCurrentStateText().setText("Quest: " + questCard.getTitle() + "\nDo you want to sponsor this quest?");
                            System.out.println(questCard.getCardImg());
                            handleDrawnCard(questCard);
                            disableView(false);
                            view.getHud().getEndTurnButton().setVisible(false);
                        });
                    } else if(command.equals(Command.PLAYER_QUEST_TURN)) { // Handle taking quest turn
                        System.out.println("== It's my turn to take turn for quest stage");
                        Card questStageCard = receivedCommand.getCard();
                        view.getHud().getCurrentStateText().setText("Quest Stage: " + questStageCard.getTitle());
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
                    Card currentStoryCard = externalGameState.getCurrentStoryCard();
                    if(currentStoryCard != null) // Display this on GUI
                        System.out.println("Game Controller state update says: Current story " + currentStoryCard.getClass() + " -> " +  currentStoryCard.getTitle());
                }
            });

        } catch(IOException err) {
            err.printStackTrace();
        }

        setView();
    }

    public void setView() {

        // link lists to listviews
        view.getHud().getMyHand().setListViewItems(myHand);
        view.getHud().getDiscardedCards().setListViewItems(discarded);

        // Add player cards to gui cards
        for (Card card : player.getCards()) {
            addCardToHand(myHand, card);
        }

        disableView(true);
        view.getHud().getCurrentStateText().setText("Wait for your turn!");

        // a lot of this is just for laying out gui will be removed later
        view.getHud().getShieldsView().setShields(1);


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

        view.getHud().getEndTurnButton().setOnAction(e -> {
            System.out.println("Turn ended");
            // Send end turn command
            GameCommand endTurnCommand = new GameCommand(Command.END_TURN);
            endTurnCommand.setPlayerId(client.getPlayerId());
            endTurnCommand.setPlayer(player);
            client.sendCommand(endTurnCommand);

            disableView(true);
            view.getHud().getCurrentStateText().setText("Wait for your turn");

        });

        view.getHud().getShowHandButton().setOnAction(e -> {
            System.out.println("showing hand");
            // show and hide hand
            clearCardViewButtonsHighlight();
            if (view.getHud().getBottom() != null && view.getHud().getBottom().equals(view.getHud().getMyHand().getListView())) {
                view.getHud().setBottom(null);
            } else {
                view.getHud().setBottom(view.getHud().getMyHand().getListView());
                view.getHud().getShowHandButton().getStyleClass().add("caution");
            }
        });

        view.getHud().getShowDiscardedButton().setOnAction(e -> {
            System.out.println("showing discarded");
            // show and hide discarded
            clearCardViewButtonsHighlight();
            if (view.getHud().getBottom() != null && view.getHud().getBottom().equals(view.getHud().getDiscardedCards().getListView())) {
                view.getHud().setBottom(null);
            } else {
                view.getHud().setBottom(view.getHud().getDiscardedCards().getListView());
                view.getHud().getShowDiscardedButton().getStyleClass().add("caution");
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
        CardView drawnCard = new CardView();
        drawnCard.getButtonBox().setVisible(true);
        view.getMainPane().add(drawnCard, Pos.CENTER, true);
        drawnCard.setCard(card);

        // if it is a quest card offer player option to sponsor or decline card
        if (drawnCard.getCard() instanceof QuestCard) {
            drawnCard.getPlayButton().setText("Sponsor");
            drawnCard.getDiscardButton().setText("Decline");

            drawnCard.getPlayButton().setOnAction(e -> {
                System.out.println("played card");
                view.getMainPane().remove(drawnCard);
                questSetup((QuestCard) drawnCard.getCard());
            });

            drawnCard.getDiscardButton().setOnAction(e -> {
                System.out.println("discarded card");
                // send decline to server
                GameCommand declineSponsorQuestCommand = new GameCommand(Command.WILL_NOT_SPONSOR_QUEST);
                declineSponsorQuestCommand.setPlayerId(client.getPlayerId());
                declineSponsorQuestCommand.setPlayer(player);
                GameCommand declinedSponsorQuestCommand =  client.sendCommand(declineSponsorQuestCommand);
                player = declinedSponsorQuestCommand.getPlayer();

                view.getMainPane().remove(drawnCard);
            });
        }
    }

    private void discardCard(CardView card) {
        // Send discard command
        // Send discard card command
        GameCommand discardCardCommand = new GameCommand(Command.DISCARD_CARD);
        discardCardCommand.setPlayerId(client.getPlayerId());
        discardCardCommand.setPlayer(player);
        discardCardCommand.setCard(card.getCard());
        GameCommand discardedCardCommand =  client.sendCommand(discardCardCommand);
        player = discardedCardCommand.getPlayer();

        myHand.remove(card);
    }

    private void disableView(Boolean disable) {
        if (disable) {
            view.getHud().getCardButtons().setVisible(false);
            clearCardViewButtonsHighlight();
        } else {
            view.getHud().getCardButtons().setVisible(true);
        }
        disableDecks(disable);
        hideDecks();
    }

    private void disableDecks(Boolean disable) {
        if (disable) {
            view.getHud().getDeckButtons().setVisible(false);
        } else {
            view.getHud().getDeckButtons().setVisible(true);
        }
    }

    public void hideDecks() {
        view.getHud().setBottom(null);
        clearCardViewButtonsHighlight();
    }

    public void showHand() {
        clearCardViewButtonsHighlight();
        view.getHud().setBottom(view.getHud().getMyHand().getListView());
        view.getHud().getShowHandButton().getStyleClass().add("caution");
    }

    private void clearCardViewButtonsHighlight() {
        view.getHud().getShowDiscardedButton().getStyleClass().remove("caution");
        view.getHud().getShowHandButton().getStyleClass().remove("caution");
    }

    private void addCardToHand(ObservableList<CardView> hand, Card card) {
        CardView newcard = new CardView(card);
        setCardViewButtonActions(newcard);
        hand.add(0, newcard);
    }

    private void questSetup(QuestCard questCard) {
        view.getHud().getEndTurnButton().setVisible(false);
        QuestSetupController qsc = new QuestSetupController(this, questCard);
        view.getMainPane().add(qsc.getView(), Pos.CENTER, false);
    }

    public void questSetupComplete(Quest quest) {
        System.out.println("== Quest setup completed");
        // send decline to server
        GameCommand questSetupCommand = new GameCommand(Command.WILL_SPONSOR_QUEST);
        questSetupCommand.setPlayerId(client.getPlayerId());
        questSetupCommand.setPlayer(player);
        questSetupCommand.setQuest(quest);
        client.sendCommand(questSetupCommand);
    }

    public void setCardViewButtonActions(CardView cardView) {
        cardView.getDiscardButton().setOnAction(e -> {
            // send delete signal to server and await response
            System.out.println("Discarding card");

            // Send discard card command
            discardCard(cardView);

        });

        cardView.getPlayButton().setVisible(false);
        cardView.getPlayButton().setText("Play");
        cardView.getDiscardButton().setText("Discard");

        cardView.setOnMouseEntered(e -> {
            cardView.getButtonBox().setVisible(true);
        });

        cardView.setOnMouseExited(e -> {
            cardView.getButtonBox().setVisible(false);
        });
    }


}
