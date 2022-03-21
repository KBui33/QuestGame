package model;

import component.card.EventCard;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {

    private EventCard event;

    public Event(EventCard event){
        this.event = event;
    }

    public EventCard getEvent() {
        return event;
    }

}
