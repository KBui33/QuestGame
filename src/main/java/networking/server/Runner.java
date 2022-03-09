package networking.server;

public abstract class Runner implements Runnable {
    @Override
    public void run() {
        loop();
    }

    public abstract void loop();
}
