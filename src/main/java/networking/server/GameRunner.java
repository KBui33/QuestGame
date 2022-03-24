package networking.server;

import component.card.*;
import component.card.QuestCard;
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
        server.notifyClients(new GameCommand(GameCommandName.GAME_STARTED));


        try {
            Thread.sleep(2000);

            ArrayList<Player> players = gameState.getPlayers();
            gameState.setGameStatus(GameStatus.RUNNING);

            System.out.println("== Game runner says: Starting game loop");

            while (!gameState.getGameStatus().equals(GameStatus.GAME_OVER)) {
                // Iterate over clients and instruct them to take turns
                for (Player player : players) {
                    gameState.setGameStatus(GameStatus.TAKING_TURN);

                    int playerId = player.getPlayerId();

                    GameCommand playerTurnCommand = new GameCommand(GameCommandName.PLAYER_TURN); // Broadcast take turn command
                    playerTurnCommand.setPlayerId(playerId);
                    gameState.setCurrentTurnPlayer(player);

                    Card currentStoryCard = gameState.drawStoryCard();
                    gameState.setCurrentStoryCard(currentStoryCard);

                    // Start quest sponsor thread if card is a quest card
                    if(currentStoryCard instanceof QuestCard) {
                        System.out.println("== Game runner says: Quest card played");
                        new Thread(new QuestSponsorRunner(server)).start();
                       // Start event thread if card is an event card
                    } else if (currentStoryCard instanceof EventCard){
                        System.out.println("== Game runner says: Event card played");
                        continue;
//                        new Thread(new EventRunner(server, gameState.getCurrentEvent())).start();
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
                    GameCommand endTurnCommand = new GameCommand(GameCommandName.TOOK_TURN);

                    endTurnCommand.setPlayerId(playerId);
                    server.notifyClients(endTurnCommand);

                    // Check if game over
                    if(checkGameOver()) break;
                }

                System.out.println("== All players have taken a turn");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        shouldStopRunner();
        System.out.println("== Game Runner says: GAME IS OVER");
        endGame();
    }

    public boolean checkGameOver() {
        ArrayList<Player> winners = gameState.getWinners();
        if(winners.size() <= 0) return false;

        gameState.setGameStatus(GameStatus.GAME_OVER);

        return true;
    }

    public void endGame() {
        ArrayList<Player> winners = gameState.getWinners();

        shouldRespond = gameState.getNumPlayers();
        server.resetNumResponded(CommandType.GAME);


        GameCommand gameOverCommand = new GameCommand(GameCommandName.GAME_COMPLETE);
        gameOverCommand.setPlayers(winners);
        server.notifyClients(gameOverCommand);

        waitForResponses();

        // reset game
        gameState.resetGame();
    }

    @Override
    protected void waitForResponses() {
        try {
            while (server.getNumResponded(CommandType.GAME) < shouldRespond) Thread.sleep(1000);
            server.resetNumResponded(CommandType.GAME);
            shouldRespond = 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
