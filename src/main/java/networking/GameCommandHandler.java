package networking;

import game.components.card.Card;
import model.ExternalGameState;
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
        Player player = gameCommand.getPlayer();
        ExternalGameState externalGameState = server.getExternalGameState();
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

            case DISCARD_CARD: {
                int playerId = gameCommand.getPlayerId();
                Card card = gameCommand.getCard();
                System.out.println("== Command handler says: Player " + playerId + " is discarding a card");
                System.out.println("== Player discard before: " +  player.getCards().size());
                System.out.println("== Player discard res: " +  player.discardCard(card));
                System.out.println("== Player discard after: " +  player.getCards().size());
                gameState.discardCard(card);
                returnCommand.setCommand(GameCommand.Command.DISCARDED_CARD);
                returnCommand.setPlayer(player);
                returnCommand.setPlayerId(playerId);
                break;
            }

            case END_TURN: {
                int playerId = gameCommand.getPlayerId();
                System.out.println("== Command handler says: Player took " + playerId);
                gameState.setGameStatus(GameState.GameStatus.RUNNING); // Update game status
                returnCommand.setCommand(GameCommand.Command.ENDED_TURN);
                returnCommand.setPlayerId(playerId);
                break;
            }
        }

        server.notifyClients(returnCommand);
        if(startGame)  new Thread(new GameRunner(server)).start();
        return returnCommand;
    }
}
