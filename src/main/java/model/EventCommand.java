package model;

import java.io.Serializable;

public class EventCommand extends GameCommand implements Serializable {
    private Event event;
    private int loseShields;
    private int gainShields;

    public EventCommand() {
        super();
        commandType = CommandType.EVENT;
        loseShields = 0;
        gainShields = 0;
    }

    public EventCommand(EventCommandName commandName) {
        this();
        this.commandName = commandName;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getLoseShields() {
        return loseShields;
    }

    public int getGainShields() {
        return gainShields;
    }

    public void setLoseShields(int loseShields) {
        this.loseShields = loseShields;
    }

    public void setGainShields(int gainShields) {
        this.gainShields = gainShields;
    }

    @Override
    public String toString() {
        String cmd = super.toString();
        if(event != null) cmd += "Event: " + event.getEvent().getTitle() + ", ";
        return cmd;
    }


}
