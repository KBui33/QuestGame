package model;

import game.components.card.Card;
import game.components.deck.AdventureDeck;
import game.components.deck.Deck;
import game.components.deck.StoryDeck;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InternalGameState implements BaseGameState, Serializable {
    public static final int MAX_PLAYERS = 4;

    private ArrayList<Player> players;
    private int numPlayers = 0;
    private Deck storyDeck;
    private Deck adventureDeck;
    private GameStatus gameStatus;
    private Card currentStoryCard;

    public InternalGameState() {
        players = new ArrayList<Player>();
        storyDeck = new StoryDeck();
        adventureDeck = new AdventureDeck();
        gameStatus = GameStatus.READY;
        currentStoryCard = null;
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
        if(gameStatus.equals(GameStatus.STARTED)) return 0;
        if(players.size() >= MAX_PLAYERS) return 0;

        players.add(numPlayers++, player);
        player.setPlayerId(numPlayers);

        return numPlayers;
    }

    public Player getPlayer(int playerId) {
        return players.get(playerId - 1);
    }

    public Player setPlayer(int playerId, Player player) {
        return players.set(playerId, player);
    }

    public void startGame() {
        storyDeck.init();
        adventureDeck.init();
        storyDeck.shuffle();
        adventureDeck.shuffle();
        dealAdventureCards(12); // Deal 12 adventure cards to each player
        gameStatus = GameStatus.STARTED;
    }

    public void dealAdventureCards(int num) {
        for (int i = 0; i < num; i++) {
            for (Player player: players) {
                player.addCard(adventureDeck.draw());
            }
        }
    }

    public List<Card> getDiscardedCards() {
        ArrayList<Card> discardPile = new ArrayList<>();
        discardPile.addAll(adventureDeck.getDiscarded());
        discardPile.addAll(storyDeck.getDiscarded());
        return discardPile;
    }

    public boolean discardAdventureCard(Card card) {
        return adventureDeck.discard(card);
    }

    public boolean discardStoryCard(Card card) {return storyDeck.discard(card);}

    public Card drawAdventureCard() {
        return adventureDeck.draw();
    }

    public Card drawStoryCard() {
        return storyDeck.draw();
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setCurrentStoryCard(Card currentStoryCard) {
        this.currentStoryCard = currentStoryCard;
    }

    @Override
    public Card getCurrentStoryCard() {
        return currentStoryCard;
    }
}
