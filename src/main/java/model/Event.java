package model;

import component.card.EventCard;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {

    private EventCard event;
    private ArrayList<EventPlayer> players;

    public Event(EventCard event){
        this.event = event;
        this.players = new ArrayList<>();
    }

    public EventCard getEvent() {
        return event;
    }

    public ArrayList<EventPlayer> getPlayers() {
        return players;
    }
}
