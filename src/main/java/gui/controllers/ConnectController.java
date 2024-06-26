package gui.controllers;

import gui.main.ClientApplication;
import gui.other.AlertBox;
import gui.panes.ConnectPane;
import gui.scenes.LobbyScene;
import javafx.scene.control.Alert;
import networking.client.Client;

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
            } catch(IOException | ClassNotFoundException err) {
                // display error to user later on
                err.printStackTrace();
                AlertBox.alert("Could not connect to server.\n" + err.getMessage(), Alert.AlertType.ERROR);
            }


        });
    }
}
