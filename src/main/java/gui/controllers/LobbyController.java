package gui.controllers;

import gui.main.ClientApplication;
import gui.panes.LobbyPane;
import gui.scenes.ConnectScene;
import gui.scenes.GameScene;

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
