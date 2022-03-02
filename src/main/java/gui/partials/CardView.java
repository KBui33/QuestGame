package gui.partials;

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

    public ImageView getImageView() {
        return imageView;
    }

    public VBox getButtonBox() {
        return buttonBox;
    }

    public Button getDiscardButton() {
        return discardButton;
    }

    public Button getPlayButton() {
        return playButton;
    }

    // will take Card object once cards are implemented
    public CardView(Image img) {
        imageView = new ImageView(img);
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);

        buttonBox = new VBox();
        buttonBox.setSpacing(5);
        buttonBox.setAlignment(Pos.CENTER);

        playButton = new Button("Play Card");
        playButton.getStyleClass().add("success");
        discardButton = new Button("Discard");
        discardButton.getStyleClass().add("caution");
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

    public CardView(String url) {
        this(new Image(String.valueOf(CardView.class.getResource(url))));
    }
}
