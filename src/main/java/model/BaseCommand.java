package model;

import java.io.*;

public class BaseCommand extends Command implements Serializable {
    protected int clientIndex = -1;
    protected int numReady = 0;
    protected int numJoined = 0;

    public BaseCommand() {
        super();
        this.commandType = CommandType.BASE;
    }

    public BaseCommand(BaseCommandName commandName) {
        this();
        this.commandName = commandName;
    }

    public void setClientIndex(int clientIndex) {
        this.clientIndex = clientIndex;
    }

    public int getClientIndex() {
        return clientIndex;
    }

    public void setNumReady(int numReady) {
        this.numReady = numReady;
    }

    public int getNumReady() {
        return numReady;
    }

    public void setNumJoined(int numJoined) {
        this.numJoined = numJoined;
    }

    public int getNumJoined() {
        return numJoined;
    }

    @Override
    public String toString() {
        String cmd = super.toString();
        cmd += "Client index: " + clientIndex + ", ";

        return cmd;
    }
}
