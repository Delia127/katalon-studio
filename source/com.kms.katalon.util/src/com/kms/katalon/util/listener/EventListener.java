package com.kms.katalon.util.listener;

public interface EventListener<E> {
    void handleEvent(E event, Object object);
}
