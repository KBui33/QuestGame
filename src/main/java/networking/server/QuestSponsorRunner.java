package networking.server;

import model.*;

import java.util.ArrayList;

public class QuestSponsorRunner extends Runner {
    private Server server;
    private InternalGameState gameState;

    public QuestSponsorRunner(Server server) {
        this.server = server;
        this.gameState = server.getGameState();
    }

    @Override
    public void loop() {
        // Ask players to sponsor quest
        System.out.println("== Quest Sponsor runner says: Looking for quest sponsor");
        gameState.setGameStatus(GameStatus.FINDING_QUEST_SPONSOR);
        server.notifyClients(new GameCommand(Command.FIND_QUEST_SPONSOR));
        boolean foundSponsor = false;
        try {
            ArrayList<Player> players = gameState.getPlayers();
            //gameState.setGameStatus(GameStatus.RUNNING);

            // Determine prompt order
            int[] promptOrder = computePromptOrder();

            // Iterate over clients to find sponsor
            for(int playerId: promptOrder) {
                gameState.setGameStatus(GameStatus.PROMPTING_QUEST_SPONSOR);
                GameCommand playerShouldSponsorQuestCommand = new GameCommand(Command.SHOULD_SPONSOR_QUEST);
                playerShouldSponsorQuestCommand.setCard(gameState.getCurrentStoryCard());
                playerShouldSponsorQuestCommand.setPlayerId(playerId);

                server.notifyClientByPlayerId(playerId, playerShouldSponsorQuestCommand);
                System.out.println("== Quest Sponsor runner says: should sponsor command sent");
                // Wait for player to make decision
                while (gameState.getGameStatus().equals(GameStatus.PROMPTING_QUEST_SPONSOR)) {
                    Thread.sleep(1000);
                }

                // If sponsor agreed, exit
                if(gameState.getGameStatus().equals(GameStatus.FINDING_QUEST_PARTICIPANTS)) {
                    System.out.println("== Quest Sponsor runner says: Found quest sponsor");
                    foundSponsor = true;
                    break;
                }
            }

            shouldStopRunner();
            if(foundSponsor) { // If sponsor found, start quest join runner to get participants
                System.out.println("== Quest sponsor runner says: starting quest join runner to prompt participants");
                new Thread(new QuestJoinRunner(server, gameState.getCurrentQuest())).start();
            } else {
                System.out.println("== Quest Sponsor runner says: No sponsor found. Exiting...");
                gameState.setGameStatus(GameStatus.RUNNING);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int[] computePromptOrder() {
        int numPlayers = gameState.getNumPlayers();
        int[] promptOrder = new int[numPlayers];
        int currentPlayerId = gameState.getCurrentTurnPlayer().getPlayerId();

        for (int i = 0; i < numPlayers; i++) {
            if (currentPlayerId > numPlayers) currentPlayerId = 1;
            promptOrder[i] = currentPlayerId++;
        }

        return promptOrder;
    }
}
