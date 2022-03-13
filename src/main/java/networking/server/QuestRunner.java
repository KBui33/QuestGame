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
        gameState.setCurrentQuest(this.quest);
        gameState.setGameStatus(GameStatus.RUNNING_QUEST);
        server.notifyClients(new GameCommand(Command.QUEST_STARTED));
        System.out.println("== Quest runner says: initializing quest");

        try {
            // For now assume all players are participating in quest
            // TODO:: Add setup step to get participating players
            for (Player player: gameState.getPlayers()) {
                quest.addQuestPlayer(player.getPlayerId() - 1, player);
            }

            int stageIndex = 1;
            for(Stage stage: quest.getStages()) {
                for(QuestPlayer questPlayer: quest.getQuestPlayers()) {
                    int playerId = questPlayer.getPlayerId();
                    // TODO:: Scaffold command
                    GameCommand questStageCommand = new GameCommand(Command.PLAYER_QUEST_TURN);
                    questStageCommand.setCard(stage.getStageCard());
                    server.notifyClient(playerId - 1, questStageCommand);

                    // Wait for player to take turn
                    while (!gameState.getGameStatus().equals(GameStatus.RUNNING_QUEST)) {
                        Thread.sleep(1000);
                    }
                }

                // Find stage winners and losers
                ArrayList<QuestPlayer> stageLosers = quest.computeStageWinners(stage);
                System.out.println("== Stage:\n\tin game -> " + quest.getQuestPlayers().size() + "\n\tlosers -> " + stageLosers.size());

                // Send notification to quest losers
                for(QuestPlayer stageLoser: stageLosers) {
                    GameCommand questStageLostCommand = new GameCommand(Command.QUEST_STAGE_LOST);
                    questStageLostCommand.setPlayerId(stageLoser.getPlayerId());
                    questStageLostCommand.setPlayer(stageLoser.getPlayer());
                    server.notifyClient(stageLoser.getPlayerId() - 1, questStageLostCommand);
                }

                // Rest cards of winners
                for(QuestPlayer stageWinner: quest.getQuestPlayers()) {
                    stageWinner.resetQuestCardsUsed();
                }

                System.out.println("== Stage " + stageIndex++ + " completed");
                Thread.sleep(2000);
            }

            shouldStopRunner();

        } catch (InterruptedException e) {
            e.printStackTrace();
            shouldStopRunner();
        }
    }
}
