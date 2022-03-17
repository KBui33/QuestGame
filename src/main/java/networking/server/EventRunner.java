package networking.server;

import model.Event;
import model.InternalGameState;

public class EventRunner extends Runner{
    private Server server;
    private InternalGameState gameState;
    private Event event;

    public EventRunner(Server server, InternalGameState gameState, Event event) {
        this.server = server;
        this.gameState = gameState;
        this.event = event;
    }

    @Override
    public void loop() {

    }
}
