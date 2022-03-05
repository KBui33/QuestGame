package gui.partials;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.Stack;

/**
 * @author James DiNovo
 *
 * Self-contained shields display for indicating number of shields
 */
public class ShieldsView extends GridPane {
    private int shieldCount;
    private final Stack<ImageView> shields;

    public ShieldsView() {
        shieldCount = 0;
        shields = new Stack<>();
        this.setMaxSize(5, 2);
    }

    public void setShields(int count) {
        this.getChildren().clear();
        shields.clear();
        shieldCount = count > 10 ? 9 : count - 1;
        for (int i = 0; i <= shieldCount; i++) {
            add(generateShield(), i % 5, i / 5);
        }
    }

    public void addShield() {
        if (shieldCount >= 9) {
            return;
        }
        shieldCount++;
        add(generateShield(), shieldCount % 5, shieldCount / 5);
    }

    public void removeShield() {
        if (shieldCount < 0) {
            return;
        }
        this.getChildren().remove(shields.pop());
        shieldCount--;
    }

    private ImageView generateShield() {
        // will need to be changed to generate shield based on player (or not)
        ImageView sh = new ImageView(new Image(String.valueOf(getClass().getResource("/shields/quest_shield_1.png"))));
        sh.setFitWidth(25);
        sh.setPreserveRatio(true);
        setMargin(sh, new Insets(2.5));
        shields.add(sh);
        return sh;
    }
}
