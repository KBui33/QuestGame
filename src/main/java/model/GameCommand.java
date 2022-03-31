package model;

import component.card.Card;

import java.io.*;
import java.util.ArrayList;

public class GameCommand extends BaseCommand implements Serializable {
    protected int playerId = -1;
    protected Player player;
    protected Card card;
    protected ArrayList<Card> cards;
    protected ArrayList<Player> players;

    public GameCommand() {
        super();
        commandType = CommandType.GAME;
    }

    public GameCommand(GameCommandName commandName) {
        this();
        this.commandName = commandName;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    @Override
    public String toString() {
        String cmd = super.toString();
        cmd += "Player ID: " + playerId + ", ";
        return cmd;
    }
}
