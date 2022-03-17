package gui.controllers;

import game.components.card.*;
import gui.other.AlertBox;
import gui.panes.GamePane;
import gui.partials.CardView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import model.*;
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

    private Client client;
    private Player player;
    private ObservableList<CardView> myHand;
    private ObservableList<CardView> discarded;
    private GamePane view;
    private QuestController questController;

    public GameController (GamePane view) {
        myHand = FXCollections.observableArrayList();
        discarded = FXCollections.observableArrayList();
        this.view = view;

        // for reference inside handlers
        GameController gc = this;

        try {
            client = Client.getInstance();

            // Fetch corresponding player
            GameCommand getAttachedPlayerCommand = new GameCommand(Command.GET_ATTACHED_PLAYER);
            getAttachedPlayerCommand.setPlayerId(client.getPlayerId());
            getAttachedPlayerCommand.setClientIndex(client.getClientIndex());
            GameCommand returnedAttachedPlayerCommand = client.sendCommand(getAttachedPlayerCommand);
            player = returnedAttachedPlayerCommand.getPlayer();
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
                    } else if(command.equals(Command.SHOULD_SPONSOR_QUEST)) { // Prompt player to sponsor quest
                        System.out.println("== It's my turn to decide to sponsor the quest");
                        Card questCard = receivedCommand.getCard();
                        Platform.runLater(() -> {
                            view.getHud().getCurrentStateText().setText("Quest: " + questCard.getTitle() + "\nDo you want to sponsor this quest?");
                            System.out.println(questCard.getCardImg());
                            questSponsor(questCard);
                            disableView(false);
                        });
                    } else if(command.equals(Command.SHOULD_JOIN_QUEST)) { // Prompt player to join quest
                        System.out.println("== It's my turn to decide to join the quest");
                        Card questCard = receivedCommand.getCard();
                        Platform.runLater(() -> {
                            view.getHud().getCurrentStateText().setText("Quest: " + questCard.getTitle() + "\nDo you want to join this quest?");
                            System.out.println(questCard.getCardImg());
                            // TODO::Prompt player to join quest
                            questJoin(questCard);
                            disableView(false);
                        });
                    } else if(command.equals(Command.PLAYER_TAKE_STAGE_CARD)) { // Handle accepting/discarding quest stage card
                        System.out.println("== It's my turn to accept/discard stage card");
                        Card questStageAdventureCard = receivedCommand.getCard();
                        Quest quest = receivedCommand.getQuest();

                    } else if(command.equals(Command.PLAYER_QUEST_TURN)) { // Handle taking quest turn
                        System.out.println("== It's my turn to take turn for quest stage");
                        Card questStageCard = receivedCommand.getCard();
                        Quest quest = receivedCommand.getQuest();
                        view.getHud().getCurrentStateText().setText("Quest Stage: " + (questStageCard instanceof FoeCard ? "Foe" : "Test"));
                        System.out.println("== Quest Card: " + questStageCard.getTitle());

                        Platform.runLater(() -> {
                            // if quest controller is null you are sponsor i guess? not really -> NO NEED: If sponsoring, will never receive this command
                            try {
                                while (questController == null) Thread.sleep(1000); // Wait for quest controller to be setup
                            } catch (InterruptedException e) {e.printStackTrace();}

                            view.getMainPane().clear();
                            view.getMainPane().add(questController.getQuestView());
                            questController.pickCards(gc, quest);
                            disableView(false);
                        });
                    } else if(command.equals(Command.QUEST_STAGE_WON)) { // Handle end quest stage turn when stage won -> continue quest
                        System.out.println("== I just won this stage. Continuing...");
                        Quest quest = receivedCommand.getQuest();
                        Stage currentStage = quest.getCurrentStage();
                        view.getHud().getCurrentStateText().setText("Quest Stage: " + currentStage.getStageCard().getTitle());

                        // Should show button to continue
                        Platform.runLater(() -> questController.stageComplete(gc, quest, true));

                    } else if(command.equals(Command.QUEST_STAGE_LOST)) { // Handle end quest stage turn when stage lost -> sit out of quest
                        System.out.println("== I just lost this stage. Sitting out...");
                        Quest quest = receivedCommand.getQuest();
                        Stage currentStage = quest.getCurrentStage();
                        view.getHud().getCurrentStateText().setText("Quest Stage: " + currentStage.getStageCard().getTitle());

                        // Should show sit out of quest button
                        Platform.runLater(() -> questController.stageComplete(gc, quest, false));

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

                    // needs quest to initialize quest controller
                    Quest q = externalGameState.getCurrentQuest();
                    if (q != null) {
                        if(questController == null) {
                            Platform.runLater(() -> {
                                questController = new QuestController(q);
                            });
                        } else if(externalGameState.getGameStatus().equals(GameStatus.RUNNING_QUEST) && q.getSponsor().getPlayerId() != player.getPlayerId() && q.getQuestPlayerByPlayerId(player.getPlayerId()) == null) { // Check if player is still in quest
                            System.out.println("== Game Controller state update says: You have fallen out of the quest");
                            view.getHud().getCurrentStateText().setText("Quest Stage: Sitting out till the end of the quest");
                        }
                    }
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

        // hide endturn button for now
        view.getHud().getEndTurnButton().setVisible(false);

        waitTurn();

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
            endTurnCommand.setClientIndex(client.getClientIndex());
            GameCommand endedTurnCommand =  client.sendCommand(endTurnCommand);
            player = endedTurnCommand.getPlayer();
            waitTurn();
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

    public void setMyHandList(ObservableList<CardView> newHand) {
        this.myHand = newHand;
    }

    public GamePane getView() {
        return view;
    }

    public void cleanUpGui() {
        // clear quest setup
        // getView().getMainPane().getChildren().clear();

        // reset view
        getView().getHud().getEndTurnButton().setVisible(true);

        // fix list view - need better fix at some point
        ObservableList<CardView> tmp = FXCollections.observableArrayList();

        getMyHandList().forEach(c -> {
            CardView n = new CardView(c.getCard());
            setCardViewButtonActions(n);
            tmp.add(n);
        });
        setMyHandList(tmp);
        getView().getHud().getMyHand().setListViewItems(getMyHandList());
    }

    public void playerStageContinue() {
        // send quest turn complete command to server
        GameCommand endQuestTurnCommand = new GameCommand(Command.END_QUEST_TURN);
        endQuestTurnCommand.setPlayerId(client.getPlayerId());
        endQuestTurnCommand.setClientIndex(client.getClientIndex());
        endQuestTurnCommand.setPlayer(player);
        GameCommand endedQuestTurnCommand =  client.sendCommand(endQuestTurnCommand);
        if (endedQuestTurnCommand.getPlayer() != null) player = endedQuestTurnCommand.getPlayer();
    }

    public void playerStageCardsPicked(ArrayList<Card> weaponCards) {
        disableView(true);
        // send cards to server
        GameCommand takeQuestTurnCommand = new GameCommand(Command.TAKE_QUEST_TURN);
        takeQuestTurnCommand.setPlayerId(client.getPlayerId());
        takeQuestTurnCommand.setClientIndex(client.getClientIndex());
        takeQuestTurnCommand.setPlayer(player);
        takeQuestTurnCommand.setCards(weaponCards);
        GameCommand tookQuestTurnCommand =  client.sendCommand(takeQuestTurnCommand);
        player = tookQuestTurnCommand.getPlayer();
        waitTurn();
    }

    private void waitTurn() {
        disableView(true);
        view.getHud().getCurrentStateText().setText("Wait for your turn");
    }

    private void takeTurn() {
        disableView(false);
    }

    private void questJoin(Card card) {
        CardView drawnCard = new CardView(card, true, "Join", "Decline");

        displayCard(drawnCard, e -> {
            // player chooses join
            // Send will join command to server
            GameCommand willJoinQuestCommand = new GameCommand(Command.WILL_JOIN_QUEST);
            willJoinQuestCommand.setPlayerId(client.getPlayerId());
            willJoinQuestCommand.setClientIndex(client.getClientIndex());
            willJoinQuestCommand.setPlayer(player);
            GameCommand joinedQuestCommand =  client.sendCommand(willJoinQuestCommand);
            if (joinedQuestCommand.getPlayer() != null) player = joinedQuestCommand.getPlayer();
            view.getMainPane().remove(drawnCard);
            waitTurn();
        }, e -> {
            // player chooses decline
            // Send will not join command to server
            GameCommand willNotJoinQuestCommand = new GameCommand(Command.WILL_NOT_JOIN_QUEST);
            willNotJoinQuestCommand.setPlayerId(client.getPlayerId());
            willNotJoinQuestCommand.setClientIndex(client.getClientIndex());
            willNotJoinQuestCommand.setPlayer(player);
            GameCommand didNotJoinQuestCommand =  client.sendCommand(willNotJoinQuestCommand);
            if (didNotJoinQuestCommand.getPlayer() != null) player = didNotJoinQuestCommand.getPlayer();
            view.getMainPane().remove(drawnCard);
            waitTurn();
        });
    }

    private void questSponsor(Card card) {
        CardView drawnCard = new CardView(card, true, "Sponsor", "Decline");

        displayCard(drawnCard, e -> {
            if (myHand.filtered(c -> c.getCard() instanceof FoeCard || c.getCard() instanceof TestCard).size()
                    < ((QuestCard) drawnCard.getCard()).getStages()) {
                // notify user they dont have enough cards to sponsor
                AlertBox.alert("Insufficient cards in hand. This Quest requires at least "
                        + ((QuestCard) drawnCard.getCard()).getStages() +
                        " Foe or Test cards to sponsor.", Alert.AlertType.WARNING, e2 -> {

                    // send decline to server
                    GameCommand declineSponsorQuestCommand = new GameCommand(Command.WILL_NOT_SPONSOR_QUEST);
                    declineSponsorQuestCommand.setPlayerId(client.getPlayerId());
                    declineSponsorQuestCommand.setClientIndex(client.getClientIndex());
                    declineSponsorQuestCommand.setPlayer(player);
                    GameCommand declinedSponsorQuestCommand =  client.sendCommand(declineSponsorQuestCommand);
                    player = declinedSponsorQuestCommand.getPlayer();

                    drawnCard.getDiscardButton().fire();
                });
            } else {
                view.getMainPane().remove(drawnCard);
                questSetup((QuestCard) drawnCard.getCard());
            }
        }, e -> {
            // send decline to server
            GameCommand declineSponsorQuestCommand = new GameCommand(Command.WILL_NOT_SPONSOR_QUEST);
            declineSponsorQuestCommand.setPlayerId(client.getPlayerId());
            declineSponsorQuestCommand.setClientIndex(client.getClientIndex());
            declineSponsorQuestCommand.setPlayer(player);
            GameCommand declinedSponsorQuestCommand =  client.sendCommand(declineSponsorQuestCommand);
            player =  declinedSponsorQuestCommand.getPlayer();
            view.getMainPane().remove(drawnCard);

            waitTurn();
        });
    }

    private void displayCard(CardView cardView,
                             EventHandler<ActionEvent> posButtonEvent,
                             EventHandler<ActionEvent> negButtonEvent) {

        cardView.setButtonEvents(posButtonEvent, negButtonEvent);

        // add cardview to center of main pane
        // might want to add card stack pane as its own thing later on.....
        view.getMainPane().add(cardView, Pos.CENTER, true);
    }

    private void discardCard(CardView card) {
        // Send discard card command
        GameCommand discardCardCommand = new GameCommand(Command.DISCARD_CARD);
        discardCardCommand.setPlayerId(client.getPlayerId());
        discardCardCommand.setClientIndex(client.getClientIndex());
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
        // send sponsor to server
        GameCommand questSetupCompleteCommand = new GameCommand(Command.WILL_SPONSOR_QUEST);
        questSetupCompleteCommand.setPlayerId(client.getPlayerId());
        questSetupCompleteCommand.setClientIndex(client.getClientIndex());
        questSetupCompleteCommand.setPlayer(player);
        questSetupCompleteCommand.setQuest(quest);
        GameCommand questSetupCompletedCommand =  client.sendCommand(questSetupCompleteCommand);
        player = questSetupCompletedCommand.getPlayer();
        waitTurn();
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
