package model;

import game.components.card.EventCard;

import java.io.Serializable;

public class Event implements Serializable {

    private EventCard event;

    public Event(EventCard event){
        this.event = event;
    }

}
