package gui.controllers.event;

import component.card.Card;
import component.card.EventCard;
import gui.controllers.GameController;
import gui.partials.event.EventView;
import model.Event;
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
        this.event = event;
    }

    public void showInteractiveEvent(EventCard event, Callback<ArrayList<Card>> callback) {

    }

    public void showNonInteractiveEvent(EventCard event, CallbackEmpty callback) {

    }

    private void showGUI() {
        parent.getView().getMainPane().clear();
        parent.getView().getMainPane().add(this.eventView);
    }

}
