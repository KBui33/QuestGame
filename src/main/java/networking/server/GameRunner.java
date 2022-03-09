package networking.server;

import model.*;

import java.io.IOException;
import java.util.ArrayList;

public class GameRunner extends Runner {
    private Server server;
    private InternalGameState gameState;

    public GameRunner(Server server, InternalGameState gameState) {
        this.server = server;
        this.gameState = gameState;

        // Start game
        System.out.println("== Game runner says: Starting game");
        this.gameState.startGame();
        this.server.notifyClients(new GameCommand(Command.GAME_STARTED));
    }

    public void loop() {
        System.out.println("== Game runner says: Starting game loop");

        try {
        Thread.sleep(5000);

        ArrayList<Player> players = gameState.getPlayers();
            gameState.setGameStatus(GameStatus.RUNNING);


            while (true) {
                // Iterate over clients and instruct them to take turns
                for (Player player : players) {
                    gameState.setGameStatus(GameStatus.TAKING_TURN);
                    int playerId = player.getPlayerId();
                    GameCommand playerTurnCommand = new GameCommand(Command.PLAYER_TURN); // Broadcast take turn command
                    playerTurnCommand.setPlayerId(playerId);
                    server.notifyClients(playerTurnCommand);
                    System.out.println("== Game runner says: take turn command sent");
                    // Wait for player to play
                    while (!gameState.getGameStatus().equals(GameStatus.RUNNING)) {
                        Thread.sleep(1000);
                    }

                    // Notify clients
                    GameCommand endTurnCommand = new GameCommand(Command.TOOK_TURN);
                    endTurnCommand.setPlayerId(playerId);

                    server.notifyClients(endTurnCommand);

                    Thread.sleep(2000);
                }

                System.out.println("== All players have taken a turn");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
