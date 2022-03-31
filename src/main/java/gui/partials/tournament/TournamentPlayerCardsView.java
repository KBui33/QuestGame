package gui.partials.tournament;

import gui.partials.CardView;
import gui.partials.DeckView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.TournamentPlayer;

public class TournamentPlayerCardsView extends VBox {

    private CardView playerRank;
    private DeckView cardsView;
    private Text playerText;

    private ObservableList<CardView> cardsList;

    private HBox cardsBox;

    public TournamentPlayerCardsView() {

        cardsBox = new HBox();
        cardsBox.setSpacing(5);
        cardsBox.setAlignment(Pos.CENTER);

        playerText = new Text();
        playerText.getStyleClass().add("stats-font");

        playerRank = new CardView();
        playerRank.setSize(125);
        playerRank.getButtonBox().setVisible(false);

        cardsView = new DeckView();
        cardsView.setHeight(125);
        cardsView.setWidth(400);
        this.cardsList = FXCollections.observableArrayList();
        cardsView.setListViewItems(cardsList);

        cardsBox.getChildren().addAll(playerRank, cardsView.getListView());

        this.getChildren().addAll(playerText, cardsBox);
        this.setAlignment(Pos.CENTER);
    }

    public TournamentPlayerCardsView(TournamentPlayer player) {
        this();
        setPlayerCards(player);
    }

    // take in tournament player and set cards accordingly
    public void setPlayerCards(TournamentPlayer player) {
//        this.playerText.setText("Player " + player.getPlayerId() + ": " + player.calculateBattlePoints() + " Battle Points");
        this.playerText.setText("Player " + player.getPlayerId());
        this.playerRank.setCard(player.getRankCard());
        player.getCardsUsed().forEach(c -> {
            CardView cv = new CardView(c);
            cv.setSize(100);
            cv.getButtonBox().setVisible(false);
            cardsList.add(cv);
        });
    }
}
