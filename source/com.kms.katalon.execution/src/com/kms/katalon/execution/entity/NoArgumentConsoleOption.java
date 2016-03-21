package com.kms.katalon.execution.entity;

public abstract class NoArgumentConsoleOption extends AbstractConsoleOption<Object> {
    @Override
    public boolean hasArgument() {
        return false;
    }

    @Override
    public void setArgumentValue(String argumentValue) {
        // Do nothing because of no argument
    }

    @Override
    public Class<Object> getArgumentType() {
        return Object.class;
    }

}
