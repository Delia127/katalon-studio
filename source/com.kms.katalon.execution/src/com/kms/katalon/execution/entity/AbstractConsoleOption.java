package com.kms.katalon.execution.entity;

public abstract class AbstractConsoleOption<T> implements ConsoleOption<T> {
    boolean isEnable = false;
    
    @Override
    public void setEnable() {
        this.isEnable = true;
    }
    
    @Override
    public boolean isEnable() {
        return isEnable;
    }
}
