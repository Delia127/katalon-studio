package com.kms.katalon.integration.qtest.entity;

public class QTestExecutionStatus extends QTestEntity {

	private boolean isDefault;
	
	public QTestExecutionStatus(long id, String name) {
		super(id, name);
	}
	
	public QTestExecutionStatus() {}
	
	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	public static String getMappedValue(String katalonStatus){
		if(katalonStatus.equalsIgnoreCase("PASSED") || 
				katalonStatus.equalsIgnoreCase("WARNING")){
			return "Passed";
		}
		else if(katalonStatus.equalsIgnoreCase("FAILED")){
			return "Failed";
		}
		else if(katalonStatus.equalsIgnoreCase("INCOMPLETED") || 
				katalonStatus.equalsIgnoreCase("SKIPPED") || 
				katalonStatus.equalsIgnoreCase("ERROR")) {
			return "Incomplete";
		}
		else{
			return "Unexecuted";
		}
	} 
}