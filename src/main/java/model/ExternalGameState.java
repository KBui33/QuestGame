package model;

import component.card.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExternalGameState implements BaseGameState, Serializable {
    private InternalGameState internalGameState;

    public ExternalGameState(InternalGameState internalGameState) {
        this.internalGameState = internalGameState;
    }

    @Override
    public ArrayList<Player> getPlayers() {
        return internalGameState.getPlayers();
    }

    @Override
    public List<Card> getDiscardedCards() {return internalGameState.getDiscardedCards();}

    @Override
    public int getNumPlayers() {
        return internalGameState.getNumPlayers();
    }

    @Override
    public GameStatus getGameStatus() {
        return internalGameState.getGameStatus();
    }

    @Override
    public Player getPlayer(int playerId) {
        return internalGameState.getPlayer(playerId);
    }

    @Override
    public Card getCurrentStoryCard() {
        return internalGameState.getCurrentStoryCard();
    }

    @Override
    public Player getCurrentTurnPlayer() {
        return internalGameState.getCurrentTurnPlayer();
    }

    @Override
    public Quest getCurrentQuest() {
        return internalGameState.getCurrentQuest();
    }

    @Override
    public String toString() {
        return "== External Game State ==\n" +
                "\tNum Players: " + internalGameState.getNumPlayers() + "\n" +
                "\tMax players: " + InternalGameState.MAX_PLAYERS;
    }


}
