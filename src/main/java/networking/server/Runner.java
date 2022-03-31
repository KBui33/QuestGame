package networking.server;

import model.InternalGameState;
import model.Player;

import java.io.IOException;
import java.util.Arrays;

import static utils.Utility.shiftLeft;

public abstract class Runner implements Runnable {
    private boolean stopRunner = false;
    protected int shouldRespond = 0;
    @Override
    public void run() {
        while (!stopRunner) loop();
    }

    protected void shouldStopRunner() {
        this.stopRunner = true;
    }

    protected abstract void loop();

    protected void waitForResponses() {};

    protected int[] computePromptOrder() throws IOException {
        InternalGameState gameState = Server.getInstance().getGameState();
        int numPlayers = gameState.getNumPlayers();
        int[] promptOrder = new int[numPlayers];
        int currentPlayerId = gameState.getCurrentTurnPlayer().getPlayerId();

        int i = 0;
        for(Player player: gameState.getPlayers()) {
            promptOrder[i++] = player.getPlayerId();
        }
        Arrays.sort(promptOrder);

        for(int j = 0; j < promptOrder.length; j++) {
            if(currentPlayerId == promptOrder[j]) {
                promptOrder = shiftLeft(promptOrder, j);
                break;
            }
        }

        return promptOrder;
    }

}
