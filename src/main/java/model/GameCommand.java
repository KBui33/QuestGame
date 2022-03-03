package model;

import java.io.*;

public class GameCommand implements Serializable {
    // Various game commands
    public static enum Command {
        JOIN,
        JOINED,
        READY,
        IS_READY,
        QUIT,
        QUITED
    };

    private int playerId = 0;
    private int readyPlayers = 0;
    private int joinedPlayers = 0;
    private Command command;

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

    @Override
    public String toString() {
        String strCmd = "GameCommand{";
        if(playerId > 0) strCmd += "playerId=" + playerId + ", ";
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
