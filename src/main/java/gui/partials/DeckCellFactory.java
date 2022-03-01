package gui.partials;

import gui.partials.CardView;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class DeckCellFactory implements Callback<ListView<CardView>, ListCell<CardView>> {
    @Override
    public ListCell<CardView> call(ListView<CardView> param) {
        return new ListCell<CardView>() {
            @Override
            public void updateItem(CardView card, boolean empty) {
                super.updateItem(card, empty);
                if (empty || card == null) {
                    setItem(null);
                    setGraphic(null);
                } else {
                    setItem(card);
                    // will be card.imageview later on
                    setGraphic(card);

                }
            }
        };
    }


}
