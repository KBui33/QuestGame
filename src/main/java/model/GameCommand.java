package model;

import component.card.Card;

import java.io.*;
import java.util.ArrayList;

public class GameCommand implements Serializable {
    // Various game commands
    private int playerId = -1;
    private int clientIndex = -1;
    private int readyPlayers = 0;
    private int joinedPlayers = 0;
    private Command command;
    private Player player;
    private Card card;
    private ArrayList<Card> cards;
    private ArrayList<Player> players;
    private Quest quest;
    private Event event;

    public GameCommand() {
    }

    public GameCommand(Command command) {
        this.command = command;
    }

    public GameCommand(int playerId, Command command) {
        this.playerId = playerId;
        this.command = command;
    }

    public GameCommand(int playerId, Command command, int readyPlayers) {
        this(playerId, command);
        this.readyPlayers = readyPlayers;
    }

    public GameCommand(int playerId, Command command, int readyPlayers, int joinedPlayers) {
        this(playerId, command, readyPlayers);
        this.joinedPlayers = joinedPlayers;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setClientIndex(int clientIndex) {
        this.clientIndex = clientIndex;
    }

    public int getClientIndex() {
        return clientIndex;
    }

    public void setReadyPlayers(int readyPlayers) {
        this.readyPlayers = readyPlayers;
    }

    public int getReadyPlayers() {
        return readyPlayers;
    }

    public void setJoinedPlayers(int joinedPlayers) {
        this.joinedPlayers = joinedPlayers;
    }

    public int getJoinedPlayers() {
        return joinedPlayers;
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

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public String toString() {
        String strCmd = "GameCommand{";
        if(playerId > 0) strCmd += "playerId=" + playerId + ", ";
        strCmd += "clientIndex=" + clientIndex + ", ";
        strCmd += "command=" + command + ", ";
        strCmd += "readyPlayers=" + readyPlayers + ", ";
        strCmd += "joinedPlayers=" + joinedPlayers;
        strCmd += '}';
        return strCmd;
    }

    public static GameCommand fromBytesArray(byte[] bytes) throws IOException, ClassNotFoundException {
       ByteArrayInputStream bis = new ByteArrayInputStream(bytes, 0, bytes.length);
       ObjectInputStream ins = new ObjectInputStream(bis);
       return (GameCommand) ins.readObject();
    }

    public static byte[] toBytesArray(GameCommand gameCommand) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream outs = new ObjectOutputStream(bos);
        outs.writeObject(gameCommand);
        return bos.toByteArray();
    }
}
