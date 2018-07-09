package com.kms.katalon.composer.execution.exceptions;

import java.text.MessageFormat;

import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;

public class StepNotFoundException extends Exception {
    
    private int stepIndex;
    
    private String testCaseId;

    /**
     * 
     */
    private static final long serialVersionUID = -7493912508475757492L;
    
    public StepNotFoundException(int stepIndex, String testCaseId) {
        this.stepIndex = stepIndex;
        this.testCaseId = testCaseId;
    }
    
    @Override
    public String getMessage() {
        return MessageFormat.format(
                ComposerExecutionMessageConstants.DIA_WARN_UNABLE_NAVIGATE_TEST_CASE_STEP,
                Integer.toString(stepIndex), testCaseId);
    }
}
