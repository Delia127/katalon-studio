package com.kms.katalon.execution.console.entity;

import java.util.List;

public interface ConsoleOptionContributor {
    /**
     * Get the console option list for this contributor
     * 
     * @return the console options list for this contributor
     */
    public List<ConsoleOption<?>> getConsoleOptionList();

    /**
     * Set the argument value from user input into the console option
     * 
     * @param argumentValue
     * @throws Exception
     */
    void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception;
}
