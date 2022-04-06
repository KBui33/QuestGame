package networking.server;

import component.card.Card;
import component.card.Rank;
import model.*;

import java.io.IOException;
import java.util.*;

public class EventRunner extends Runner{
    private Server server;
    private InternalGameState gameState;
    private Event event;

    @Override
    public void loop() throws InterruptedException, IOException {

        Server server = Server.getInstance();
        InternalGameState gameState = server.getGameState();
        Event event = gameState.getCurrentEvent();

        // Set event and card
        gameState.setGameStatus(GameStatus.RUNNING_EVENT);
        EventCommand startEvent = new EventCommand(EventCommandName.EVENT_STARTED);
        startEvent.setEvent(event);
        server.notifyClients(startEvent);

        System.out.println("== Event runner says: Initializing event");
        System.out.println("== Event runner says: Event card " + gameState.getCurrentStoryCard().getTitle() + " in play");

        // Wait for the client to get command and the card
        while (gameState.getGameStatus().equals(GameStatus.RUNNING_EVENT)) Thread.sleep(1000);

        shouldRespond = 0;
        server.resetNumResponded(CommandType.EVENT);

        EventCommand runningGameCommand = new EventCommand(EventCommandName.EVENT_NON_INTERACTIVE);
        runningGameCommand.setEvent(event);
        ArrayList<Player> players = new ArrayList<>();
        int totalCards = 0;
        try{
            // Figure out which event is being played
            switch(gameState.getCurrentStoryCard().getTitle()){
                case "King's Recognition":{
                    // Can't do this rn (do not have something to keep track this card rn)
                    break;
                }
                case "Queen's Favor": {
                    // Send to the lowest rank players
                    // The Lowest rank players
                    for(Player player: gameState.getPlayers()) {
                        if (player.getRank() == Rank.SQUIRE) players.add(player);
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
                    players = gameState.getPlayers();
                    // Removing the drawer
                    for(Player player: gameState.getPlayers()){
                        if(player.getPlayerId()
                                == gameState.getCurrentTurnPlayer().getPlayerId()) {
                            players.remove(player);
                        }
                    }
                    runningGameCommand.setShieldResult(EventCommandName.EVENT_SHIELD_LOST);
                    runningGameCommand.setShields(1);
                    break;
                }
                case "Prosperity Throughout the Realm": {
                    // Send to all player except drawer
                    // Need to update players with new shield values
                    players = gameState.getPlayers();
                    runningGameCommand.setCommandName(EventCommandName.EVENT_INTERACTIVE);
                    totalCards = 2;
                    break;
                }
                case "Plague": {
                    // Send only to drawer
                    players = new ArrayList<Player>(List.of(gameState.getCurrentTurnPlayer()));
                    // Need to update drawer shield
                    runningGameCommand.setShieldResult(EventCommandName.EVENT_SHIELD_LOST);
                    runningGameCommand.setShields(2);
                    break;
                }
                case "Chivalrous Deed": {
                    // Send to players with the lowest rank and low shield
                    // The lowest amount of shields a player can have
                    int lowestShields = gameState.getPlayers()
                            .stream()
                            .min(Comparator.comparing(Player::getShields))
                            .orElseThrow().getShields();

                    // Getting players that match conditions
                    for (Player player: gameState.getPlayers()){
                        if (player.getRank() == Rank.SQUIRE
                                && player.getShields() == lowestShields) {
                            players.add(player);
                        }
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
            if(runningGameCommand.getCommandName() == EventCommandName.EVENT_INTERACTIVE && totalCards != 0){
                for(Player player: players){
                    int playerId = player.getPlayerId();
                    shouldRespond++;

                    System.out.println("== Event runner says: Sending "+ totalCards + " adventure card(s) to player " + playerId);
                    gameState.setGameStatus(GameStatus.TAKING_EVENT_ADVENTURE_CARD);

                    ArrayList<Card> cards = new ArrayList<>();
                    for(int i = 0; i < totalCards; i++) cards.add(gameState.drawAdventureCard());

                    runningGameCommand.setCards(cards);
                    runningGameCommand.setPlayerId(playerId);
                    runningGameCommand.setPlayer(player);
                    server.notifyClientByPlayerId(playerId, runningGameCommand);
                }

                waitForResponses();
            }else {
                for(Player player: players){
                    int playerId = player.getPlayerId();

                    int add_lose_shields = runningGameCommand.getShields();

                    if(runningGameCommand.getShieldResult().equals(EventCommandName.EVENT_SHIELD_GAIN)){
                        player.incrementShields(add_lose_shields);
                    }else {
                        player.decrementShields(add_lose_shields);
                    }

                    runningGameCommand.setPlayerId(playerId);
                    runningGameCommand.setPlayer(player);
                    server.notifyClientByPlayerId(playerId, runningGameCommand);
                }

                while (gameState.getGameStatus().equals(GameStatus.RUNNING_EVENT)) Thread.sleep(1000);
            }

            //Update the internal state
            System.out.println("== Event runner says: Updating internal state");

            // Discarding Event card
            if(gameState.getGameStatus().equals(GameStatus.ENDING_EVENT)){
                gameState.discardStoryCard(gameState.getCurrentStoryCard());
            }

            System.out.println("== Event runner says: Ending event");
            server.notifyClients(new EventCommand(EventCommandName.EVENT_COMPLETED));

            gameState.setGameStatus(GameStatus.RUNNING);

            shouldStopRunner();
            System.out.println("== Event runner says: Event completed");
        }catch(Exception e){
            e.printStackTrace();
            shouldStopRunner();
        }
    }

    public void endEventTurns(){}


    @Override
    protected void waitForResponses() {
        try {
            while (server.getNumResponded(CommandType.EVENT) < shouldRespond) Thread.sleep(1000);
            server.resetNumResponded(CommandType.EVENT);
            shouldRespond = 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
