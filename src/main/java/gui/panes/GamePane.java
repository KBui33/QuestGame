package gui.panes;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

public class GamePane extends StackPane {

    private HUDPane hud;
    private GameDisplayPane main;

    public GamePane() {
        hud = new HUDPane();
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
