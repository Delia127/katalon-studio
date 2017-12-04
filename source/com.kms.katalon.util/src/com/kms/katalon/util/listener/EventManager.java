package com.kms.katalon.util.listener;

public interface EventManager<E> {
    Iterable<EventListener<E>> getListeners(E event);
    
    void addListener(EventListener<E> listener, Iterable<E> events);

    default void invoke(E event, Object object) {
        Iterable<EventListener<E>> listeners = getListeners(event);
        if (listeners == null) {
            return;
        }
        listeners.forEach(listener -> {
            listener.handleEvent(event, object);
        });
    }
}
