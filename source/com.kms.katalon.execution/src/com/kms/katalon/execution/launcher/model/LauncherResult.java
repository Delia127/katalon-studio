package com.kms.katalon.execution.launcher.model;

import java.text.MessageFormat;

import com.kms.katalon.execution.constants.StringConstants;

public class LauncherResult {
    public static final int RETURN_CODE_PASSED = 0;
    public static final int RETURN_CODE_FAILED = 1;
    public static final int RETURN_CODE_ERROR = 2;
    public static final int RETURN_CODE_FAILED_AND_ERROR = 3;
    
	private int totalTestCases;
	private int numPasses;
	private int numFailures;
	private int numErrors;
	
	public LauncherResult(int totalTestCases) {
		this.setTotalTestCases(totalTestCases);
		setNumPasses(0);
		setNumFailures(0);
		setNumErrors(0);
	}

	public int getTotalTestCases() {
		return totalTestCases;
	}

	private void setTotalTestCases(int totalTestCases) {
		this.totalTestCases = totalTestCases;
	}

	public int getNumPasses() {
		return numPasses;
	}

	public void setNumPasses(int numPasses) {
		this.numPasses = numPasses;
	}

	public int getNumFailures() {
		return numFailures;
	}

	public void setNumFailures(int numFailures) {
		this.numFailures = numFailures;
	}

	public int getNumErrors() {
		return numErrors;
	}

	public void setNumErrors(int numErrors) {
		this.numErrors = numErrors;
	}
	
	public int getExecutedTestCases() {
		return getNumPasses() + getNumFailures() + getNumErrors();
	}
	
	public void increasePasses() {
		numPasses++;
	}
	
	public void increaseFailures() {
		numFailures++;
	}
	
	public void increaseErrors() {
		numErrors++;
	}
	
	public boolean isNotPassed() {
		return numErrors + numFailures > 0;
	}
	
	public boolean hasErrors() {
	    return numErrors > 0;
	}
	
	public boolean hasFailures() {
	    return numFailures > 0;
	}
	
	public int getReturnCode() {
	    if (!isNotPassed()) {
	        return RETURN_CODE_PASSED;
	    } else {
	        if (hasErrors() && hasFailures()) {
	            return RETURN_CODE_FAILED_AND_ERROR;
	        } else if (hasErrors()) {
	            return RETURN_CODE_ERROR;
	        } else {
	            return RETURN_CODE_FAILED;
	        }
	    }
	}
	
	@Override
	public String toString() {
		return MessageFormat.format(StringConstants.MODEL_TOTAL_PASSED_FAILED_ERRORS,
				Integer.toString(getTotalTestCases()),
				Integer.toString(getNumPasses()),
				Integer.toString(getNumFailures()),
				Integer.toString(getNumErrors()));
	}
}
