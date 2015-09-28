package com.kms.katalon.controller;



public interface IImportExportController {
	public String getDisplayText();
	
	public boolean execute() throws Exception;
	
	public void cancel() throws Exception;
	
	public int getProgress() throws Exception;
	
	public String getErrorMessage();
}
