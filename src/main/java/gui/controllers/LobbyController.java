package gui.controllers;

import gui.main.ClientApplication;
import gui.panes.LobbyPane;
import gui.scenes.ConnectScene;
import gui.scenes.GameScene;
import model.ExternalGameState;
import model.GameCommand;
import networking.Client;
import networking.ClientEventListener;

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


        // link controller to view
        view.getReadyButton().setOnAction(e -> {
            System.out.println("Ready clicked");

            // Send a ready command to the server
            GameCommand command = new GameCommand(GameCommand.Command.READY);
            try {
                Client client = Client.getInstance();
                client.clientEvents.subscribe(Client.ClientEvent.EXTERNAL_GAME_STATE_UPDATED, new ClientEventListener() {
                    @Override
                    public void update(Client.ClientEvent eventType, Object o) {
                        ExternalGameState externalGameState = (ExternalGameState) o;
                        System.out.println("== Connect Controller says: " + externalGameState);
                    }
                });
                client.sendCommand(command);
            } catch(IOException err) {
                err.printStackTrace();
            }


            // skip to game scene *this is only for testing gui* to be removed later
            ClientApplication.window.setScene(new GameScene());

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

        view.getLeaveButton().setOnAction(e -> {
            System.out.println("Disconnecting");
            ClientApplication.window.setScene(new ConnectScene());
        });
    }
}
