package gui.partials;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * @author James DiNovo
 *
 * Display cards available for users to play during the given game state
 */
public class CardSelectionView extends BorderPane {
    private DeckView weaponsView;
    private Button doneButton;
    private Text infoText;
    private VBox cardSelectionBox;


    public DeckView getWeaponsView() {
        return weaponsView;
    }

    public Button getDoneButton() {
        return doneButton;
    }

    public CardSelectionView() {
        cardSelectionBox = new VBox();
        cardSelectionBox.setSpacing(5);
        cardSelectionBox.setAlignment(Pos.CENTER);

        doneButton = new Button("Done");
        doneButton.getStyleClass().add("success");

        infoText = new Text("Select weapon cards to prepare for battle");
        infoText.getStyleClass().add("body-font");

        weaponsView = new DeckView();
        weaponsView.setHeight(225);

        cardSelectionBox.getChildren().addAll(infoText, doneButton, weaponsView.getListView());
        this.setCenter(cardSelectionBox);
    }
}
