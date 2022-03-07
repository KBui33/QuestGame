package networking;

import model.GameCommand;
import model.GameState;
import model.Player;
import networking.server.GameRunner;
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
        boolean startGame = false;

        switch (command) {
            case READY: {
                System.out.println("== Command handler says:  Adding new player");
                int playerId = gameState.addPlayer(new Player());
                returnCommand.setCommand(GameCommand.Command.IS_READY);
                returnCommand.setPlayerId(playerId);
                returnCommand.setReadyPlayers(gameState.getNumPlayers());

                // If all lobby players ready, start the game
                if(server.getNumClients() >= 2 && gameState.getNumPlayers() == server.getNumClients()) startGame = true;
                break;
            }

            case GET_ATTACHED_PLAYER: {
                System.out.println("== Command handler says: Fetching player attached to client");
                returnCommand.setCommand(GameCommand.Command.RETURN_ATTACHED_PLAYER);
                returnCommand.setPlayer(gameState.getPlayer(gameCommand.getPlayerId()));
                break;
            }

            case GET_LOBBY_STATE: {
                System.out.println("== Command handler says: Fetching lobby state");
                returnCommand.setCommand(GameCommand.Command.RETURN_LOBBY_STATE);
                returnCommand.setReadyPlayers(gameState.getNumPlayers());
                returnCommand.setJoinedPlayers(server.getNumClients());
                break;
            }

            case TAKE_TURN: {
                int playerId = gameCommand.getPlayerId();
                System.out.println("== Command handler says: Player took " + playerId);
                returnCommand.setCommand(GameCommand.Command.TOOK_TURN);
                returnCommand.setPlayerId(playerId);
                break;
            }
        }

        server.notifyClients(returnCommand);
        if(startGame)  new Thread(new GameRunner(server)).start();
        return returnCommand;
    }
}
