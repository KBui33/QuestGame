package model;

import game.components.card.Card;
import game.components.deck.Deck;
import game.components.deck.StoryDeck;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameState implements Serializable {
    public static final int MAX_PLAYERS = 4;

    private ArrayList<Player> players;
    private int numPlayers = 0;
    private List<Card> discardedCards;
    private Deck storyDeck;

    public GameState() {
        players = new ArrayList<Player>();
        discardedCards = new ArrayList<Card>();
        storyDeck = new StoryDeck();
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

    public int addPlayer(Player player) {
        if(players.size() >= MAX_PLAYERS) return 0;

        players.add(numPlayers++, player);
        player.setPlayerId(numPlayers);

        return numPlayers;
    }

    public void startGame() {
        storyDeck.shuffle();
    }

    public void discardCard(Card card) {
        discardedCards.add(card);
    }


}
