package com.kms.katalon.composer.components.impl.listener;

public interface EventListener<E> {
    void handleEvent(E event, Object object);
}
