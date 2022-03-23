package networking.server;

import component.card.Rank;
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
        System.out.println("== Event runner says: event card " + event.getEvent().getTitle() + " in play");

        try{
            // Figure out which event is being played
            switch(event.getEvent().getTitle()){
                case "King's Recognition":{
                    // Can't do this rn (do not have something to keep track this card rn)
                    break;
                }
                case "Queen's Favor": {
                    //

                    break;
                }
                case "Court Called to Camelot": {
                    // Can't do this rn (ally cards not applied yet)
                    break;
                }
                case "Pox": {
                    //
                    break;
                }
                case "Plague": {
                    //
                    break;
                }
                case "Chivalrous Deed": {
                    //
                    break;
                }
                case "Prosperity Throughout the Realm": {
                    //
                    break;
                }
                case "King's Call to Arms": {
                    //
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            shouldStopRunner();
        }
    }
}
