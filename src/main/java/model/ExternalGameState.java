package model;

import game.components.card.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExternalGameState implements Serializable {
    private GameState gameState;

    public ExternalGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public ArrayList<Player> getGamePlayers() {
        return gameState.getPlayers();
    }

    public List<Card> getDiscardedCards() {return gameState.getDiscardedCards();}

    public int getGameNumPlayers() {
        return gameState.getNumPlayers();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public String toString() {
        return "== External Game State ==\n" +
                "\tNum Players: " + gameState.getNumPlayers() + "\n" +
                "\tMax players: " + GameState.MAX_PLAYERS;
    }


}
