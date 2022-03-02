package networking;

import model.GameCommand;
import model.Player;

import java.io.IOException;

public class GameCommandHandler {
    public GameCommand processGameCommand(GameCommand gameCommand) throws IOException {
        GameCommand.Command command =  gameCommand.getCommand();
        GameCommand returnCommand = new GameCommand();

        switch (command) {
            case READY:
                System.out.println("== Command handler says:  Adding new player");
                Server.getInstance().getGameState().addPlayer(new Player());
                returnCommand.setCommand(GameCommand.Command.IS_READY);
                System.out.println("== Num players: " + Server.getInstance().getGameState().getNumPlayers());
                break;
        }

        Server.getInstance().notifyClients(returnCommand);
        return returnCommand;
    }
}
