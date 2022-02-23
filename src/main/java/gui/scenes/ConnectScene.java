package gui.scenes;

import gui.main.Construct;
import javafx.scene.Scene;
import gui.panes.ConnectPane;

/**
 * @author James DiNovo
 *
 * ConnectScene holds ConnectPane
 */
public class ConnectScene extends Scene {
    public ConnectScene() {
        super(new ConnectPane(), Construct.SCREEN_WIDTH, Construct.SCREEN_HEIGHT);
        this.getStylesheets().add(String.valueOf(ConnectScene.class.getResource("/styles/style.css")));
    }
}
