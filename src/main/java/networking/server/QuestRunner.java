package networking.server;

import model.*;

import java.util.ArrayList;

public class QuestRunner extends Runner {
    // private Quest quest; TODO::Implement quest class
    private Server server;
    private InternalGameState gameState;
    private Quest quest;

    public QuestRunner(Server server, Quest quest) {
        this.server = server;
        this.gameState = server.getGameState();
        this.quest = quest;
    }


    @Override
    public void loop() {
        gameState.setGameStatus(GameStatus.IN_QUEST);
        server.notifyClients(new GameCommand(Command.QUEST_STARTED));
        System.out.println("== Quest runner says: initializing quest");

        try {
            // For now assume all players are participating in quest
            // TODO:: Add setup step to get participating players
            for (Player player: gameState.getPlayers()) {
                quest.addQuestPlayer(player);
            }

            for(Stage stage: quest.getStages()) {
                for(QuestPlayer questPlayer: quest.getQuestPlayers()) {
                    int playerId = questPlayer.getPlayerId();
                    System.out.println(playerId);
                    // TODO:: Scaffold command
                    GameCommand questStageCommand = new GameCommand(Command.TAKE_QUEST_TURN);
                    questStageCommand.setCard(stage.getStageCard());
                    server.notifyClient(playerId, questStageCommand);

                    Thread.sleep(2000);
                }

                System.out.println("== Stage completed");
                Thread.sleep(2000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
