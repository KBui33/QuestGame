package gui.panes;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

/**
 * @author James DiNovo
 *
 * Layers HUD over top of main display
 */
public class GamePane extends StackPane {

    private final HUDPane hud;
    private final GameDisplayPane main;

    public GamePane() {
        hud = new HUDPane();
        // allow clicking through transparent sections of pane
        hud.setPickOnBounds(false);
        setAlignment(hud, Pos.CENTER);
        main = new GameDisplayPane();
        setAlignment(main, Pos.CENTER);

        this.getChildren().addAll(main, hud);
    }

    public HUDPane getHud() {
        return hud;
    }

    public GameDisplayPane getMainPane() {
        return main;
    }
}
