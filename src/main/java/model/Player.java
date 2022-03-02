package model;

import java.io.Serializable;

public class Player implements Serializable {
    private int playerNumber;

    // -- TODO:: Add cards array list
    // -- TODO:: Add weapons array list

    public Player() {

    }

    public Player(int playerNumber) {
        this();
        this.playerNumber = playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }
}
