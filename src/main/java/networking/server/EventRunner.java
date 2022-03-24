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
        server.notifyClients(new EventCommand(EventCommandName.EVENT_STARTED));
        System.out.println("== Event runner says: initializing event");

        try{
            // Figure out which event is being played
            switch(event.getEvent().getTitle()){
                case "King's Recognition":{
                    // Can't do this rn
                    break;
                }
                case "Queen's Favor": {
                    // If squire then add 2 cards to their hand
                    gameState.getPlayers().forEach(
                            player -> {
                                if(player.getRankCard().getRank() == Rank.SQUIRE){
                                    // add 2 cards to their hand
                                }
                            });
                    break;
                }
                case "Court Called to Camelot": {
                    // Can't do this rn
                    break;
                }
                case "Pox": {
                    // Remove 1 shield for every player beside the drawing player
                    gameState.getPlayers().forEach(
                            player -> {
                                if(gameState.getCurrentTurnPlayer().getPlayerId() != player.getPlayerId()) {
                                    if(player.getShields() > 0) player.setShields(player.getShields() - 1);
                                }
                            });
                    break;
                }
                case "Plague": {
                    Player drawer = gameState.getCurrentTurnPlayer();
                    if(drawer.getShields() > 0){
                        drawer.setShields(drawer.getShields() - 2);
                    }
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
