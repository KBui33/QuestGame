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

    public EventRunner(Server server) {
        this.server = server;
        this.gameState = server.getGameState();
    }

    @Override
    public void loop() throws InterruptedException {
        gameState.setGameStatus(GameStatus.RUNNING_EVENT);
        EventCommand startEvent = new EventCommand(EventCommandName.EVENT_STARTED);
        startEvent.setCard(gameState.getCurrentStoryCard());
        server.notifyClients(startEvent);
        System.out.println("== Event runner says: Initializing event");
        System.out.println("== Event runner says: Event card " + gameState.getCurrentStoryCard().getTitle() + " in play");

        // Wait for the client to get command and the card
        while (gameState.getGameStatus().equals(GameStatus.RUNNING_EVENT)) {
            Thread.sleep(1000);
        }

        // Once setup is done, get the Event object
        if(gameState.getGameStatus().equals(GameStatus.FINDING_EVENT_CARD)) event = gameState.getCurrentEvent();
        EventCommand runningGameCommand = new EventCommand();
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
                    runningGameCommand.setCommandName(EventCommandName.RUNNING_QUEEN);
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

            shouldStopRunner();
            System.out.println("== Event runner says: Ending event");
            server.notifyClients(new EventCommand(EventCommandName.EVENT_COMPLETED));

            gameState.setGameStatus(GameStatus.RUNNING);
        }catch(Exception e){
            e.printStackTrace();
            shouldStopRunner();
        }
    }
}
