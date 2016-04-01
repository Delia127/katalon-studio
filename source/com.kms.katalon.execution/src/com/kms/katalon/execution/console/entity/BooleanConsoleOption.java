package com.kms.katalon.execution.console.entity;

public abstract class BooleanConsoleOption extends AbstractConsoleOption<Boolean> {
    @Override
    public Class<Boolean> getArgumentType() {
        return Boolean.class;
    }
}
