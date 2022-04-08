package gui.partials;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * @author James DiNovo
 *
 * View for displaying card that has been dealt to user
 * and allowing the user to make decisions
 * i.e if deck full discard or play the card
 */
public class CardsReceivedView extends BorderPane {
    private Button acceptButton;
    private DeckView deckView;
    private Text infoText;
    private VBox vBox;

    public CardsReceivedView() {
        vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);

        deckView = new DeckView();

        infoText = new Text();
        infoText.getStyleClass().add("body-font");

        acceptButton = new Button("Accept");
        acceptButton.getStyleClass().add("success");

        vBox.getChildren().addAll(infoText, acceptButton, deckView.getListView());
        this.setCenter(vBox);
    }

    public Button getAcceptButton() {
        return acceptButton;
    }

    public DeckView getDeckView() {
        return deckView;
    }

    public Text getInfoText() {
        return infoText;
    }
}
