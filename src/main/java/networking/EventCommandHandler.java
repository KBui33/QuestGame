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
        Event event = gameState.getCurrentEvent();
        Player player = null;

        CommandName commandName = eventCommand.getCommandName();
        int playerId = eventCommand.getPlayerId();
        if (playerId > 0) player = gameState.getPlayer(playerId);

        EventCommand returnCommand = new EventCommand();

        if (commandName.equals(EventCommandName.END_EVENT)) {
            System.out.println("== Command handler says: Player " + playerId +" ended event");

            returnCommand.setCommandName(EventCommandName.PLAYER_END_EVENT);
            returnCommand.setPlayer(player);
            gameState.setGameStatus(GameStatus.ENDING_EVENT);
        }else if (commandName.equals(EventCommandName.SETUP_COMPLETE)){
            System.out.println("== Command handler says: Event is running");

            event = eventCommand.getEvent();
            gameState.setCurrentEvent(event);

            returnCommand.setCommandName(EventCommandName.FIND_CARD_INTERACTIVE_OR_NOT);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.FINDING_EVENT_CARD);
        }

        server.incrementNumResponded(CommandType.EVENT);
        server.notifyClients(returnCommand);

        return returnCommand;
    }
}
