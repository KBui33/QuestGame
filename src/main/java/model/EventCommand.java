package model;

import java.io.Serializable;

public class EventCommand extends GameCommand implements Serializable {
    private Event event;
    private int shields;

    public EventCommand() {
        super();
        commandType = CommandType.EVENT;
        this.shields = 0;
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

    public int getShields() {
        return shields;
    }

    public void setShields(int shields) {
        this.shields = shields;
    }

    @Override
    public String toString() {
        String cmd = super.toString();
        if(event != null) cmd += "Event: " + event.getEvent().getTitle() + ", ";
        return cmd;
    }


}
