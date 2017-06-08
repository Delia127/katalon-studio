package com.kms.katalon.selenium.driver;

public interface IDelayableDriver {
    /**
     * Delay the driver
     */
    default void delay() {
        final int delayInMili = getActionDelay();
        if (delayInMili > 0) {
            try {
                Thread.sleep(delayInMili);
            } catch (InterruptedException e) {
                // Ignore this
            }
        }
    }

    /**
     * Get the action delay for this driver (in miliseconds)
     * @return the action delay for this driver (in miliseconds)
     */
    int getActionDelay();
}
