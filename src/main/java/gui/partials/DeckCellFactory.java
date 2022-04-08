package gui.partials;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * @author James DiNovo
 *
 * Custom listview cell factory for implementing DeckView
 */
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
                    setGraphic(card);

                }
            }
        };
    }


}
