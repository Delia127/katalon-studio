package com.kms.katalon.execution.console.entity;

public abstract class LongConsoleOption extends AbstractConsoleOption<Long> {
    @Override
    public Class<Long> getArgumentType() {
        return Long.class;
    }
}
