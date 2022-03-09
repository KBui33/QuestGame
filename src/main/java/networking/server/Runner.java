package networking.server;

public abstract class Runner implements Runnable {
    private boolean stopRunner = false;
    @Override
    public void run() {
        while (!stopRunner) loop();
    }

    public void shouldStopRunner() {
        this.stopRunner = true;
    }

    public abstract void loop();
}
