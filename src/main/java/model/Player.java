package model;

import java.io.Serializable;

public class Player implements Serializable {
    private int playerId;

    // -- TODO:: Add cards array list
    // -- TODO:: Add weapons array list

    public Player() {}

    public Player(int playerId) {
        this();
        this.playerId = playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
