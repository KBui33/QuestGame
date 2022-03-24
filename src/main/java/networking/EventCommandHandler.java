package networking;

import component.card.Card;
import model.*;
import networking.server.GameRunner;
import networking.server.Server;

import java.io.IOException;

public class EventCommandHandler extends CommandHandlerDecorator {

    public EventCommandHandler(CommandHandler commandHandler) {
        super(commandHandler);
    }

    @Override
    public Command processGameCommand(Command command) throws IOException {
        EventCommand eventCommand = (EventCommand) command;
        Server server = Server.getInstance();
        InternalGameState gameState = server.getGameState();
        Player player = null;

        CommandName commandName = eventCommand.getCommandName();
        int playerId = eventCommand.getPlayerId();
        if (playerId > 0) player = gameState.getPlayer(playerId);

        GameCommand returnCommand = new GameCommand();

        if (commandName.equals(EventCommandName.END_EVENT)) {
            System.out.println("== Command handler says: Player " + playerId +" ended event");

            returnCommand.setCommandName(EventCommandName.ENDED_EVENT);
            returnCommand.setPlayer(player);
            gameState.setGameStatus(GameStatus.ENDING_QUEST);
        }

        server.incrementNumResponded(CommandType.EVENT);
        server.notifyClients(returnCommand);

        return returnCommand;
    }
}
