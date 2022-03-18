package gui.partials.quest;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class QuestCompleteView extends BorderPane {

    private Text headerText, infoText;
    private ListView<String> players, outcomes;
    private HBox outcomeBox;
    private VBox infoBox;
    private Button continueButton;

    public static final String INFO_STRING = "Earned shields: ";

    public QuestCompleteView() {

        headerText = new Text("Complete");
        headerText.getStyleClass().add("body-font");
        infoText = new Text();
        infoText.getStyleClass().add("stats-font");

        players = new ListView<>();
        players.setEditable(false);
        players.setMaxHeight(200);
        outcomes = new ListView<>();
        outcomes.setEditable(false);
        outcomes.setMaxHeight(200);

        outcomeBox = new HBox();
        outcomeBox.setAlignment(Pos.CENTER);
        outcomeBox.getChildren().addAll(players, outcomes);

        infoBox = new VBox();
        infoBox.setSpacing(5);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.getChildren().addAll(headerText, infoText);

        continueButton = new Button("Continue");
        continueButton.getStyleClass().add("success");
        setAlignment(continueButton, Pos.CENTER);

        this.setTop(infoBox);
        this.setCenter(outcomeBox);
        this.setBottom(continueButton);

    }

    public Text getHeaderText() {
        return headerText;
    }

    public Text getInfoText() {
        return infoText;
    }

    public ListView<String> getPlayers() {
        return players;
    }

    public ListView<String> getOutcomes() {
        return outcomes;
    }

    public HBox getOutcomeBox() {
        return outcomeBox;
    }

    public VBox getInfoBox() {
        return infoBox;
    }

    public Button getContinueButton() {
        return continueButton;
    }
}
