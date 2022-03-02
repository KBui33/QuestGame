package networking;

public interface ClientEventListener {
    void update(Client.ClientEvent eventType, Object o);
}
