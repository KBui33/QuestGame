package gui.partials;

import javafx.collections.FXCollections;
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

    // cards will be card objects once cards are created
    protected ListView<CardView> listView;
    protected ObservableList<CardView> list;
    protected DeckCellFactory cardCellFactory;

    public DeckView() {
        listView = new ListView<>();
        listView.setOrientation(Orientation.HORIZONTAL);
        listView.setMaxHeight(325);
        list = FXCollections.observableArrayList();

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

    public void setListViewItems (ObservableList<CardView> input) {
        this.list = input;
        listView.setItems(this.list);
    }

    public ListView<CardView> getListView() {
        return listView;
    }

    public ObservableList<CardView> getList() {
        return list;
    }

    public void setVisible(Boolean b) {
        listView.setVisible(b);
    }

    public boolean isVisible() {
        return listView.isVisible();
    }


}
