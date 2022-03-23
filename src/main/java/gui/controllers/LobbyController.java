package gui.controllers;

import gui.main.ClientApplication;
import gui.panes.LobbyPane;
import gui.scenes.ConnectScene;
import gui.scenes.GameScene;
import javafx.application.Platform;
import model.Command;
import model.ExternalGameState;
import model.GameCommand;
import networking.client.Client;
import networking.client.ClientEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * @author James DiNovo
 *
 * The controller for manipulating LobbyPane view
 */
public class LobbyController {

    // need model
    private Client client;
    private final ArrayList<Callable> eventSubscriptions = new ArrayList<>();

    public LobbyController(LobbyPane view) {
        setView(view);
    }

    public void setView(LobbyPane view) {

        // get model


        // ensure view is updated whenever model changes

        try {
            client = Client.getInstance();
            // Get current lobby state
            GameCommand initLobbyStateCommand =  client.sendCommand(new GameCommand(Command.GET_LOBBY_STATE));
            if(initLobbyStateCommand.getCommand().equals(Command.RETURN_LOBBY_STATE)) {
                view.getPlayersText().setText("Players Connected: " + initLobbyStateCommand.getJoinedPlayers());
            }

            // Subscribe to game updates
            Callable<Void> unsubscribeCommandReceived = client.clientEvents.subscribe(Client.ClientEvent.GAME_COMMAND_RECEIVED, (eventType, o) -> {
                GameCommand receivedCommand = (GameCommand) o;
                System.out.println("== Lobby Controller says: " + receivedCommand);
                if(receivedCommand.getCommand().equals(Command.PLAYER_JOINED)) { // Update players connected
                    view.getPlayersText().setText("Players Connected: " + receivedCommand.getJoinedPlayers());
                } else if(receivedCommand.getCommand().equals(Command.GAME_STARTED)) { // Load game view
                    unsubscribeEvents(); // Unsubscribe from events
                    Platform.runLater(() ->ClientApplication.window.setScene(new GameScene()));
                }
            });

            eventSubscriptions.add(unsubscribeCommandReceived);

            Callable<Void> unsubscribeGameStateUpdate = client.clientEvents.subscribe(Client.ClientEvent.EXTERNAL_GAME_STATE_UPDATED, (eventType, o) -> {
                ExternalGameState externalGameState = (ExternalGameState) o;
                System.out.println("== Lobby Controller says: " + externalGameState);
            });

            eventSubscriptions.add(unsubscribeGameStateUpdate);

            // link controller to view
            view.getReadyButton().setOnAction(e -> {
                System.out.println("Ready clicked");

                if (view.getReadyButton().getText().equals("Ready")) {
                    // Send a ready command to the server
                    GameCommand readyCommand = new GameCommand(Command.READY);
                    readyCommand.setClientIndex(client.getClientIndex());
                    GameCommand isReadyCommand =  client.sendCommand(readyCommand);
                    client.setPlayerId(isReadyCommand.getPlayer().getPlayerId()); // Set id of player/client

                    view.getReadyButton().getStyleClass().remove("success");
                    view.getReadyButton().getStyleClass().add("caution");
                    view.getReadyButton().setText("Wait");
                } else {
                    // TODO :: ADD UNREADY FUNCTIONALITY
//                    view.getReadyButton().getStyleClass().remove("caution");
//                    view.getReadyButton().getStyleClass().add("success");
//                    view.getReadyButton().setText("Ready");
                }
            });

        } catch(IOException err) {
            err.printStackTrace();
        }

        view.getServerText().setText("Server Address: " + client.getServerHost());

        view.getLeaveButton().setOnAction(e -> {
            System.out.println("Disconnecting");
            ClientApplication.window.setScene(new ConnectScene());
            // should send disconnect notification to server and remove player from game
            // TODO::Unsubscribe from all events
        });
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
