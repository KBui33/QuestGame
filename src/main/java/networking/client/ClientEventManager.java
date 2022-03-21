package networking.client;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class ClientEventManager {
    private final Object MONITOR = new Object(); // Monitor object for synchronization
    private Map<Client.ClientEvent, List<ClientEventListener>> listeners = Collections.synchronizedMap(new HashMap<>());

    public ClientEventManager(Client.ClientEvent[] eventTypes) {
        for (Client.ClientEvent eventType: eventTypes) {
            this.listeners.put(eventType, new ArrayList<>());
        }
    }

    public Callable<Void> subscribe(Client.ClientEvent eventType, ClientEventListener listener) {
        synchronized (MONITOR) {
            List<ClientEventListener> eventSubscribers = listeners.get(eventType);
            eventSubscribers.add(listener);
        }

        return () -> {
            unsubscribe(eventType, listener);
            return null;
        };
    }



    public void unsubscribe(Client.ClientEvent eventType, ClientEventListener listener) {
        synchronized (MONITOR) {
            List<ClientEventListener> eventSubscribers = listeners.get(eventType);
            eventSubscribers.remove(listener);
        }
    }

    public void notify(Client.ClientEvent eventType, Object o) {
        List<ClientEventListener> eventSubscribers;
        synchronized (MONITOR) {
            eventSubscribers = new ArrayList<>(listeners.get(eventType));
        }

        for (ClientEventListener listener : eventSubscribers) {
            listener.update(eventType, o);
        }
    }
}
