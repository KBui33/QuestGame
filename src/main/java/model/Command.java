package model;

import java.io.*;

public class Command implements Serializable {
    protected CommandName commandName;
    protected CommandType commandType;


    public Command() {
    }

    public Command(CommandName commandName) {
        this.commandName = commandName;
    }

    public Command(CommandType commandType) {
        this.commandType = commandType;
    }

    public Command(CommandName commandName, CommandType commandType) {
        this(commandName);
        this.commandType = commandType;
    }

    public void setCommandName(CommandName commandName) {
        this.commandName = commandName;
    }

    public CommandName getCommandName() {
        return commandName;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    @Override
    public String toString() {
        String cmd = "== Command: ";
        cmd += "Name: " + commandName + ", ";
        cmd += "Type: " + commandType + ", ";

        return cmd;
    }

    public static Command fromBytesArray(byte[] bytes) throws IOException, ClassNotFoundException {
        Command command = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes, 0, bytes.length);
        ObjectInputStream ins = new ObjectInputStream(bis);
        try {
            command = (Command) ins.readObject();
        } catch (EOFException e) {}
        ins.close();
        return command;
    }

    public static byte[] toBytesArray(Command command) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream outs = new ObjectOutputStream(bos);
        outs.writeObject(command);
        outs.close();
        return bos.toByteArray();
    }
}
