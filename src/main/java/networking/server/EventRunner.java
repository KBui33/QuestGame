package networking.server;

import component.card.Card;
import component.card.Rank;
import model.*;

import java.util.*;

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
        // Need to set card
        gameState.setGameStatus(GameStatus.RUNNING_EVENT);
        EventCommand startEvent = new EventCommand(EventCommandName.EVENT_STARTED);
        startEvent.setCard(gameState.getCurrentStoryCard());
        server.notifyClients(startEvent);

        System.out.println("== Event runner says: Initializing event");
        System.out.println("== Event runner says: Event card " + gameState.getCurrentStoryCard().getTitle() + " in play");

        // Wait for the client to get command and the card
        while (gameState.getGameStatus().equals(GameStatus.RUNNING_EVENT)) Thread.sleep(1000);

        // Once setup is done, get the Event object
        if(gameState.getGameStatus().equals(GameStatus.FINDING_EVENT_CARD)) event = gameState.getCurrentEvent();

        EventCommand runningGameCommand = new EventCommand(EventCommandName.EVENT_EXTRA_INFO);

        ArrayList<Player> players = null;
        ArrayList<Card> cards = new ArrayList<>();
        try{
            // Figure out which event is being played
            switch(event.getEvent().getTitle()){
                case "King's Recognition":{
                    // Can't do this rn (do not have something to keep track this card rn)
                    break;
                }
                case "Queen's Favor": {
                    // Send to the lowest rank players
                    // The Lowest rank players
                    ArrayList<Player> eventPlayers = new ArrayList<>();

                    for(Player player: gameState.getPlayers()) {
                        if (player.getRank() == Rank.SQUIRE) eventPlayers.add(player);
                    }
                    players = eventPlayers;

                    players.forEach(
                            player -> {
                                cards.add(gameState.drawAdventureCard());
                                cards.add(gameState.drawAdventureCard());
                            });
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
                    runningGameCommand.setLoseShields(1);
                    break;
                }
                case "Prosperity Throughout the Realm": {
                    // Send to all player except drawer
                    // Need to update players with new shield values
                    players = gameState.getPlayers();

                    players.forEach(
                            player -> {
                                cards.add(gameState.drawAdventureCard());
                                cards.add(gameState.drawAdventureCard());
                            });
                    break;
                }
                case "Plague": {
                    // Send only to drawer
                    players = new ArrayList<Player>(List.of(gameState.getCurrentTurnPlayer()));
                    // Need to update drawer shield
                    runningGameCommand.setLoseShields(2);
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
                                && player.getShields() == lowestShields) players.add(player);
                    }

                    runningGameCommand.setGainShields(3);
                    break;
                }
                case "King's Call to Arms": {
                    // To much work rn  :( (me don't want to do)
                    break;
                }

            }

            //runningGameCommand.setPlayers(players);
            runningGameCommand.setCards(cards);

            if(players != null) event.addArrayEventPlayers(players);
            runningGameCommand.setEvent(event);

            // Send and wait for client to apply changes to players
            server.notifyClients(runningGameCommand);
            gameState.setGameStatus(GameStatus.RUNNING_EVENT);
            while (gameState.getGameStatus().equals(GameStatus.RUNNING_EVENT)) {Thread.sleep(1000);}

            //Update the internal state
            // Discarding Event card
            if(gameState.getGameStatus().equals(GameStatus.ENDING_EVENT)){
                gameState.discardStoryCard(event.getEvent());
            }
            //Discard card and get new card
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
