package networking.server;

import component.card.Card;
import component.card.Rank;
import model.*;

import java.io.IOException;
import java.util.*;

public class EventRunner extends Runner{

    @Override
    public void loop() throws InterruptedException, IOException {
        try{
            Server server = Server.getInstance();
            InternalGameState gameState = server.getGameState();
            Event event = gameState.getCurrentEvent();
            shouldRespond = 0;
            server.resetNumResponded(CommandType.EVENT);

            // Set event and card
            gameState.setGameStatus(GameStatus.RUNNING_EVENT);
            EventCommand startEvent = new EventCommand(EventCommandName.EVENT_STARTED);
            startEvent.setEvent(event);
            server.notifyClients(startEvent);

            System.out.println("== Event runner says: Initializing event");
            System.out.println("== Event runner says: Event card " + gameState.getCurrentStoryCard().getTitle() + " in play");

            // Wait for all clients to be ready
            for(Player player: gameState.getPlayers()) shouldRespond++;
            waitForResponses();


            server.resetNumResponded(CommandType.EVENT);
            EventCommand runningGameCommand = new EventCommand(EventCommandName.EVENT_NON_INTERACTIVE);
            int totalCards = 0;

            // Figure out which event is being played
            switch(event.getEventCard().getTitle()){
                case "King's Recognition":{
                    // Can't do this rn (do not have something to keep track this card rn)
                    break;
                }
                case "Queen's Favor": {
                    // Send to the lowest rank players
                    // The Lowest rank players
                    ArrayList<Player> tmp = new ArrayList<>(gameState.getPlayers());
                    Collections.sort(tmp);
                    // compare based only on rank not shields
                    Rank lowestRank = tmp.get(0).getRank();
                    for(Player player: tmp) {
                        if (player.getRank().ordinal() <= lowestRank.ordinal()) event.addEventPlayer(player);
                    }

                    runningGameCommand.setCommandName(EventCommandName.EVENT_INTERACTIVE);
                    totalCards = 2;
                    break;
                }
                case "Court Called to Camelot": {
                    // Can't do this rn (ally cards not applied yet)
                    break;
                }
                case "Pox":{
                    event.addArrayEventPlayers(gameState.getPlayers());
                    // Removing the drawer
                    for(Player player: gameState.getPlayers()){
                        if(player.getPlayerId()
                                == gameState.getCurrentTurnPlayer().getPlayerId()) {
                            event.removePlayer(player);
                        }
                    }
                    runningGameCommand.setShieldResult(EventCommandName.EVENT_SHIELD_LOST);
                    runningGameCommand.setShields(1);
                    break;
                }
                case "Prosperity Throughout the Realm": {
                    // Send to all player except drawer
                    // Need to update players with new shield values
                    event.addArrayEventPlayers(gameState.getPlayers());
                    runningGameCommand.setCommandName(EventCommandName.EVENT_INTERACTIVE);
                    totalCards = 2;
                    break;
                }
                case "Plague": {
                    // Send only to drawer
                    event.addArrayEventPlayers(new ArrayList<Player>(List.of(gameState.getCurrentTurnPlayer())));
                    // Need to update drawer shield
                    runningGameCommand.setShieldResult(EventCommandName.EVENT_SHIELD_LOST);
                    runningGameCommand.setShields(2);
                    break;
                }
                case "Chivalrous Deed": {
                    // Send to players with the lowest rank and low shield
                    // The lowest amount of shields a player can have
                    ArrayList<Player> tmp = new ArrayList<>(gameState.getPlayers());
                    Collections.sort(tmp);
                    Player lowestPlayer = tmp.get(0);
                    for(Player player: tmp) {
                        if (player.compareTo(lowestPlayer) <= 0) event.addEventPlayer(player);
                    }

                    runningGameCommand.setShieldResult(EventCommandName.EVENT_SHIELD_GAIN);
                    runningGameCommand.setShields(3);
                    break;
                }
                case "King's Call to Arms": {
                    // To much work rn  :( (me don't want to do)
                    break;
                }
            }

            // Give cards to players
            for(EventPlayer player: event.getEventPlayers()){
                int playerId = player.getPlayerId();
                shouldRespond++;

                if(runningGameCommand.getCommandName() == EventCommandName.EVENT_INTERACTIVE && totalCards != 0){
                    System.out.println("== Event runner says: Sending "+ totalCards + " adventure card(s) to player " + playerId);
                    gameState.setGameStatus(GameStatus.TAKING_EVENT_ADVENTURE_CARD);

                    ArrayList<Card> cards = new ArrayList<>();
                    for(int i = 0; i < totalCards; i++) cards.add(gameState.drawAdventureCard());

                    runningGameCommand.setCards(cards);
                }else{
                    System.out.println("== Event runner says: Sending updated player to" + playerId);
                    int add_lose_shields = runningGameCommand.getShields();

                    if(runningGameCommand.getShieldResult().equals(EventCommandName.EVENT_SHIELD_LOST)
                            && player.getShields() > add_lose_shields){
                        player.decrementShields(add_lose_shields);
                    }else if (runningGameCommand.getShieldResult().equals(EventCommandName.EVENT_SHIELD_GAIN)) {
                        player.incrementShields(add_lose_shields);
                    }
                }

                gameState.setCurrentEvent(event);

                runningGameCommand.setEvent(event);
                runningGameCommand.setPlayerId(playerId);
                runningGameCommand.setPlayer(player.getPlayer());
                server.notifyClientByPlayerId(playerId, runningGameCommand);
            }

            waitForResponses();

            // Discarding Event card
            System.out.println("== Event runner says: Ending event");
            server.notifyClients(new EventCommand(EventCommandName.EVENT_COMPLETED));


            gameState.setGameStatus(GameStatus.RUNNING);

            shouldStopRunner();
            System.out.println("== Event runner says: Event completed");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void endEventTurns(){}


    @Override
    protected void waitForResponses() {
        try {
            Server server = Server.getInstance();
            while (server.getNumResponded(CommandType.EVENT) < shouldRespond) Thread.sleep(1000);
            server.resetNumResponded(CommandType.EVENT);
            shouldRespond = 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
