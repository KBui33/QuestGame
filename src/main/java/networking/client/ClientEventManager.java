package networking.client;

import java.util.*;

public class ClientEventManager {
    private Map<Client.ClientEvent, List<ClientEventListener>> listeners = new HashMap<>();

    public ClientEventManager(Client.ClientEvent[] eventTypes) {
        for (Client.ClientEvent eventType: eventTypes) {
            this.listeners.put(eventType, new ArrayList<>());
        }
    }

    public void subscribe(Client.ClientEvent eventType, ClientEventListener listener) {
        List<ClientEventListener> eventSubscribers = listeners.get(eventType);
        eventSubscribers.add(listener);
    }

    public void unsubscribe(Client.ClientEvent eventType, ClientEventListener listener) {
        List<ClientEventListener> eventSubscribers = listeners.get(eventType);
        eventSubscribers.remove(listener);
    }

    public void notify(Client.ClientEvent eventType, Object o) {
        List<ClientEventListener> eventSubscribers = listeners.get(eventType);
        for(ClientEventListener listener: eventSubscribers) {
            listener.update(eventType, o);
        }
    }
}
