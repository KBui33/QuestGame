package gui.scenes;

import gui.main.Construct;
import gui.panes.LobbyPane;
import javafx.scene.Scene;

public class LobbyScene extends Scene {

    public LobbyScene() {
        super(new LobbyPane(), Construct.SCREEN_WIDTH, Construct.SCREEN_HEIGHT);
        this.getStylesheets().add(String.valueOf(LobbyScene.class.getResource("/styles/style.css")));
    }
}
