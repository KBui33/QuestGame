package model;

import java.io.Serializable;

public class EventCommand extends GameCommand implements Serializable {
    private Event event;

    public EventCommand() {
        super();
        commandType = CommandType.EVENT;
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

    @Override
    public String toString() {
        String cmd = super.toString();
        if(event != null) cmd += "Event: " + event.getEvent().getTitle() + ", ";
        return cmd;
    }


}
