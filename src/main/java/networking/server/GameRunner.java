package networking.server;

import model.GameCommand;
import model.GameState;
import model.Player;

import java.io.IOException;
import java.util.ArrayList;

public class GameRunner implements Runnable {
    private Server server;

    public GameRunner(Server server) {
        this.server = server;

        // Start game
        System.out.println("== Game runner says: Starting game");
        server.getGameState().startGame();
        server.notifyClients(new GameCommand(GameCommand.Command.GAME_STARTED));
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
            gameLoop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void gameLoop() throws IOException, InterruptedException {
        System.out.println("== Game runner says: Starting game loop");

        GameState gameState = this.server.getGameState();
        ArrayList<Player> players = gameState.getPlayers();
        gameState.setGameStatus(GameState.GameStatus.RUNNING);

        while(true) {
            // Iterate over clients and instruct them to take turns
            for (Player player: players) {
                gameState.setGameStatus(GameState.GameStatus.TAKING_TURN);
                int playerId = player.getPlayerId();
                GameCommand playerTurnCommand = new GameCommand(GameCommand.Command.PLAYER_TURN); // Broadcast take turn command
                playerTurnCommand.setPlayerId(playerId);
                server.notifyClients(playerTurnCommand);
                System.out.println("== Game runner says: take turn command sent");
                // Wait for player to play
                while(!gameState.getGameStatus().equals(GameState.GameStatus.RUNNING)) {
                    Thread.sleep(1000);
                }

                // Notify clients
                GameCommand endTurnCommand = new GameCommand(GameCommand.Command.TOOK_TURN);
                endTurnCommand.setPlayerId(playerId);

                server.notifyClients(endTurnCommand);

                Thread.sleep(2000);
            }

            System.out.println("== All players have taken a turn");
        }
    }
}
