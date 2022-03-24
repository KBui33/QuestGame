package gui.partials;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class ResultsView extends VBox {

    public ResultsView() {
        setMargin(this, new Insets(10));
        this.setAlignment(Pos.CENTER);
        this.setSpacing(5);
    }

    public ResultsView(ArrayList<String> column1, ArrayList<String> column2) {
        this();
        generate(column1, column2);
    }

    public void setItems(ArrayList<String> column1, ArrayList<String> column2) {

    }

    private void generate(ArrayList<String> column1, ArrayList<String> column2) {
        this.getChildren().clear();
        for (int i = 0; i < Math.max(column1.size(), column2.size()); i++) {
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER);
            row.setSpacing(5);

            Text text1 = new Text("     ");
            text1.getStyleClass().add("body-font");
            Text text2 = new Text("     ");
            text2.getStyleClass().add("body-font");

            if (i < column1.size()) {
                text1.setText(column1.get(i));
            }
            if (i < column2.size()) {
                text2.setText(column2.get(i));
            }
            this.getChildren().add(row);
        }


    }
}
