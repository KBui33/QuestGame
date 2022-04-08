package gui.scenes;

import gui.controllers.LobbyController;
import gui.main.Construct;
import gui.panes.LobbyPane;
import javafx.scene.Scene;

/**
 * @author James DiNovo
 *
 * Lobby scene for displaying lobby view and initializing controller
 */
public class LobbyScene extends Scene {

    public LobbyScene() {
        super(new LobbyPane(), Construct.SCREEN_WIDTH, Construct.SCREEN_HEIGHT);
        this.getStylesheets().add(String.valueOf(LobbyScene.class.getResource("/styles/style.css")));
        new LobbyController((LobbyPane) this.getRoot());
    }
}
