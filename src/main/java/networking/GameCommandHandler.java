package networking;

import model.GameCommand;
import model.GameState;
import model.Player;
import networking.server.Server;

import java.io.IOException;

public class GameCommandHandler {
    private Server server;
    public GameCommandHandler(Server server) {
        this.server = server;
    }

    public GameCommand processGameCommand(GameCommand gameCommand) throws IOException {
        GameCommand.Command command =  gameCommand.getCommand();
        GameCommand returnCommand = new GameCommand();
        GameState gameState = server.getGameState();

        switch (command) {
            case READY: {
                System.out.println("== Command handler says:  Adding new player");
                int playerId = gameState.addPlayer(new Player());
                returnCommand.setCommand(GameCommand.Command.IS_READY);
                returnCommand.setPlayerId(playerId);
                returnCommand.setReadyPlayers(gameState.getNumPlayers());

                // If all lobby players ready, start the game
                if(server.getNumClients() >= 2 && gameState.getNumPlayers() == server.getNumClients()) server.notifyClients(new GameCommand(GameCommand.Command.GAME_STARTED));
                break;
            }

            case GET_LOBBY_STATE: {
                System.out.println("== Command handler says: Fetching lobby state");
                returnCommand.setCommand(GameCommand.Command.RETURN_LOBBY_STATE);
                returnCommand.setReadyPlayers(gameState.getNumPlayers());
                returnCommand.setJoinedPlayers(server.getNumClients());
                break;
            }
        }

        server.notifyClients(returnCommand);
        return returnCommand;
    }
}
