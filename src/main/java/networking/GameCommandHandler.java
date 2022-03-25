package networking;

import component.card.Card;
import model.*;
import networking.server.GameRunner;
import networking.server.Server;

import java.io.IOException;

public class GameCommandHandler extends CommandHandlerDecorator {

    public GameCommandHandler(CommandHandler commandHandler) {
        super(commandHandler);
    }

    @Override
    public Command processGameCommand(Command command) throws IOException {
        GameCommand gameCommand = (GameCommand) command;
        Server server = Server.getInstance();
        InternalGameState gameState = server.getGameState();
        Player player = null;

        CommandName commandName = gameCommand.getCommandName();
        int playerId = gameCommand.getPlayerId();
        if (playerId > 0) player = gameState.getPlayer(playerId);
        Card card = gameCommand.getCard();

        GameCommand returnCommand = new GameCommand();

        boolean startGame = false;

        if (commandName.equals(GameCommandName.READY)) {
            System.out.println("== Command handler says:  Adding new player");
            player = gameState.addPlayer(new Player());

            server.setClientPlayerId(gameCommand.getClientIndex(), player.getPlayerId());

            returnCommand.setCommandName(GameCommandName.IS_READY);
            returnCommand.setPlayer(player);
            returnCommand.setNumReady(gameState.getNumPlayers());

            // If all lobby players ready, start the game
            if (server.getNumClients() >= 2 && gameState.getNumPlayers() == server.getNumClients())
                startGame = true;
        } if (commandName.equals(GameCommandName.UNREADY)) {
            System.out.println("== Command handler says: Removing player " + playerId);
            player = gameState.removePlayer(playerId);

            returnCommand.setCommandName(GameCommandName.IS_UNREADY);
            returnCommand.setPlayer(player);
            returnCommand.setNumReady(gameState.getNumPlayers());
            returnCommand.setNumJoined(server.getNumClients());
        } else if (commandName.equals(GameCommandName.GET_ATTACHED_PLAYER)) {
            System.out.println("== Command handler says: Fetching player attached to client");
            returnCommand.setCommandName(GameCommandName.RETURN_ATTACHED_PLAYER);
            returnCommand.setPlayer(player);
            returnCommand.setPlayerId(gameCommand.getPlayerId());
        } else if (commandName.equals(GameCommandName.DISCARD_CARD)) {
            System.out.println("== Command handler says: Player " + playerId + " is discarding a card");
            player.discardCard(card);
            gameState.discardAdventureCard(card);
            returnCommand.setCommandName(GameCommandName.DISCARDED_CARD);
            returnCommand.setPlayer(player);
        } else if (commandName.equals(GameCommandName.END_TURN)) {
            System.out.println("== Command handler says: Player took " + playerId);

            returnCommand.setCommandName(GameCommandName.ENDED_TURN);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.RUNNING); // Update game status

        } else if (commandName.equals(GameCommandName.COMPLETE_GAME)) {
            System.out.println("== Command handler says: Player " + playerId + " completed game");

            returnCommand.setCommandName(GameCommandName.COMPLETED_GAME);
            returnCommand.setPlayer(player);
            returnCommand.setPlayerId(playerId);
        }

        server.incrementNumResponded(CommandType.GAME);
        server.notifyClients(returnCommand);

        if (startGame) new Thread(new GameRunner(server, server.getGameState())).start();

        return returnCommand;
    }
}
