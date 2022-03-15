package gui.panes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * @author James DiNovo
 *
 * Main game display
 */
public class GameDisplayPane extends StackPane {

    public void add(Node node) {
        add(node, Pos.CENTER, true);
    }

    public void add(Node node, Pos pos) {
        add(node, pos, true);
    }

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

    public void clear() {
        this.getChildren().clear();
    }

    public GameDisplayPane() {
        this.setPadding(new Insets(20));
    }
}
