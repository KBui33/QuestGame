package gui.controllers;

import gui.main.ClientApplication;
import gui.panes.LobbyPane;
import gui.scenes.ConnectScene;
import gui.scenes.GameScene;
import javafx.application.Platform;
import model.ExternalGameState;
import model.GameCommand;
import networking.client.Client;
import networking.client.ClientEventListener;

import java.io.IOException;

/**
 * @author James DiNovo
 *
 * The controller for manipulating LobbyPane view
 */
public class LobbyController {

    // need model

    public LobbyController(LobbyPane view) {
        setView(view);
    }

    public void setView(LobbyPane view) {

        // get model


        // ensure view is updated whenever model changes

        try {
            Client client = Client.getInstance();
            // Get current lobby state
            GameCommand initLobbyStateCommand =  client.sendCommand(new GameCommand(GameCommand.Command.GET_LOBBY_STATE));
            if(initLobbyStateCommand.getCommand().equals(GameCommand.Command.RETURN_LOBBY_STATE)) {
                view.getPlayersText().setText("Players Connected: " + initLobbyStateCommand.getJoinedPlayers());
            }
            // Subscribe to game updates
            client.clientEvents.subscribe(Client.ClientEvent.GAME_COMMAND_RECEIVED, new ClientEventListener() {
                @Override
                public void update(Client.ClientEvent eventType, Object o) {
                    GameCommand receivedCommand = (GameCommand) o;
                    System.out.println("== Lobby Controller says: " + receivedCommand);
                    if(receivedCommand.getCommand().equals(GameCommand.Command.JOINED)) { // Update players connected
                        view.getPlayersText().setText("Players Connected: " + receivedCommand.getJoinedPlayers());
                    } else if(receivedCommand.getCommand().equals(GameCommand.Command.GAME_STARTED)) { // Load game view
                        // skip to game scene *this is only for testing gui* to be removed later
                        Platform.runLater(() ->ClientApplication.window.setScene(new GameScene()));
                    }
                }
            });

            // link controller to view
            view.getReadyButton().setOnAction(e -> {
                System.out.println("Ready clicked");

                // Send a ready command to the server
                GameCommand command = new GameCommand(GameCommand.Command.READY);
                client.clientEvents.subscribe(Client.ClientEvent.EXTERNAL_GAME_STATE_UPDATED, new ClientEventListener() {
                    @Override
                    public void update(Client.ClientEvent eventType, Object o) {
                        ExternalGameState externalGameState = (ExternalGameState) o;
                        System.out.println("== Lobby Controller says: " + externalGameState);
                    }
                });
                command =  client.sendCommand(command);
                client.setPlayerId(command.getPlayerId()); // Set id of player/client

                if (view.getReadyButton().getText().equals("Ready")) {
                    view.getReadyButton().getStyleClass().remove("success");
                    view.getReadyButton().getStyleClass().add("caution");
                    view.getReadyButton().setText("Wait");
                } else {
                    view.getReadyButton().getStyleClass().remove("caution");
                    view.getReadyButton().getStyleClass().add("success");
                    view.getReadyButton().setText("Ready");
                }
            });

        } catch(IOException err) {
            err.printStackTrace();
        }

        view.getLeaveButton().setOnAction(e -> {
            System.out.println("Disconnecting");
            ClientApplication.window.setScene(new ConnectScene());
        });
    }
}
