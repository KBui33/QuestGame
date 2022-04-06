package model;

import component.card.EventCard;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {

    private EventCard event;
    private ArrayList<EventPlayer> eventPlayers;

    public Event(EventCard event){
        this.event = event;
        this.eventPlayers = new ArrayList<>();
    }

    public ArrayList<EventPlayer> getEventPlayers() {
        return eventPlayers;
    }

    public void addEventPlayer(Player p){
        this.eventPlayers.add(new EventPlayer(p));
    }

    public boolean removePlayer(Player player){return this.eventPlayers.remove(new EventPlayer(player));}

    public void addArrayEventPlayers(ArrayList<Player> eventPlayers) {
        eventPlayers.forEach(this::addEventPlayer);
    }

    public EventCard getEventCard() {
        return event;
    }

}
