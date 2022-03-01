package gui.partials;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.util.Callback;

import java.util.ArrayList;

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

    public DeckView() {
        listView = new ListView<>();
        listView.setOrientation(Orientation.HORIZONTAL);
        listView.setMaxHeight(325);
        list = FXCollections.observableArrayList();
        listView.setItems(list);

        // Disable default list item selection behaviour
        listView.setOnMouseClicked(Event::consume);
        listView.setOnMousePressed(e -> {
            listView.getSelectionModel().clearSelection();
            e.consume();
        });

        Callback<ListView<CardView>, ListCell<CardView>> cardCellFactory = new DeckCellFactory();
        listView.setCellFactory(cardCellFactory);

    }

    public DeckView(ArrayList<Image> input) {
        this();

        for (Image c : input) {
            this.addCard(c);
        }
    }

    public void addCard(String url) {
        list.add(new CardView(new Image(String.valueOf(getClass().getResource(url)))));
    }

    public void addCard(Image img) {
        list.add(new CardView(img));
    }

    public boolean removeCard(CardView card) {
        return list.remove(card);
    }

    public boolean removeCard(int pos) {
        try {
            return list.remove(pos) != null;
        } catch (IndexOutOfBoundsException | UnsupportedOperationException e) {
            return false;
        }
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
