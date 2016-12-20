package com.kms.katalon.execution.console.entity;

public abstract class AbstractConsoleOption<T> implements ConsoleOption<T> {
    protected T value;

    @Override
    public boolean hasArgument() {
        return true;
    }

    @Override
    public String getDefaultArgumentValue() {
        return null;
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    public T getValue() {
        return value;
    }

    @Override
    public void setValue(String rawValue) {
    }
}
