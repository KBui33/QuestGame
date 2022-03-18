package networking.server;

import model.*;

public class EventRunner extends Runner{
    private Server server;
    private InternalGameState gameState;
    private Event event;

    public EventRunner(Server server, Event event) {
        this.server = server;
        this.gameState = server.getGameState();
        this.event = event;
    }

    @Override
    public void loop() {
        gameState.setGameStatus(GameStatus.RUNNING_EVENT);
        server.notifyClients(new GameCommand(Command.EVENT_STARTED));
        System.out.println("== Event runner says: initializing event");

        try{
            // Figure out which event is being played
            switch(event.getEvent().getTitle()){
                case "King's Recognition":{
                    break;
                }
                case "Queen's Favor": {
                    break;
                }
                case "Court Called to Camelot": {
                    break;
                }
                case "Pox": {
                    break;
                }
                case "Plague": {
                    break;
                }
                case "Chivalrous Deed": {
                    break;
                }
                case "Prosperity Throughout the Realm": {
                    break;
                }
                case "King's Call to Arms": {
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            shouldStopRunner();
        }
    }
}
