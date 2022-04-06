package model;

import java.io.Serializable;

public class EventCommand extends GameCommand implements Serializable {
    private Event event;
    private int shields;
    private EventCommandName shieldResult;

    public EventCommand() {
        super();
        commandType = CommandType.EVENT;
        this.shields = 0;
        this.shieldResult = null;
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

    public EventCommandName getShieldResult() {
        return shieldResult;
    }

    public void setShieldResult(EventCommandName shieldResult) {
        this.shieldResult = shieldResult;
    }

    @Override
    public String toString() {
        String cmd = super.toString();
        if(event != null) cmd += "Event: " + event.getEventCard().getTitle() + ", ";
        return cmd;
    }


}
