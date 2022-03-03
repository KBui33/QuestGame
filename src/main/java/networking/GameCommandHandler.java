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

        switch (command) {
            case READY:
                System.out.println("== Command handler says:  Adding new player");
                GameState gameState = server.getGameState();
                int playerId = gameState.addPlayer(new Player());
                returnCommand.setCommand(GameCommand.Command.IS_READY);
                returnCommand.setPlayerId(playerId);
                returnCommand.setReadyPlayers(gameState.getNumPlayers());
                System.out.println("== Num players: " + gameState.getNumPlayers());
                break;
        }

        server.notifyClients(returnCommand);
        return returnCommand;
    }
}
