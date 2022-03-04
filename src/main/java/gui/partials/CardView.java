package gui.partials;

import game.components.card.Card;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CardView extends StackPane {
    private ImageView imageView;
    private VBox buttonBox;
    private Button discardButton, playButton;
    private Card card;

    public VBox getButtonBox() {
        return buttonBox;
    }

    public Button getDiscardButton() {
        return discardButton;
    }

    public Button getPlayButton() {
        return playButton;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
        this.imageView.setImage(new Image(String.valueOf(getClass().getResource(this.card.getCardImg()))));
    }

    // will take Card object once cards are implemented
    public CardView(Card card) {
        this();
        setCard(card);
    }

    public CardView() {
        imageView = new ImageView();
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);

        buttonBox = new VBox();
        buttonBox.setSpacing(5);
        buttonBox.setAlignment(Pos.CENTER);

        playButton = new Button("Play Card");
        playButton.getStyleClass().add("success");
        discardButton = new Button("Discard");
        discardButton.getStyleClass().add("warn");
        buttonBox.getChildren().addAll(playButton, discardButton);
        buttonBox.setVisible(false);

        this.setHeight(300);
        this.setWidth(100);

        setAlignment(imageView, Pos.CENTER);
        setAlignment(buttonBox, Pos.CENTER);
        imageView.setScaleZ(1);
        buttonBox.setScaleZ(2);
        this.getChildren().addAll(imageView, buttonBox);
    }

}
