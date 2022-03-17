package networking.server;

import model.*;

import java.util.ArrayList;

public class QuestRunner extends Runner {
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
        //gameState.setCurrentQuest(this.quest);
        gameState.setGameStatus(GameStatus.RUNNING_QUEST);
        server.notifyClients(new GameCommand(Command.QUEST_STARTED));
        System.out.println("== Quest runner says: initializing quest");

        // Setup already done in quest setup controller
        try {
            int stageIndex = 1;
            for(Stage stage: quest.getStages()) {
                System.out.println("== Quest runner says: Stage " + stageIndex + " started");

                // Deal adventure cards to participants
                System.out.println("== Quest runner says: Dealing an adventure card to each participant");
                for(QuestPlayer questPlayer: quest.getQuestPlayers()) {
                    questPlayer.addCard(gameState.drawAdventureCard());
                }

                for(QuestPlayer questPlayer: quest.getQuestPlayers()) {
                    int playerId = questPlayer.getPlayerId();
                    System.out.println("== Game runner says: Sending take quest turn command to player " + playerId);
                    gameState.setGameStatus(GameStatus.TAKING_QUEST_TURN);

                    GameCommand questStageCommand = new GameCommand(Command.PLAYER_QUEST_TURN);
                    questStageCommand.setCard(stage.getStageCard());
                    server.notifyClient(playerId - 1, questStageCommand);

                    // Wait for player to take turn
                    while (!gameState.getGameStatus().equals(GameStatus.RUNNING_QUEST)) {
                        Thread.sleep(2000);
                    }
                }

                // Find stage winners and losers
                ArrayList<QuestPlayer> stageLosers = quest.computeStageWinners(stage);
                ArrayList<QuestPlayer> stageWinners = quest.getQuestPlayers();
                System.out.println("== Stage:\tin game -> " + quest.getQuestPlayers().size() + "\tlosers -> " + stageLosers.size());

                // Send notification to quest losers
                for(QuestPlayer stageLoser: stageLosers) {
                    int playerId = stageLoser.getPlayerId();
                    System.out.println("== Game runner says: Sending end quest turn to loser " + playerId);
                    gameState.setGameStatus(GameStatus.ENDING_QUEST_TURN);

                    GameCommand questStageLostCommand = new GameCommand(Command.QUEST_STAGE_LOST);
                    questStageLostCommand.setPlayerId(playerId);
                    questStageLostCommand.setPlayer(stageLoser.getPlayer());
                    questStageLostCommand.setQuest(quest);
                    server.notifyClient(playerId - 1, questStageLostCommand);

                    // Wait for player to end turn
//                    while (!gameState.getGameStatus().equals(GameStatus.RUNNING_QUEST)) {
//                        Thread.sleep(1000);
//                    }
                }

                // Send notification to quest winners
                for(QuestPlayer stageWinner: stageWinners) {
                    int playerId = stageWinner.getPlayerId();
                    System.out.println("== Game runner says: Sending end quest turn to winner " + playerId);
                    gameState.setGameStatus(GameStatus.ENDING_QUEST_TURN);

                    GameCommand questStageWonCommand = new GameCommand(Command.QUEST_STAGE_WON);
                    questStageWonCommand.setPlayerId(playerId);
                    questStageWonCommand.setPlayer(stageWinner.getPlayer());
                    questStageWonCommand.setQuest(quest);
                    server.notifyClient(playerId - 1, questStageWonCommand);

                    // Wait for player to end turn
//                    while (!gameState.getGameStatus().equals(GameStatus.RUNNING_QUEST)) {
//                        Thread.sleep(1000);
//                    }
                }

                // If no players are left, end quest
                if(stageWinners.size() <= 0) {
                    break;
                }

                // Reset cards of winners
                for(QuestPlayer stageWinner: stageWinners) {
                    stageWinner.resetQuestCardsUsed();
                }

                // Give card(s) to the sponsor
                for(int i = 0; i < quest.distributeToSponsor(); i++) {
                    quest.getSponsor().getCards().add(
                            gameState.drawAdventureCard()
                    );
                }

                System.out.println("== Quest runner says: Stage " + stageIndex++ + " completed");
                quest.incrementStage(); // Increment stage
                Thread.sleep(2000);
            }

            shouldStopRunner();
            System.out.println("== Quest runner says: Quest completed");
            server.notifyClients(new GameCommand(Command.ENDED_QUEST));
            gameState.setGameStatus(GameStatus.RUNNING);

        } catch (InterruptedException e) {
            e.printStackTrace();
            shouldStopRunner();
        }
    }
}
