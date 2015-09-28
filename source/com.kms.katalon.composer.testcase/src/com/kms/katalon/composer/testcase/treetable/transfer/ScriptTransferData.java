package com.kms.katalon.composer.testcase.treetable.transfer;

import java.io.Serializable;

public class ScriptTransferData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScriptTransferData(String scriptSnippet, String testCaseId) {
		this.scriptSnippet = scriptSnippet;
		this.testCaseId = testCaseId;
	}
	
	private String testCaseId;
	private String scriptSnippet;
	
	public String getTestCaseId() {
		return testCaseId;
	}
	
	public String getScriptSnippet() {
		return scriptSnippet;
	}
}
