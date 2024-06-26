package gui.partials;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * @author James DiNovo
 *
 * Display results to the user in the form of a list
 */
public class ResultsView extends VBox {

    public ResultsView() {
        setMargin(this, new Insets(10));
        this.setAlignment(Pos.CENTER);
        this.setSpacing(5);
    }

    public ResultsView(ArrayList<String> column1) {
        this();
        generate(column1);
    }

    public void setItems(ArrayList<String> column1) {
        generate(column1);
    }

    private void generate(ArrayList<String> column1) {
        this.getChildren().clear();
        for (int i = 0; i < column1.size(); i++) {

            Text text1 = new Text("     ");
            text1.getStyleClass().add("body-font");

            if (i < column1.size()) {
                text1.setText(column1.get(i));
            }
            this.getChildren().add(text1);
        }


    }
}
