package com.kms.katalon.core.logging.model;


public class TestStatus {
	
	public enum TestStatusValue {
		//PASSED, FAILED, NOT_RUN, ERROR, INCOMPLETE;	//Suite & Test status
		
		/**
		 * In HTML report support 4 statuses: PASSED, FAILED, NOT_RUN, ERROR. INCOMPLETE and NO_RUN in HTML report should be the same meaning
		 */
		PASSED, FAILED, INCOMPLETE, ERROR;	//Suite & Test status
		
//		public static int indexOf(TestStatusValue testStatusValue) {
//			if (testStatusValue != null) {
//				TestStatusValue[] allValues = TestStatusValue.values();
//				for (int i = 0; i < allValues.length ; i++) {
//					if (testStatusValue == allValues[i]) {
//						return i;
//					}
//				}
//			}
//			return -1;
//		}
	}
	// Error Java stack Trace
	protected String stackTrace = ""; 	

	//Default is NOT_RUN
	protected TestStatusValue statusValue = TestStatusValue.INCOMPLETE;

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public TestStatusValue getStatusValue() {
		return statusValue;
	}

	public void setStatusValue(TestStatusValue statusValue) {
		this.statusValue = statusValue;
	}
}
