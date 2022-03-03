package networking.client;

import networking.client.Client;

public interface ClientEventListener {
    void update(Client.ClientEvent eventType, Object o);
}
