package es.uib.lotery.event;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EventHandler<E> {
    private final Set<Consumer<E>> listeners = new HashSet<>();

    public void register(Consumer<E> listener) {
        listeners.add(listener);
    }
    public void handleEvent(E context) {
        synchronized (listeners) {
            for (Consumer<E> listener : listeners) listener.accept(context);
        }
    }
}