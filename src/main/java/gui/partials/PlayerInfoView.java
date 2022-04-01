package gui.partials;

import component.card.Rank;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Player;

public class PlayerInfoView extends VBox {

    private ShieldsView shieldsView;
    private CardView rankCard;
    private Text name;

    private HBox levelBox;

    public PlayerInfoView() {
        levelBox = new HBox();
        levelBox.setSpacing(2.5);
        levelBox.setAlignment(Pos.TOP_LEFT);

        shieldsView = new ShieldsView();

        rankCard = new CardView();
        rankCard.setSize(100);
        rankCard.setAlignment(Pos.TOP_LEFT);

        levelBox.getChildren().addAll(rankCard, shieldsView);

        name = new Text();
        name.getStyleClass().add("stats-font");

        this.getChildren().addAll(name, levelBox);
        setAlignment(Pos.TOP_LEFT);
    }

    public void updatePlayer(Player p) {
        if (p.getRankCard().getRank() == Rank.ROUND_TABLE_KNIGHT) {
            shieldsView.setVisible(false);
            rankCard.setVisible(false);
            name.setText("Player " + p.getPlayerId() + " - Knight of the Round Table");
        } else {
            shieldsView.setVisible(true);
            rankCard.setVisible(true);
            name.setText("Player " + p.getPlayerId());
            shieldsView.setShields(p.getShields());
            rankCard.setCard(p.getRankCard());
        }
    }
}
