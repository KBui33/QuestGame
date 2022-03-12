package gui.panes;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class GameDisplayPane extends StackPane {

    public void add(Node node, Pos pos, boolean front) {
        setAlignment(node, pos);

        if (front) {
            this.getChildren().add(node);
        } else {
            this.getChildren().add(0, node);
        }
    }

    public void remove(Node node) {
        this.getChildren().remove(node);
    }

    public GameDisplayPane() {

    }
}
