package networking.server;

import model.*;

public class QuestJoinRunner extends Runner {

    private Quest quest;
    private Server server;

    public QuestJoinRunner(Server server, Quest quest) {
        this.server = server;
        this.quest = quest;
    }

    @Override
    public void loop() {
        InternalGameState gameState = this.server.getGameState();
        gameState.setGameStatus(GameStatus.FINDING_QUEST_PARTICIPANTS);

        System.out.println("== Quest join runner says: Finding quest participants");

        for(Player player: gameState.getPlayers()) {
            int playerId = player.getPlayerId();

            if(playerId == quest.getSponsor().getPlayerId()) continue; // Do not prompt quest sponsor

            gameState.setGameStatus(GameStatus.PROMPTING_QUEST_PARTICIPANT);
            GameCommand playerShouldJoinQuestCommand = new GameCommand(Command.SHOULD_JOIN_QUEST);
            playerShouldJoinQuestCommand.setQuest(quest);
            playerShouldJoinQuestCommand.setCard(gameState.getCurrentStoryCard());
            playerShouldJoinQuestCommand.setPlayer(player);
            playerShouldJoinQuestCommand.setPlayerId(playerId);

            server.notifyClientByPlayerId(playerId, playerShouldJoinQuestCommand);
            System.out.println("== Quest join runner says: should join quest command sent");

            try {
                while (gameState.getGameStatus().equals(GameStatus.PROMPTING_QUEST_PARTICIPANT)) Thread.sleep(1000); // Wait for player response
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        shouldStopRunner();

        int numParticipants = quest.getQuestPlayers().size();
        if(numParticipants > 0) {  // if any participants, start quest
            System.out.println("== Quest join runner says: starting quest with " + numParticipants + " participants");
            new Thread(new QuestRunner(server, quest)).start();
        } else { // ...otherwise continue running
            System.out.println("== Quest join runner says: No participants to join quest. Exiting");
            gameState.setGameStatus(GameStatus.RUNNING);
        }
    }
}