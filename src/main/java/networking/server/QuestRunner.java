package networking.server;

import model.*;

import java.util.ArrayList;

public class QuestRunner extends Runner {
    // private Quest quest; TODO::Implement quest class
    private Server server;
    private InternalGameState gameState;

    public QuestRunner(Server server) {
        this.server = server;
        this.gameState = server.getGameState();
    }


    @Override
    public void loop() {
        gameState.setGameStatus(GameStatus.IN_QUEST);
        server.notifyClients(new GameCommand(Command.QUEST_STARTED));
        System.out.println("== Quest runner says: initializing quest");

    }
}
