package networking.server;

import game.components.card.Card;
import game.components.card.QuestCard;
import model.*;

import java.util.ArrayList;

public class GameRunner extends Runner {
    private Server server;
    private InternalGameState gameState;

    public GameRunner(Server server, InternalGameState gameState) {
        this.server = server;
        this.gameState = gameState;
    }

    public void loop() {
        // Start game
        System.out.println("== Game runner says: Starting game");
        gameState.startGame();
        server.notifyClients(new GameCommand(Command.GAME_STARTED));


        try {
            Thread.sleep(2000);

            ArrayList<Player> players = gameState.getPlayers();
            gameState.setGameStatus(GameStatus.RUNNING);

            System.out.println("== Game runner says: Starting game loop");

            while (true) {
                // Iterate over clients and instruct them to take turns
                for (Player player : players) {
                    gameState.setGameStatus(GameStatus.TAKING_TURN);

                    int playerId = player.getPlayerId();

                    GameCommand playerTurnCommand = new GameCommand(Command.PLAYER_TURN); // Broadcast take turn command
                    playerTurnCommand.setPlayerId(playerId);
                    gameState.setCurrentTurnPlayer(player);

                    Card currentStoryCard = gameState.drawStoryCard();
                    gameState.setCurrentStoryCard(currentStoryCard);

                    // Start quest sponsor thread if card is a quest card
                    if (currentStoryCard instanceof QuestCard) {
                        new Thread(new QuestSponsorRunner(server)).start();
                    } else {
                        playerTurnCommand.setCard(currentStoryCard); // Deal story card to current player
                        server.notifyClients(playerTurnCommand);
                        System.out.println("== Game runner says: take turn command sent");
                    }

                    // Wait for player to play
                    while (!gameState.getGameStatus().equals(GameStatus.RUNNING)) {
                        Thread.sleep(1000);
                    }

                    // Discard story card
                    System.out.println("== Game runner says: Discarding story card");
                    gameState.discardStoryCard(currentStoryCard);
                    gameState.setCurrentQuest(null);
                    gameState.setCurrentStoryCard(null);

                    // Notify clients
                    GameCommand endTurnCommand = new GameCommand(Command.TOOK_TURN);

                    endTurnCommand.setPlayerId(playerId);
                    server.notifyClients(endTurnCommand);
                }

                System.out.println("== All players have taken a turn");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.shouldStopRunner();
    }

    @Override
    protected void waitForResponses() {

    }
}
