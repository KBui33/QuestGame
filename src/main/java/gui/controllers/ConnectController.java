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
            try {
                // Create new client instance to connect to server
                Client client = Client.initialize(serverHost);
                System.out.println("== Connect Controller says: Loading lobby scene...");
                ClientApplication.window.setScene(new LobbyScene());
            } catch(IOException err) {
                err.printStackTrace();
            }


        });
    }
}
