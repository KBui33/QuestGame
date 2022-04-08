package networking.server;

import model.InternalGameState;
import model.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static utils.Utility.shiftLeft;

public abstract class Runner implements Runnable {
    private boolean stopRunner = false;
    protected int shouldRespond = 0;
    protected final HashSet<Integer> shouldRespondIds = new HashSet<>();
    @Override
    public void run() {
        while (!stopRunner) {
            try {
                loop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void shouldStopRunner() {
        this.stopRunner = true;
    }

    protected abstract void loop() throws InterruptedException, IOException;

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

    protected  void initShouldRespondIds(Object o) {
        shouldRespondIds.clear();
    }

}
