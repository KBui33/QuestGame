package gui.partials.tournament;

import gui.partials.CardView;
import gui.partials.DeckView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class PlayerCards extends BorderPane {

    private CardView playerRank;
    private DeckView cardsView;
    private Text playerText;

    private ObservableList<CardView> cardsList;

    public PlayerCards() {

        playerText = new Text();
        playerText.getStyleClass().add("stats-font");

        playerRank = new CardView();
        playerRank.setSize(150);
        playerRank.getButtonBox().setVisible(false);
        setAlignment(playerRank, Pos.CENTER);

        cardsView = new DeckView();
        cardsView.setHeight(150);
        cardsView.setWidth(400);
        this.cardsList = FXCollections.observableArrayList();
        cardsView.setListViewItems(cardsList);

        this.setTop(playerText);
        this.setLeft(playerRank);
        this.setCenter(cardsView.getListView());
    }

    // will need to take in tournament player and set cards accordingly
    public void setPlayerCards() {

    }
}
