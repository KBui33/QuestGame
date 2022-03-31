package model;

import java.io.Serializable;

public class EventPlayer extends Player implements Serializable {
    private Player player;

    public EventPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
