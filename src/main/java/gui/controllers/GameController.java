package gui.controllers;

import component.card.*;
import gui.controllers.quest.QuestController;
import gui.controllers.quest.QuestSetupController;
import gui.controllers.tournament.TournamentController;
import gui.main.ClientApplication;
import gui.other.AlertBox;
import gui.panes.GamePane;
import gui.partials.CardView;
import gui.partials.EndGameView;
import gui.scenes.LobbyScene;
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
import java.util.concurrent.Callable;

/**
 * @author James DiNovo
 * <p>
 * Controller for manipulating GamePane view
 */
public class GameController {

    private Client client;
    private Player player;
    private ObservableList<CardView> myHand;
    private ObservableList<CardView> discarded;
    private GamePane view;
    private QuestController questController;
    private TournamentController tournamentController;
    private final GameController gc;

    private final ArrayList<Callable> eventSubscriptions = new ArrayList<>();

    public GameController(GamePane view) {
        myHand = FXCollections.observableArrayList();
        discarded = FXCollections.observableArrayList();
        this.view = view;

        // for reference inside handlers
        gc = this;

        try {
            client = Client.getInstance();

            // Fetch corresponding player
            GameCommand getAttachedPlayerCommand = defaultServerCommand(new GameCommand(GameCommandName.GET_ATTACHED_PLAYER));
            GameCommand returnedAttachedPlayerCommand = (GameCommand) client.sendCommand(getAttachedPlayerCommand);
            if (returnedAttachedPlayerCommand.getPlayer() != null)
                updatePlayer(returnedAttachedPlayerCommand.getPlayer());

            // Subscribe to command updates
            Callable<Void> unsubscribeCommandReceived = client.clientEvents.subscribe(Client.ClientEvent.GAME_COMMAND_RECEIVED, new ClientEventListener() {
                @Override
                public void update(Client.ClientEvent eventType, Object o) {

                    Command receivedCommand = (Command) o;

                    switch (receivedCommand.getCommandType()) {
                        case BASE:
                            handleBaseCommands((BaseCommand) receivedCommand);
                            break;
                        case GAME:
                            handleGameCommands((GameCommand) receivedCommand);
                            break;
                        case QUEST:
                            handleQuestCommands((QuestCommand) receivedCommand);
                            break;
                        case EVENT:
                            handleEventCommands((EventCommand) receivedCommand);
                            break;
                        case TOURNAMENT:
                            handleTournamentCommands((TournamentCommand) receivedCommand);
                            break;
                    }
                }
            });

            eventSubscriptions.add(unsubscribeCommandReceived);

            // Subscribe to game state updates
            Callable<Void> unsubscribeGameStateUpdate = client.clientEvents.subscribe(Client.ClientEvent.EXTERNAL_GAME_STATE_UPDATED, new ClientEventListener() {
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
                    if (currentStoryCard != null) // Display this on GUI
                        System.out.println("Game Controller state update says: Current story " + currentStoryCard.getClass() + " -> " + currentStoryCard.getTitle());

                    // needs quest to initialize quest controller
                    Quest q = externalGameState.getCurrentQuest();
                    if (q != null) {
                        if (questController == null) {
                            Platform.runLater(() -> {
                                questController = new QuestController(q, gc);
                            });
                        } else if (externalGameState.getGameStatus().equals(GameStatus.RUNNING_QUEST) && q.getSponsor().getPlayerId() != player.getPlayerId() && q.getQuestPlayerByPlayerId(player.getPlayerId()) == null) { // Check if player is still in quest
                            System.out.println("== Game Controller state update says: You have fallen out of the quest");
                            view.getHud().getCurrentStateText().setText("Quest Stage: Sitting out till the end of the quest");
                        }
                    }
                }
            });

            eventSubscriptions.add(unsubscribeGameStateUpdate);
        } catch (IOException err) {
            err.printStackTrace();
        }

        setView();
    }

    public void setView() {

        // link lists to listviews
        view.getHud().getMyHand().setListViewItems(myHand);
        view.getHud().getDiscardedCards().setListViewItems(discarded);

        // hide endturn button for now
        view.getHud().getEndTurnButton().setVisible(false);

        waitTurn();

        updatePlayer(player);

        view.getHud().getEndTurnButton().setOnAction(e -> {
            System.out.println("Turn ended");
            // Send end turn command
            GameCommand endTurnCommand = defaultServerCommand(new GameCommand(GameCommandName.END_TURN));
            GameCommand endedTurnCommand = (GameCommand) client.sendCommand(endTurnCommand);
            if (endedTurnCommand.getPlayer() != null) updatePlayer(endedTurnCommand.getPlayer());
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


    public void playerStageContinue() {
        // send quest turn complete command to server
        QuestCommand endQuestTurnCommand = (QuestCommand) defaultServerCommand(new QuestCommand(QuestCommandName.END_QUEST_TURN));
        QuestCommand endedQuestTurnCommand = (QuestCommand) client.sendCommand(endQuestTurnCommand);
        if (endedQuestTurnCommand.getPlayer() != null) updatePlayer(endedQuestTurnCommand.getPlayer());
        waitTurn();
    }


    private void waitTurn() {
        disableView(true);
        view.getHud().getCurrentStateText().setText("Wait for your turn");
    }

    private void takeTurn() {
        disableView(false);
    }

    private void acceptQuestCard(Card card) {
        CardView drawnCard = new CardView(card, true, "Accept", "Discard");

        acceptCard(drawnCard, e -> {
            if (myHand.size() == 12) {
                // card cannot be removed from selected weapons
                questController.addWeapon((WeaponCard) card, false);
            } else if (myHand.size() < 12) {
                // we have room in hand
                addCardToHand(myHand, card);
            }

            // tell server card accepted either way so game can continue
            // will probably need to change later
            QuestCommand acceptQuestStageCardCommand = (QuestCommand) defaultServerCommand(new QuestCommand(QuestCommandName.ACCEPT_QUEST_STAGE_CARD));
            acceptQuestStageCardCommand.setCard(card);
            QuestCommand acceptedQuestStageCardCommand = (QuestCommand) client.sendCommand(acceptQuestStageCardCommand);
            if (acceptedQuestStageCardCommand.getPlayer() != null)
                updatePlayer(acceptedQuestStageCardCommand.getPlayer());
            view.getMainPane().remove(drawnCard);
        }, e -> {
            discardCard(drawnCard);

            // Server command
            QuestCommand discardQuestStageCardCommand = (QuestCommand) defaultServerCommand(new QuestCommand(QuestCommandName.DISCARD_QUEST_STAGE_CARD));
            discardQuestStageCardCommand.setCard(card);
            QuestCommand discardedQuestStageCardCommand = (QuestCommand) client.sendCommand(discardQuestStageCardCommand);
            if (discardedQuestStageCardCommand.getPlayer() != null)
                updatePlayer(discardedQuestStageCardCommand.getPlayer());

            view.getMainPane().remove(drawnCard);
        });

    }

    private void questJoin(Card card, Quest quest) {
        CardView drawnCard = new CardView(card, true, "Join", "Decline");

        displayCard(drawnCard, e -> {
            // player chooses join
            // Send will join command to server
            QuestCommand willJoinQuestCommand = (QuestCommand) defaultServerCommand(new QuestCommand(QuestCommandName.WILL_JOIN_QUEST));
            QuestCommand joinedQuestCommand = (QuestCommand) client.sendCommand(willJoinQuestCommand);
            if (joinedQuestCommand.getPlayer() != null) updatePlayer(joinedQuestCommand.getPlayer());

            view.getMainPane().remove(drawnCard);

            questController = new QuestController(quest, gc);

            waitTurn();
        }, e -> {
            // player chooses decline
            // Send will not join command to server
            QuestCommand willNotJoinQuestCommand = (QuestCommand) defaultServerCommand(new QuestCommand(QuestCommandName.WILL_NOT_JOIN_QUEST));
            QuestCommand didNotJoinQuestCommand = (QuestCommand) client.sendCommand(willNotJoinQuestCommand);
            if (didNotJoinQuestCommand.getPlayer() != null) updatePlayer(didNotJoinQuestCommand.getPlayer());

            view.getMainPane().remove(drawnCard);
            waitTurn();
        });
    }

    private void questSponsor(Card card) {
        CardView drawnCard = new CardView(card, true, "Sponsor", "Decline");

        displayCard(drawnCard, e -> {
            if (myHand.filtered(c -> c.getCard() instanceof FoeCard || c.getCard() instanceof TestCard).size() < ((QuestCard) drawnCard.getCard()).getStages()) {
                // notify user they dont have enough cards to sponsor
                AlertBox.alert("Insufficient cards in hand. This Quest requires at least " + ((QuestCard) drawnCard.getCard()).getStages() + " Foe or Test cards to sponsor.", Alert.AlertType.WARNING, e2 -> {
                    // TODO::Let player decline
                    /*drawnCard.getDiscardButton().fire();
                    waitTurn();

                    // send decline to server
                    QuestCommand declineSponsorQuestCommand = (QuestCommand) defaultServerCommand(new QuestCommand(QuestCommandName.WILL_NOT_SPONSOR_QUEST));
                    QuestCommand declinedSponsorQuestCommand = (QuestCommand) client.sendCommand(declineSponsorQuestCommand);
                    if(declinedSponsorQuestCommand.getPlayer() != null) updatePlayer(declinedSponsorQuestCommand.getPlayer());

                     */

                });
            } else {
                view.getMainPane().remove(drawnCard);
                questSetup((QuestCard) drawnCard.getCard());
            }
        }, e -> {
            // send decline to server
            view.getMainPane().remove(drawnCard);
            waitTurn();

            QuestCommand declineSponsorQuestCommand = (QuestCommand) defaultServerCommand(new QuestCommand(QuestCommandName.WILL_NOT_SPONSOR_QUEST));
            QuestCommand declinedSponsorQuestCommand = (QuestCommand) client.sendCommand(declineSponsorQuestCommand);
            if (declinedSponsorQuestCommand.getPlayer() != null) updatePlayer(declinedSponsorQuestCommand.getPlayer());


        });
    }

    public void sponsorDiscardRewardCard(Card card) {
        // TODO :: - send discard reward card message to server
    }

    private void acceptTournamentCard(Card card) {
        CardView drawnCard = new CardView(card, true, "Accept", "Discard");


        view.getMainPane().remove(drawnCard);

        // Server command


        acceptCard(drawnCard, e -> {
            if (myHand.size() == 12) {
                // card cannot be removed from selected weapons
                tournamentController.addWeapon((WeaponCard) card, false);
            } else if (myHand.size() < 12) {
                // we have room in hand
                addCardToHand(myHand, card);
            }

            // tell server card accepted either way so game can continue
            // will probably need to change later
            TournamentCommand acceptTournamentCardCommand = (TournamentCommand) defaultServerCommand(new TournamentCommand(TournamentCommandName.ACCEPT_TOURNAMENT_CARD));
            acceptTournamentCardCommand.setCard(card);
            TournamentCommand acceptedTournamentCardCommand = (TournamentCommand) client.sendCommand(acceptTournamentCardCommand);
            if (acceptedTournamentCardCommand.getPlayer() != null)
                updatePlayer(acceptedTournamentCardCommand.getPlayer());

            view.getMainPane().remove(drawnCard);
        }, e -> {
            discardCard(drawnCard);

            // Server command
            TournamentCommand discardTournamentCardCommand = (TournamentCommand) defaultServerCommand(new TournamentCommand(TournamentCommandName.DISCARD_TOURNAMENT_CARD));
            discardTournamentCardCommand.setCard(card);
            TournamentCommand discardedTournamentCardCommand = (TournamentCommand) client.sendCommand(discardTournamentCardCommand);
            if (discardedTournamentCardCommand.getPlayer() != null)
                updatePlayer(discardedTournamentCardCommand.getPlayer());

            view.getMainPane().remove(drawnCard);
        });
    }

    private void acceptCard(CardView drawnCard, EventHandler<ActionEvent> posButtonEvent, EventHandler<ActionEvent> negButtonEvent) {

        if (myHand.size() == 12) {
            // drawing card
            drawnCard.getPlayButton().setText("Play");

            if (!(drawnCard.getCard() instanceof WeaponCard)) {
                // if it isnt a weapon we cant play it
                drawnCard.getPlayButton().setVisible(false);
                AlertBox.alert("You currently have too many cards so you must forfeit this draw.");
            } else {
                AlertBox.alert("You may choose to play this card this stage or discard it.");
            }

        } else {
            drawnCard.getDiscardButton().setVisible(false);

            AlertBox.alert("You have received a new card.");
        }

        displayCard(drawnCard, posButtonEvent, negButtonEvent);
    }

    private void displayCard(CardView cardView, EventHandler<ActionEvent> posButtonEvent, EventHandler<ActionEvent> negButtonEvent) {

        cardView.setButtonEvents(posButtonEvent, negButtonEvent);

        // add cardview to center of main pane
        // might want to add card stack pane as its own thing later on.....
        view.getMainPane().add(cardView, Pos.CENTER, true);
    }

    private void discardCard(CardView card) {
        // Send discard card command
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
        QuestSetupController qsc = new QuestSetupController(this, questCard, (quest) -> {
            System.out.println("== Quest setup completed");
            // send sponsor to server
            QuestCommand questSetupCompleteCommand = (QuestCommand) defaultServerCommand(new QuestCommand(QuestCommandName.WILL_SPONSOR_QUEST));
            questSetupCompleteCommand.setQuest(quest);
            QuestCommand questSetupCompletedCommand = (QuestCommand) client.sendCommand(questSetupCompleteCommand);
            if (questSetupCompletedCommand.getPlayer() != null) updatePlayer(questSetupCompletedCommand.getPlayer());

            waitTurn();
        });
        view.getMainPane().add(qsc.getView(), Pos.CENTER, false);
    }


    public void setCardViewButtonActions(CardView cardView) {
        cardView.getDiscardButton().setOnAction(e -> {
            // send delete signal to server and await response
            System.out.println("Discarding card");

            // Send discard card command
            discardCard(cardView);

            // Server command
            GameCommand discardCardCommand = defaultServerCommand(new GameCommand(GameCommandName.DISCARD_CARD));
            discardCardCommand.setCard(cardView.getCard());
            GameCommand discardedCardCommand = (GameCommand) client.sendCommand(discardCardCommand);
            if (discardedCardCommand.getPlayer() != null) updatePlayer(discardedCardCommand.getPlayer());
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

    private void updatePlayer(Player p) {
        this.player = p;
        Platform.runLater(() -> {
            view.getHud().getPlayerInfoView().updatePlayer(p);
            myHand.clear();
            // Add player cards to gui cards
            for (Card card : player.getCards()) {
                addCardToHand(myHand, card);
            }
        });
    }

    /**
     * Default boiler plate for server commands
     *
     * @param command the game command to be sent to the server
     * @return an initialized command with all the default values
     */
    public GameCommand defaultServerCommand(GameCommand command) {
        command.setPlayerId(client.getPlayerId());
        command.setClientIndex(client.getClientIndex());
        return command;
    }

    public void handleBaseCommands(BaseCommand command) {
        CommandName commandName = command.getCommandName();
        System.out.println("== Game Controller command update says: " + command);

    }

    public void handleGameCommands(GameCommand command) {
        CommandName commandName = command.getCommandName();
        System.out.println("== Game Controller command update says: " + command);

        if (commandName.equals(GameCommandName.PLAYER_TURN) && command.getPlayerId() == client.getPlayerId()) { // Take turn if it's player's turn
            System.out.println("== It's my turn. Player: " + command.getPlayerId());
            view.getHud().getCurrentStateText().setText("Take your turn!");
            disableView(false);
        } else if (commandName.equals(GameCommandName.GAME_COMPLETE)) { // Complete game
            System.out.println("== The game is now complete");
            ArrayList<Player> winners = command.getPlayers(); // These are the winners
            System.out.println("== Winners: " + winners.size() + " " + winners);

            Platform.runLater(() -> {
                view.getHud().getCurrentStateText().setText("Game Over");

                EndGameView endGameView = new EndGameView();
                view.getChildren().remove(view.getHud());
                view.getMainPane().clear();
                view.getMainPane().add(endGameView);

                ArrayList<String> players = new ArrayList<>();
                winners.forEach(w -> {
                    players.add("Player " + w.getPlayerId());
                });
                endGameView.getResultsView().setItems(players);

                endGameView.getContinueButton().setOnAction(e -> {
                    GameCommand completeGameCommand = (GameCommand) defaultServerCommand(new GameCommand(GameCommandName.COMPLETE_GAME));
                    GameCommand completedGameCommand = (GameCommand) client.sendCommand(completeGameCommand);

                    // send user back to lobby
                    unsubscribeEvents();
                    ClientApplication.window.setScene(new LobbyScene());
                });
            });
        }
    }

    public void handleQuestCommands(QuestCommand command) {
        CommandName commandName = command.getCommandName();
        System.out.println("== Game Controller command update says: " + command);

        if (commandName.equals(QuestCommandName.SHOULD_SPONSOR_QUEST)) { // Prompt player to sponsor quest
            System.out.println("== It's my turn to decide to sponsor the quest");
            Card questCard = command.getCard();
            Platform.runLater(() -> {
                view.getHud().getCurrentStateText().setText("Quest: " + questCard.getTitle() + "\nDo you want to sponsor this quest?");
                System.out.println(questCard.getCardImg());
                questSponsor(questCard);
                disableView(false);
            });
        } else if (commandName.equals(QuestCommandName.SHOULD_JOIN_QUEST)) { // Prompt player to join quest
            System.out.println("== It's my turn to decide to join the quest");
            Card questCard = command.getCard();
            Quest quest = command.getQuest();
            Platform.runLater(() -> {
                view.getHud().getCurrentStateText().setText("Quest: " + questCard.getTitle() + "\nDo you want to join this quest?");
                System.out.println(questCard.getCardImg());
                questJoin(questCard, quest);
                disableView(false);
            });
        } else if (commandName.equals(QuestCommandName.NO_PLAYER_JOINED_QUEST)) { // Handle if no player joins the quest

            ArrayList<Card> questCardsUsed = command.getCards();
            updatePlayer(command.getPlayer()); // Update player
            System.out.println("== No player joined my quest " + questCardsUsed.size());
        } else if (commandName.equals(QuestCommandName.PLAYER_TAKE_STAGE_CARD)) { // Handle accepting/discarding quest stage card
            System.out.println("== It's my turn to accept/discard stage card");
            Card questStageAdventureCard = command.getCard();
            Quest quest = command.getQuest();

            Platform.runLater(() -> {
                questController.updateQuest(quest);
                acceptQuestCard(questStageAdventureCard);
            });

        } else if (commandName.equals(QuestCommandName.PLAYER_QUEST_TURN)) { // Handle taking quest turn
            System.out.println("== It's my turn to take turn for quest stage");
            Card questStageCard = command.getCard();
            Quest quest = command.getQuest();
            view.getHud().getCurrentStateText().setText("Quest Stage: " + (questStageCard instanceof FoeCard ? "Foe" : "Test"));
            System.out.println("== Quest Card: " + questStageCard.getTitle());

            Platform.runLater(() -> {
                // if quest controller is null you are sponsor i guess? not really -> NO NEED: If sponsoring, will never receive this command
                try {
                    while (questController == null) Thread.sleep(1000); // Wait for quest controller to be setup
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                view.getMainPane().clear();
                view.getMainPane().add(questController.getQuestView());
                questController.pickCards(quest, (wl) -> {
                    // once player has picked cards
                    disableView(true);
                    // send cards to server
                    QuestCommand takeQuestTurnCommand = (QuestCommand) defaultServerCommand(new QuestCommand(QuestCommandName.TAKE_QUEST_TURN));
                    takeQuestTurnCommand.setCards(wl);
                    QuestCommand tookQuestTurnCommand = (QuestCommand) client.sendCommand(takeQuestTurnCommand);
                    if (tookQuestTurnCommand.getPlayer() != null) updatePlayer(tookQuestTurnCommand.getPlayer());
                    waitTurn();
                });
                disableView(false);
            });
        } else if (commandName.equals(QuestCommandName.QUEST_STAGE_WON)) { // Handle end quest stage turn when stage won -> continue quest
            System.out.println("== I just won this stage. Continuing...");
            Quest quest = command.getQuest();
            Stage currentStage = quest.getCurrentStage();
            view.getHud().getCurrentStateText().setText("Quest Stage: " + currentStage.getStageCard().getTitle());

            // Should show button to continue
            Platform.runLater(() -> {
                view.getMainPane().clear();
                view.getMainPane().add(questController.getQuestView());
                questController.stageComplete(quest, true, () -> {
                    playerStageContinue();
                });
            });

        } else if (commandName.equals(QuestCommandName.QUEST_STAGE_LOST)) { // Handle end quest stage turn when stage lost -> sit out of quest
            System.out.println("== I just lost this stage. Sitting out...");
            Quest quest = command.getQuest();
            Stage currentStage = quest.getCurrentStage();
            view.getHud().getCurrentStateText().setText("Quest Stage: " + currentStage.getStageCard().getTitle());

            // Should show sit out of quest button
            Platform.runLater(() -> {
                view.getMainPane().clear();
                view.getMainPane().add(questController.getQuestView());
                questController.stageComplete(quest, false, () -> {
                    playerStageContinue();
                });
            });
        } else if (commandName.equals(QuestCommandName.PLAYER_TAKE_SPONSOR_QUEST_CARDS)) { // Accept quest cards for sponsor
            System.out.println("== As sponsor, I accept quest cards");
            Quest quest = command.getQuest();
            ArrayList<Card> cards = command.getCards();
            view.getHud().getCurrentStateText().setText("Quest Complete");

            Platform.runLater(() -> {
                view.getMainPane().clear();
                view.getMainPane().add(questController.getQuestView());
                System.out.println("Received cards: " + cards.size());
                questController.sponsorQuestRewards(quest, cards, (keptCards) -> {
                    QuestCommand acceptSponsorQuestCardsCommand = (QuestCommand) defaultServerCommand(new QuestCommand(QuestCommandName.ACCEPT_SPONSOR_QUEST_CARDS));
                    acceptSponsorQuestCardsCommand.setCards(keptCards);
                    QuestCommand acceptedSponsorQuestCardsCommand = (QuestCommand) client.sendCommand(acceptSponsorQuestCardsCommand);
                    if (acceptedSponsorQuestCardsCommand.getPlayer() != null)
                        updatePlayer(acceptedSponsorQuestCardsCommand.getPlayer());
                    waitTurn();
                });
            });
        } else if (commandName.equals(QuestCommandName.PLAYER_END_QUEST)) { // Complete quest
            System.out.println("== As quest participant, I end the quest");
            view.getHud().getCurrentStateText().setText("Quest Over");
            Quest quest = command.getQuest();

            Platform.runLater(() -> {
                view.getMainPane().clear();
                view.getMainPane().add(questController.getQuestView());
                questController.questComplete(quest, player, () -> {
                    QuestCommand endQuestCommand = (QuestCommand) defaultServerCommand(new QuestCommand(QuestCommandName.END_QUEST));
                    QuestCommand endedQuestCommand = (QuestCommand) client.sendCommand(endQuestCommand);
                    if (endedQuestCommand.getPlayer() != null) updatePlayer(endedQuestCommand.getPlayer());
                    waitTurn();
                });
            });
        }
    }

    public void handleEventCommands(EventCommand command) {
        CommandName commandName = command.getCommandName();
        System.out.println("== Game Controller command update says: " + command);

        if (commandName.equals(EventCommandName.EVENT_STARTED)) {
            System.out.println("== Setting up Event");
            Card eventCard = command.getCard();

            // TODO:: make EventSetupController view and send event over
            setUpEvent((EventCard) eventCard);
        } else if (commandName.equals(EventCommandName.EVENT_EXTRA_INFO)) {
            System.out.println("== Got extra stuff");
        }

    }

    public void handleTournamentCommands(TournamentCommand command) {
        CommandName commandName = command.getCommandName();
        System.out.println("== Game Controller command update says: " + command);

        if (commandName.equals(TournamentCommandName.SHOULD_JOIN_TOURNAMENT)) { // Prompt player to join tournament
            System.out.println("== It's my turn to decide to join the tournament");
            Card tournamentCard = command.getCard();
            Tournament tournament = command.getTournament();

            Platform.runLater(() -> {
                CardView drawnCard = new CardView(tournamentCard, true, "Join", "Decline");

                displayCard(drawnCard, e -> {
                    // player chooses join
                    // Send will join command to server
                    TournamentCommand willJoinTournamentCommand = (TournamentCommand) defaultServerCommand(new TournamentCommand(TournamentCommandName.WILL_JOIN_TOURNAMENT));
                    TournamentCommand joinedTournamentCommand = (TournamentCommand) client.sendCommand(willJoinTournamentCommand);
                    if (joinedTournamentCommand.getPlayer() != null) updatePlayer(joinedTournamentCommand.getPlayer());

                    view.getMainPane().remove(drawnCard);

                    tournamentController = new TournamentController(tournament, gc);

                    waitTurn();
                }, e -> {
                    // player chooses decline
                    // Send will not join command to server
                    TournamentCommand willNotJoinTournamentCommand = (TournamentCommand) defaultServerCommand(new TournamentCommand(TournamentCommandName.WILL_NOT_JOIN_TOURNAMENT));
                    TournamentCommand didNotJoinTournamentCommand = (TournamentCommand) client.sendCommand(willNotJoinTournamentCommand);
                    if (didNotJoinTournamentCommand.getPlayer() != null)
                        updatePlayer(didNotJoinTournamentCommand.getPlayer());

                    view.getMainPane().remove(drawnCard);
                    waitTurn();
                });
            });

        } else if (commandName.equals(TournamentCommandName.NO_PLAYER_JOINED_TOURNAMENT)) { // Handle if no player joins the tournament
            System.out.println("== No player joined tournament ");

            AlertBox.alert("Nobody else joined the tournament so you win by default.");
        } else if (commandName.equals(TournamentCommandName.PLAYER_TAKE_TOURNAMENT_CARD)) { // Handle accepting/discarding tournament adventure card
            System.out.println("== It's my turn to accept/discard tournament adventure card");
            Card tournamentAdventureCard = command.getCard();
            Tournament tournament = command.getTournament();

            Platform.runLater(() -> {
                tournamentController.updateTournament(tournament);
                acceptTournamentCard(tournamentAdventureCard);
            });

        } else if (commandName.equals(TournamentCommandName.PLAYER_TOURNAMENT_TURN)) { // Handle taking tournament turn
            System.out.println("== It's my turn to take turn for tournament");
            Card tournamentAdventureCard = command.getCard();
            Tournament tournament = command.getTournament();

            Platform.runLater(() -> {
                tournamentController.pickCards(tournament, cards -> {
                    // send cards picked to server

                });
            });

        } else if (commandName.equals(TournamentCommandName.TOURNAMENT_WON)) {
            System.out.println("== I just won the tournament.");
            Card tournamentAdventureCard = command.getCard();
            Tournament tournament = command.getTournament();

            // TODO:: GUI Stuff

        } else if (commandName.equals(TournamentCommandName.TOURNAMENT_LOST)) {
            System.out.println("== I just lost the tournament");
            Card tournamentAdventureCard = command.getCard();
            Tournament tournament = command.getTournament();

            // TODO:: GUI Stuff
        } else if (commandName.equals(TournamentCommandName.PLAYER_END_TOURNAMENT)) { // Complete tournament
            System.out.println("== As tournament participant, I end the tournament");
            view.getHud().getCurrentStateText().setText("Tournament Over");
            Card tournamentAdventureCard = command.getCard();
            Tournament tournament = command.getTournament();

            tournamentController.tournamentComplete(tournament, () -> {
                // send continue button clicked to server

                waitTurn();
            });
            ;
        }
    }


    public void setUpEvent(EventCard event) {
        EventCommand eventSetupCompleteCommand = (EventCommand) defaultServerCommand(new EventCommand(EventCommandName.SETUP_COMPLETE));
        eventSetupCompleteCommand.setEvent(new Event(event));
        EventCommand eventSetupCompletedCommand = (EventCommand) client.sendCommand(eventSetupCompleteCommand);
        if (eventSetupCompletedCommand.getPlayer() != null) updatePlayer(eventSetupCompletedCommand.getPlayer());

    }

    /**
     * Unsubscribe from all events
     */
    public void unsubscribeEvents() {
        try {
            for (Callable eventSubscription : eventSubscriptions) {
                eventSubscription.call();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
