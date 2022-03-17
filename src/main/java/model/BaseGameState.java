package model;

import component.card.Card;

import java.util.ArrayList;
import java.util.List;

public interface BaseGameState {
    public ArrayList<Player> getPlayers();

    public int getNumPlayers();

    public String toString();

    public Player getPlayer(int playerId);

    public List<Card> getDiscardedCards();

    public GameStatus getGameStatus();

    public Card getCurrentStoryCard();

    public Player getCurrentTurnPlayer();

    public Quest getCurrentQuest();
}
