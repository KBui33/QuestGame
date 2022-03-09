package networking;

import game.components.card.Card;
import model.*;
import networking.server.GameRunner;
import networking.server.Server;

import java.io.IOException;

public class GameCommandHandler {
    private Server server;
    public GameCommandHandler(Server server) {
        this.server = server;
    }

    public GameCommand processGameCommand(GameCommand gameCommand) throws IOException {
        Command command =  gameCommand.getCommand();
        GameCommand returnCommand = new GameCommand();
        InternalGameState internalGameState = server.getGameState();
        Player player = gameCommand.getPlayer();
        ExternalGameState externalGameState = server.getExternalGameState();
        boolean startGame = false;

        switch (command) {
            case READY: {
                System.out.println("== Command handler says:  Adding new player");
                int playerId = internalGameState.addPlayer(new Player());
                returnCommand.setCommand(Command.IS_READY);
                returnCommand.setPlayerId(playerId);
                returnCommand.setReadyPlayers(internalGameState.getNumPlayers());

                // If all lobby players ready, start the game
                if(server.getNumClients() >= 2 && internalGameState.getNumPlayers() == server.getNumClients()) startGame = true;
                break;
            }

            case GET_ATTACHED_PLAYER: {
                System.out.println("== Command handler says: Fetching player attached to client");
                returnCommand.setCommand(Command.RETURN_ATTACHED_PLAYER);
                returnCommand.setPlayer(internalGameState.getPlayer(gameCommand.getPlayerId()));
                break;
            }

            case GET_LOBBY_STATE: {
                System.out.println("== Command handler says: Fetching lobby state");
                returnCommand.setCommand(Command.RETURN_LOBBY_STATE);
                returnCommand.setReadyPlayers(internalGameState.getNumPlayers());
                returnCommand.setJoinedPlayers(server.getNumClients());
                break;
            }

            case DISCARD_CARD: {
                int playerId = gameCommand.getPlayerId();
                Card card = gameCommand.getCard();
                System.out.println("== Command handler says: Player " + playerId + " is discarding a card");
                player.discardCard(card);
                internalGameState.discardAdventureCard(card);
                returnCommand.setCommand(Command.DISCARDED_CARD);
                returnCommand.setPlayer(player);
                returnCommand.setPlayerId(playerId);
                break;
            }

            case END_TURN: {
                int playerId = gameCommand.getPlayerId();
                System.out.println("== Command handler says: Player took " + playerId);
                internalGameState.setGameStatus(GameStatus.RUNNING); // Update game status
                returnCommand.setCommand(Command.ENDED_TURN);
                returnCommand.setPlayerId(playerId);
                break;
            }
        }

        server.notifyClients(returnCommand);
        if(startGame)  new Thread(new GameRunner(server)).start();
        return returnCommand;
    }
}
