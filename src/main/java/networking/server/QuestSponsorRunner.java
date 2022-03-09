package networking.server;

import model.*;

import java.util.ArrayList;

public class QuestSponsorRunner extends Runner {
    private Server server;
    private InternalGameState gameState;

    public QuestSponsorRunner(Server server, InternalGameState gameState) {
        this.server = server;
        this.gameState = gameState;
    }

    @Override
    public void loop() {
        // Ask players to sponsor quest
        System.out.println("== Quest Sponsor runner says: Looking for quest sponsor");
        gameState.setGameStatus(GameStatus.FINDING_QUEST_SPONSOR);
        server.notifyClients(new GameCommand(Command.FIND_QUEST_SPONSOR));

        try {
            ArrayList<Player> players = gameState.getPlayers();
            gameState.setGameStatus(GameStatus.RUNNING);

                // Iterate over clients and instruct them to take turns
                for (Player player : players) {
                    gameState.setGameStatus(GameStatus.PROMPTING_QUEST_SPONSOR);
                    int playerId = player.getPlayerId();
                    GameCommand playerShouldSponsorQuestCommand = new GameCommand(Command.SHOULD_SPONSOR_QUEST);
                    playerShouldSponsorQuestCommand.setPlayerId(playerId);

                    server.notifyClient(playerId, playerShouldSponsorQuestCommand);
                    System.out.println("== Quest Sponsor runner says: should sponsor command sent");
                    // Wait for player to make decision
                    while (gameState.getGameStatus().equals(GameStatus.PROMPTING_QUEST_SPONSOR)) {
                        Thread.sleep(1000);
                    }

                    // If sponsor agreed, exit
                    if(gameState.getGameStatus().equals(GameStatus.IN_QUEST)) {
                        System.out.println("== Quest Sponsor runner says: Found quest sponsor");
                        shouldStopRunner();
                        break;
                    }
                }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
