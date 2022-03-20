package gui.partials;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class endGameView extends BorderPane {
    private Text headerText, infoText;
    private HBox textBox;
    private Button continueButton;
    private ResultsView resultsView;

    public endGameView() {

        textBox = new HBox();
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
        continueButton.getStyleClass().add("success");

        this.setTop(textBox);
        this.setCenter(resultsView);
        this.setBottom(continueButton);
    }

}
