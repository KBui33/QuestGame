package gui.partials;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;

/**
 * @author James DiNovo
 *
 * CardView for displaying groups of cards to the user
 * i.e the user's hand or discarded cards
 */
public class DeckView {

    protected ListView<CardView> listView;
    protected DeckCellFactory cardCellFactory;

    public DeckView() {
        listView = new ListView<>();
        listView.setOrientation(Orientation.HORIZONTAL);
        listView.setMaxHeight(325);

        // Disable default list item selection behaviour
        listView.setOnMouseClicked(Event::consume);
        listView.setOnMousePressed(e -> {
            listView.getSelectionModel().clearSelection();
            e.consume();
        });

        cardCellFactory = new DeckCellFactory();
        listView.setCellFactory(cardCellFactory);

    }

    public DeckView(ObservableList<CardView> input) {
        this();
        setListViewItems(input);
    }

    public void setSize(int height) {
        listView.setMaxHeight(height);
    }

    public void setListViewItems (ObservableList<CardView> input) {
        listView.setItems(input);
    }

    public ListView<CardView> getListView() {
        return listView;
    }

    public void setVisible(Boolean b) {
        listView.setVisible(b);
    }

    public boolean isVisible() {
        return listView.isVisible();
    }


}
