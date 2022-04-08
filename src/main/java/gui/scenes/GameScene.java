package gui.scenes;

import gui.controllers.GameController;
import gui.main.Construct;
import gui.panes.GamePane;
import gui.panes.HUDPane;
import javafx.scene.Scene;

/**
 * @author James DiNovo
 *
 * GameScene hosts game views and launches controller
 */
public class GameScene extends Scene {
    public GameScene() {
        super(new GamePane(), Construct.SCREEN_WIDTH, Construct.SCREEN_HEIGHT);
        this.getStylesheets().add(String.valueOf(LobbyScene.class.getResource("/styles/style.css")));
        new GameController((GamePane) this.getRoot());
    }
}
