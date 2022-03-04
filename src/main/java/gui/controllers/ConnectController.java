package gui.controllers;

import gui.main.ClientApplication;
import gui.panes.ConnectPane;
import gui.scenes.LobbyScene;
import model.ExternalGameState;
import networking.client.Client;
import networking.client.ClientEventListener;

import java.io.IOException;

/**
 * @author James DiNovo
 *
 * The controller for manipulating ConnectPane view
 */
public class ConnectController {
    public ConnectController (ConnectPane view) {
        setView(view);
    }

    public void setView(ConnectPane view) {
        view.getConnectButton().setOnAction(e -> {
            String serverHost = view.getServerAddress().getText().trim();
            System.out.println("Connecting to " + serverHost + "...");
            // Create new client instance to connect to server
            try {
                Client client = Client.initialize(serverHost);
                client.clientEvents.subscribe(Client.ClientEvent.EXTERNAL_GAME_STATE_UPDATED, new ClientEventListener() {
                    @Override
                    public void update(Client.ClientEvent eventType, Object o) {
                        ExternalGameState externalGameState = (ExternalGameState) o;
                        System.out.println("== Connect Controller says: " + externalGameState);
                    }
                });
            } catch(IOException err) {
                err.printStackTrace();
            }

            ClientApplication.window.setScene(new LobbyScene());
        });
    }
}
