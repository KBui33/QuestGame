package networking;

import component.card.Card;
import model.*;
import networking.server.GameRunner;
import networking.server.Server;

import java.io.IOException;
import java.util.ArrayList;

public class BaseCommandHandler implements CommandHandler {
    @Override
    public Command processGameCommand(Command command) throws IOException {
        BaseCommand baseCommand = (BaseCommand) command;
        Server server = Server.getInstance();
        InternalGameState gameState = server.getGameState();

        CommandName commandName = baseCommand.getCommandName();
        int clientIndex = baseCommand.getClientIndex();

        BaseCommand returnCommand = new BaseCommand();

        boolean startGame = false;

        if (commandName.equals(BaseCommandName.GET_LOBBY_STATE)) {
            System.out.println("== Command handler says: Fetching lobby state");
            returnCommand.setCommandName(BaseCommandName.RETURN_LOBBY_STATE);
            returnCommand.setNumReady(gameState.getNumPlayers());
            returnCommand.setNumJoined(server.getNumClients());
        } else if (commandName.equals(BaseCommandName.DISCONNECT)) {
            System.out.println("== Command handler says: Client " + clientIndex + " is disconnecting");
            if(clientIndex >= 0) server.removeClient(clientIndex);
            returnCommand.setCommandName(BaseCommandName.DISCONNECTED);
        }

        server.incrementNumResponded(CommandType.BASE, clientIndex);
        server.notifyClients(returnCommand);

        if (startGame) new Thread(new GameRunner(server, server.getGameState())).start();

        return returnCommand;
    }
}
