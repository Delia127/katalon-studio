package com.kms.katalon.execution.entity;

public abstract class LongConsoleOption extends AbstractConsoleOption<Long> {
    @Override
    public Class<Long> getArgumentType() {
        return Long.class;
    }

    @Override
    public boolean hasArgument() {
        return true;
    }
}
