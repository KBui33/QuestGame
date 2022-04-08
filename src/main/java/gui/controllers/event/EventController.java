package gui.controllers.event;

import component.card.Card;
import component.card.EventCard;
import gui.controllers.CardsReceivedController;
import gui.controllers.GameController;
import gui.partials.event.EventView;
import model.EventCommandName;
import utils.Callback;
import utils.CallbackEmpty;

import java.util.ArrayList;

/**
 * @author James DiNovo
 *
 * Handles displaying event information to user and taking user inputs
 */
public class EventController {

    // just use event card for now
    private EventCard event;
    private GameController parent;
    private EventView eventView;

    public EventController(GameController parent, EventCard event) {
        this.parent = parent;
        this.eventView = new EventView();
        updateEvent(event);
    }

    public void showInteractiveEvent(EventCard event, ArrayList<Card> cards, Callback<ArrayList<Card>> callback) {
        updateEvent(event);
        eventView.mode(EventView.Mode.CARDS_RECEIVED);
        showGui();

        CardsReceivedController cardsReceivedController = new CardsReceivedController(parent, eventView.getCardsReceivedView());
        cardsReceivedController.receiveCards(cards, chosen -> {
            cleanGui();
            callback.call(chosen);
        });
    }

    public void showNonInteractiveEvent(EventCard event, int shields, EventCommandName shieldResult, CallbackEmpty callback) {
        updateEvent(event);
        switch (shieldResult) {
            case EVENT_SHIELD_LOST -> {
                eventView.getInfoText().setText(eventView.getInfoText().getText() + ": You have lost " + shields + " shields.");
            }
            case EVENT_SHIELD_GAIN -> {
                eventView.getInfoText().setText(eventView.getInfoText().getText() + ": You have gained " + shields + " shields.");
            }
        }

        eventView.mode(EventView.Mode.NONINTERACTIVE);
        showGui();

        eventView.getContinueButton().setOnAction(e -> {
            cleanGui();
            callback.call();
        });
    }

    private void showGui() {
        parent.getView().getMainPane().clear();
        parent.getView().getMainPane().add(this.eventView);
    }

    private void cleanGui() {
        // clear view
        parent.getView().getMainPane().clear();
    }

    private void updateEvent(EventCard event) {
        eventView.clearEvent();
        this.event = event;
        this.eventView.getEventCard().setCard(this.event);
        this.eventView.getInfoText().setText(this.event.getTitle());
    }

}
