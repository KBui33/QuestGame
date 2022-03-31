package model;

import component.card.Card;
import component.card.Rank;
import component.deck.AdventureDeck;
import component.deck.Deck;
import component.deck.StoryDeck;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InternalGameState implements BaseGameState, Serializable {
    public static final int MAX_PLAYERS = 4;

    private HashMap<Integer, Player> players;
    private int nextPlayerId = 1;
    private Deck storyDeck;
    private Deck adventureDeck;
    private GameStatus gameStatus;
    private Card currentStoryCard;
    private Quest currentQuest;
    private Event currentEvent;
    private Tournament currentTournament;
    private Player currentTurnPlayer;

    public InternalGameState() {
        players = new HashMap<>();
        storyDeck = new StoryDeck();
        adventureDeck = new AdventureDeck();
        gameStatus = GameStatus.READY;
        currentStoryCard = null;
        currentTurnPlayer = null;
    }

    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public int getNumPlayers() {
        return players.size();
    }

    public String toString() {
        return "== Game State ==\n\n" +
                "\tNum Players: " + players.size() + "\n" +
                "\tMax players: " + MAX_PLAYERS;
    }

    public Player addPlayer(Player player) {
        if(gameStatus.equals(GameStatus.STARTED)) return null;
        if(players.size() >= MAX_PLAYERS) return null;

        players.put(nextPlayerId, player);
        player.setPlayerId(nextPlayerId);
        nextPlayerId++;

        return player;
    }

    public Player removePlayer(int playerId) {
        return players.remove(playerId);
    }

    public Player getPlayer(int playerId) {
        return players.get(playerId);
    }

    public Player setPlayer(int playerId, Player player) {
        return players.put(playerId, player);
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
            for (Player player: players.values()) {
                player.addCard(adventureDeck.draw());
            }
        }

        for (Player player: players.values()) {
            System.out.println("== Cards: " + player.getCards().size());
        }
    }

    public List<Card> getDiscardedCards() {
        ArrayList<Card> discardPile = new ArrayList<>();
        discardPile.addAll(adventureDeck.getDiscarded());
        discardPile.addAll(storyDeck.getDiscarded());
        return discardPile;
    }

    public ArrayList<Card> getDiscardedAdventureCards() {
        return (ArrayList<Card>) adventureDeck.getDiscarded();
    }

    public ArrayList<Card> getDiscardedStoryCards() {
        return (ArrayList<Card>) storyDeck.getDiscarded();
    }

    public void discardAdventureCard(Card card) {
        adventureDeck.discard(card);
    }

    public void discardStoryCard(Card card) {storyDeck.discard(card);}

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

    public void setCurrentTurnPlayer(Player currentTurnPlayer) {
        this.currentTurnPlayer = currentTurnPlayer;
    }

    @Override
    public Player getCurrentTurnPlayer() {
        return currentTurnPlayer;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    public void setCurrentQuest(Quest currentQuest) {
        this.currentQuest = currentQuest;
    }

    @Override
    public Quest getCurrentQuest() {
        return currentQuest;
    }

    public void setCurrentTournament(Tournament currentTournament) {
        this.currentTournament = currentTournament;
    }

    @Override
    public Tournament getCurrentTournament() {
        return currentTournament;
    }

    @Override
    public ArrayList<Player> getWinners() {
        ArrayList<Player> winners = new ArrayList<>();
        for (Player player: players.values()) { // Find players with rank knight of the round table
            if(player.getRank().equals(Rank.ROUND_TABLE_KNIGHT)) winners.add(player);
        }

        return winners;
    }

    public void resetGame() {
        players.clear();
        storyDeck.clear();
        adventureDeck.clear();
        nextPlayerId = 0;
        gameStatus = GameStatus.READY;
        currentQuest = null;
        currentTournament = null;
        currentStoryCard = null;
        currentTurnPlayer = null;
    }
}
