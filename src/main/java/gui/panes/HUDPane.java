package gui.panes;

import gui.partials.DeckView;
import gui.partials.ShieldsView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import static gui.main.Construct.SCREEN_WIDTH;

/**
 * @author James DiNovo
 *
 * GamePane contains the main game gui
 *
 */
public class HUDPane extends BorderPane {

    private BorderPane topBar;
    private VBox deckButtons, cardButtons;
    private DeckView myHand, discardedCards;
    private HBox rankInfoBox;
    private ShieldsView shieldsView;
    private Text currentStateText;
    private Button showHandButton, showDiscardedButton, drawCardButton, endTurnButton;

    public DeckView getMyHand() {
        return myHand;
    }

    public DeckView getDiscardedCards() {
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

    public Button getDrawCardButton() {
        return drawCardButton;
    }

    public Button getEndTurnButton() {
        return endTurnButton;
    }

    public BorderPane getTopBar() {
        return topBar;
    }

    public VBox getDeckButtons() {
        return deckButtons;
    }

    public VBox getCardButtons() {
        return cardButtons;
    }

    public HBox getRankInfoBox() {
        return rankInfoBox;
    }

    public HUDPane() {

        topBar = new BorderPane();
        topBar.setMaxSize(SCREEN_WIDTH, 60);
        setMargin(topBar, new Insets(5));

        deckButtons = new VBox();
        deckButtons.setSpacing(5);
        deckButtons.setMaxSize(150, 60);
        setMargin(deckButtons, new Insets(5));

        cardButtons = new VBox();
        cardButtons.setSpacing(5);
        cardButtons.setMaxSize(150, 60);
        setMargin(cardButtons, new Insets(5));

        myHand = new DeckView();
        discardedCards = new DeckView();

        // put current rank card next to shields
        rankInfoBox = new HBox();
        shieldsView = new ShieldsView();

        currentStateText = new Text();
        currentStateText.getStyleClass().add("body-font");
        currentStateText.setTextAlignment(TextAlignment.CENTER);
        currentStateText.setFill(Paint.valueOf("#ff0000"));

        showHandButton = new Button("Hand");

        showDiscardedButton = new Button("Discarded");

        drawCardButton = new Button("Draw Card");
        drawCardButton.getStyleClass().add("success");
        drawCardButton.setVisible(false);

        endTurnButton = new Button("End Turn");
        endTurnButton.getStyleClass().add("warn");

        deckButtons.getChildren().addAll(showHandButton, showDiscardedButton);
        cardButtons.getChildren().addAll(drawCardButton, endTurnButton);
        topBar.setLeft(shieldsView);
        topBar.setRight(currentStateText);
        setAlignment(deckButtons, Pos.BOTTOM_RIGHT);
        this.setRight(deckButtons);
        setAlignment(cardButtons, Pos.BOTTOM_LEFT);
        this.setLeft(cardButtons);
        setAlignment(shieldsView, Pos.TOP_LEFT);
        setAlignment(currentStateText, Pos.TOP_RIGHT);
        this.setTop(topBar);
    }
}
