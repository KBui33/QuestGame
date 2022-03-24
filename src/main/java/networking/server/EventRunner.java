package networking.server;

import component.card.Card;
import component.card.Rank;
import model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        GameCommand startEvent = new GameCommand(Command.EVENT_STARTED);
        startEvent.setCard(event.getEvent());
        server.notifyClients(startEvent);
        System.out.println("== Event runner says: initializing event");
        System.out.println("== Event runner says: event card " + event.getEvent().getTitle() + " in play");

        GameCommand runningGameCommand = new GameCommand();

        try{
            // Figure out which event is being played
            switch(event.getEvent().getTitle()){
                case "King's Recognition":{
                    // Can't do this rn (do not have something to keep track this card rn)
                    break;
                }
                case "Queen's Favor": {
                    // Send two 2 cards to player

                    List<Card> adventureCards = Arrays.asList(gameState.drawAdventureCard(), gameState.drawAdventureCard());
                    runningGameCommand.setEventCommand(EventCommand.RUNNING_QUEEN);
                    runningGameCommand.setEvent(event);
                    runningGameCommand.setCards((ArrayList<Card>) adventureCards);

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

            System.out.println("== Event runner says: Ending event");
            server.notifyClients(new GameCommand(Command.EVENT_COMPLETED));

            gameState.setGameStatus(GameStatus.RUNNING);
        }catch(Exception e){
            e.printStackTrace();
            shouldStopRunner();
        }
    }
}
