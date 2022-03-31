package gui.partials.tournament;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class TournamentResultsView extends BorderPane {

    private Text resultsText;
    private VBox playersBox;
    private Button continueButton;

    public TournamentResultsView() {
        // will display multiple PlayerCards (depending on players in tournament)

        this.resultsText = new Text();
        this.resultsText.getStyleClass().add("body-font");
        setAlignment(resultsText, Pos.CENTER);

        this.playersBox = new VBox();
        this.playersBox.setSpacing(5);
        this.playersBox.setAlignment(Pos.CENTER);
        setAlignment(playersBox, Pos.CENTER);

        this.continueButton = new Button("Continue");
        this.continueButton.getStyleClass().add("success");
        setAlignment(continueButton, Pos.CENTER);

        this.setTop(resultsText);
        this.setCenter(playersBox);
        this.setBottom(continueButton);

    }

    public Text getResultsText() {
        return resultsText;
    }

    public VBox getPlayersBox() {
        return playersBox;
    }

    public Button getContinueButton() {
        return continueButton;
    }
}
