package gui.panes;

import gui.partials.CardView;
import gui.partials.ShieldsView;
import gui.partials.UserCardView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import static gui.main.Const.BODY_FONT;
import static gui.main.Construct.SCREEN_WIDTH;

/**
 * @author James DiNovo
 *
 * GamePane contains the main game gui
 *
 */
public class GamePane extends BorderPane {

    private BorderPane topBar;
    private VBox cardButtons;
    private CardView myHand, discardedCards;
    private HBox rankInfoBox;
    private ShieldsView shieldsView;
    private Text currentStateText;
    private Button showHandButton, showDiscardedButton;

    public CardView getMyHand() {
        return myHand;
    }

    public CardView getDiscardedCards() {
        return discardedCards;
    }

    public ShieldsView getShieldsView() {
        return shieldsView;
    }

    public Text getCurrentStateText() {
        return currentStateText;
    }

    public Button getShowHandButton() {
        return showHandButton;
    }

    public Button getShowDiscardedButton() {
        return showDiscardedButton;
    }

    public GamePane() {

        topBar = new BorderPane();
        topBar.setMaxSize(SCREEN_WIDTH, 60);
        setMargin(topBar, new Insets(5));

        cardButtons = new VBox();
        cardButtons.setSpacing(5);
        cardButtons.setMaxSize(150, 60);
        setMargin(cardButtons, new Insets(5));

        myHand = new UserCardView();
        discardedCards = new CardView();

        // put current rank card next to shields
        rankInfoBox = new HBox();
        shieldsView = new ShieldsView();


        currentStateText = new Text();
        currentStateText.setFont(BODY_FONT);
        currentStateText.setTextAlignment(TextAlignment.CENTER);

        showHandButton = new Button("Hand");
        showHandButton.setPrefSize(150, 25);
        showHandButton.getStyleClass().add("caution");

        showDiscardedButton = new Button("Discarded");
        showDiscardedButton.setPrefSize(150, 25);
        showDiscardedButton.getStyleClass().add("caution");

        cardButtons.getChildren().addAll(showHandButton, showDiscardedButton);
        topBar.setLeft(shieldsView);
        topBar.setRight(currentStateText);
        setAlignment(cardButtons, Pos.BOTTOM_RIGHT);
        this.setRight(cardButtons);
        setAlignment(shieldsView, Pos.TOP_LEFT);
        setAlignment(currentStateText, Pos.TOP_RIGHT);
        this.setTop(topBar);
        this.setBottom(myHand.node());
    }
}
