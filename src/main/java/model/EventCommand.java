package model;

public class EventCommand extends GameCommand {
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
        cmd += "Event: " + event.getEvent().getTitle() + ", ";
        return cmd;
    }


}
