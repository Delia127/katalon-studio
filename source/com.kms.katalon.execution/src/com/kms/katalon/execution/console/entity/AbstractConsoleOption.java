package com.kms.katalon.execution.console.entity;

public abstract class AbstractConsoleOption<T> implements ConsoleOption<T> {
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

}
