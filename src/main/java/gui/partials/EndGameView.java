package gui.partials;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class EndGameView extends BorderPane {
    private Text headerText, infoText;
    private VBox textBox;
    private Button continueButton;
    private ResultsView resultsView;

    public EndGameView() {

        textBox = new VBox();
        textBox.setSpacing(5);
        textBox.setAlignment(Pos.CENTER);
        setMargin(textBox, new Insets(10));

        headerText = new Text("Game Complete");
        headerText.getStyleClass().add("header-font");

        infoText = new Text("The winner(s) are: ");
        infoText.getStyleClass().add("body-font");

        textBox.getChildren().addAll(headerText, infoText);

        resultsView = new ResultsView();

        continueButton = new Button("Continue");
        setAlignment(continueButton, Pos.CENTER);
        continueButton.getStyleClass().add("success");

        this.setTop(textBox);
        this.setCenter(resultsView);
        this.setBottom(continueButton);
    }

    public Text getInfoText() {
        return infoText;
    }

    public Text getHeaderText() {
        return headerText;
    }

    public Button getContinueButton() {
        return continueButton;
    }

    public ResultsView getResultsView() {
        return resultsView;
    }
}
