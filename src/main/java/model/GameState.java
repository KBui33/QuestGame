package model;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    public static final int MAX_PLAYERS = 4;

    private ArrayList<Player> players;
    private int numPlayers = 0;
    // TODO::Add array list of discarded cards

    public GameState() {
        players = new ArrayList<Player>();
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public String toString() {
        return "== Game State ==\n\n" +
                "\tNum Players: " + numPlayers + "\n" +
                "\tMax players: " + MAX_PLAYERS;
    }

    public boolean addPlayer(Player player) {
        if(players.size() >= MAX_PLAYERS) return false;

        players.add(numPlayers++, player);
        player.setPlayerNumber(numPlayers);

        return true;
    }


}
