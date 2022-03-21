package gui.partials.quest;

import gui.partials.DeckView;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class QuestSponsorCardsView extends BorderPane {
    private Button acceptButton;
    private DeckView deckView;
    private Text infoText;
    private VBox vBox;

    public QuestSponsorCardsView() {
        vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);

        deckView = new DeckView();

        infoText = new Text("Cards earned for sponsoring the quest.");
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
