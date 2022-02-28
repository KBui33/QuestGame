package logic;

import java.io.*;

public class GameCommand implements Serializable {
    // Various game commands
    public static enum Command {
        JOIN,
        JOINED,
        READY,
        IS_READY,
        QUIT,
        QUITTED
    };

    private int playerId;
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

    @Override
    public String toString() {
        return "GameCommand{" +
                "playerId=" + playerId +
                ", command=" + command +
                '}';
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
