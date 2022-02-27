package gui.scenes;

import gui.main.Construct;
import gui.panes.GamePane;
import javafx.scene.Scene;

public class GameScene extends Scene {
    public GameScene() {
        super(new GamePane(), Construct.SCREEN_WIDTH, Construct.SCREEN_HEIGHT);
        this.getStylesheets().add(String.valueOf(LobbyScene.class.getResource("/styles/style.css")));
    }
}
