package model;

import java.io.Serializable;
import java.util.ArrayList;

public class ExternalGameState implements Serializable {
    private GameState gameState;

    public ExternalGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public ArrayList<Player> getGamePlayers() {
        return gameState.getPlayers();
    }

    public int getGameNumPlayers() {
        return gameState.getNumPlayers();
    }

    public String toString() {
        return "== External Game State ==\n" +
                "\tNum Players: " + gameState.getNumPlayers() + "\n" +
                "\tMax players: " + GameState.MAX_PLAYERS;
    }


}
