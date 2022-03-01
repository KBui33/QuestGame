package gui.controllers;

import gui.main.ClientApplication;
import gui.panes.ConnectPane;
import gui.scenes.LobbyScene;
import networking.Client;

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
                Client.getInstance(serverHost);
            } catch(IOException err) {
                err.printStackTrace();
            }

            ClientApplication.window.setScene(new LobbyScene());
        });
    }
}
