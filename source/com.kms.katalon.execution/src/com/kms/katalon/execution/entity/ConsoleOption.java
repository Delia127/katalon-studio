package com.kms.katalon.execution.entity;


public interface ConsoleOption<T> {
    /**
     * Get the type class of the console option argument for type check
     * @return the type class of the console option argument
     */
    Class<T> getArgumentType();
    
    /**
     * Return true if the console option has argument, false if not
     * @return true if the console option has argument, false if not
     */
    boolean hasArgument();
    
    /**
     * Get the name represent the console option
     * @return the name represent the console option
     */
    String getOption();
    
    /**
     * Set that the console option is available from user input
     */
    void setEnable();
    
    /**
     * Check if the console option is enabled or not
     * @return true if the console option is enable; otherwise false
     */
    public boolean isEnable();
    
    /**
     * Set the argument value from user input into the console option
     * @param argumentValue
     */
    void setArgumentValue(String argumentValue);
}
