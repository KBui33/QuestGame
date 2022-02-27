package gui.controllers;

import gui.main.ClientApplication;
import gui.panes.ConnectPane;
import gui.scenes.LobbyScene;

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
            System.out.println("Connecting to " + view.getServerAddress().getText().trim() + "...");
            ClientApplication.window.setScene(new LobbyScene());
        });
    }
}
