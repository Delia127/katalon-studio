package com.kms.katalon.execution.console.entity;

public abstract class NoArgumentConsoleOption extends AbstractConsoleOption<Object> {
    @Override
    public boolean hasArgument() {
        return false;
    }

    @Override
    public Class<Object> getArgumentType() {
        return Object.class;
    }

}
