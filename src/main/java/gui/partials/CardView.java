package gui.partials;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class CardView {

    private ListView<ImageView> listView;
    private ObservableList<ImageView> list;

    public CardView() {
        listView = new ListView<>();
        listView.setOrientation(Orientation.HORIZONTAL);
        listView.setMaxHeight(325);
        list = FXCollections.observableArrayList();
        listView.setItems(list);
    }
    public CardView(ArrayList<Image> input) {
        listView = new ListView<>();
        listView.setOrientation(Orientation.HORIZONTAL);
        listView.setMaxHeight(325);
        list = FXCollections.observableArrayList();
        listView.setItems(list);

        for (Image c : input) {
            this.addCard(c);
        }
    }

    public void addCard(String url) {
        ImageView tmp = new ImageView(new Image(String.valueOf(getClass().getResource(url))));
        tmp.setFitHeight(300);
        tmp.setPreserveRatio(true);
        list.add(tmp);
    }

    public void addCard(Image img) {
        ImageView tmp = new ImageView(img);
        tmp.setFitHeight(300);
        tmp.setPreserveRatio(true);
        list.add(tmp);
    }

    public ListView<ImageView> node() {
        return listView;
    }

    public void setVisible(Boolean b) {
        listView.setVisible(b);
    }

    public boolean isVisible() {
        return listView.isVisible();
    }


}
