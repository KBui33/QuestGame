package networking.server;

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
}
