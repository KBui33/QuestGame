package gui.partials.event;

import gui.partials.CardView;
import gui.partials.CardsReceivedView;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * @author James DiNovo
 *
 * Displays the current event to the user and allows them to take any required actions
 */
public class EventView extends BorderPane {

    public enum Mode {
        NONINTERACTIVE,
        CARDS_RECEIVED
    }

    private Text headerText, infoText;
    private CardView eventCard;
    private VBox infoBox;
    private CardsReceivedView cardsReceivedView;
    private Button continueButton;

    public EventView() {

        headerText = new Text("Event");
        headerText.getStyleClass().add("header-font");

        infoText = new Text();
        infoText.getStyleClass().add("body-font");

        continueButton = new Button("Continue");
        continueButton.getStyleClass().add("success");

        eventCard = new CardView();
        eventCard.setSize(200);
        setAlignment(eventCard, Pos.CENTER);

        infoBox = new VBox();
        infoBox.setSpacing(5);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.getChildren().addAll(headerText, eventCard, infoText);

        this.setTop(infoBox);

        cardsReceivedView = new CardsReceivedView();
    }

    public void mode(EventView.Mode c) {
        switch (c) {
            case NONINTERACTIVE:
                this.setCenter(this.continueButton);
                break;
            case CARDS_RECEIVED:
                this.setCenter(this.cardsReceivedView);
                break;
            default:
                clearEvent();
        }
    }

    public void clearEvent() {
//        this.infoText.setText("");
        this.setCenter(null);
    }

    public Text getHeaderText() {
        return headerText;
    }

    public Text getInfoText() {
        return infoText;
    }

    public CardView getEventCard() {
        return eventCard;
    }

    public CardsReceivedView getCardsReceivedView() {
        return cardsReceivedView;
    }

    public Button getContinueButton() {
        return continueButton;
    }
}
