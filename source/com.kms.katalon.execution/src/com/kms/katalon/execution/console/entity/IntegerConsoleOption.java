package com.kms.katalon.execution.console.entity;

public abstract class IntegerConsoleOption extends AbstractConsoleOption<Integer> {
    @Override
    public Class<Integer> getArgumentType() {
        return Integer.class;
    }
}
