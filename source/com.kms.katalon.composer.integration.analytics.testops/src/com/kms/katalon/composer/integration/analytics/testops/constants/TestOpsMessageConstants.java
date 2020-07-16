package com.kms.katalon.composer.integration.analytics.testops.constants;

import org.eclipse.osgi.util.NLS;

public class TestOpsMessageConstants extends NLS {
	
	private static final String BUNDLE_NAME = "com.kms.katalon.composer.integration.analytics.testops.constants.testopsMessageConstants";
	
	public static String LBL_EXECUTIONS;
	
	public static String LNK_VIEW_ALL_EXECUTIONS;
	
	public static String EXECUTION_STATUS;
	
	public static String EXECUTION_ID;
	
	public static String EXECUTION_NAME;
	
	public static String EXECUTION_DURATION;
	
	public static String MSG_ANALYTICS_CONNECTION_ERROR;
	
	public static String MSG_ERROR_TITLE;
	
	public static String LBL_LOADING;
	
	public static String LBL_EXECUTION_STATUS_PASSED;
	
	public static String LBL_EXECUTION_STATUS_FAILED;
	
	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, TestOpsMessageConstants.class);
    }

    private TestOpsMessageConstants() {
    }
}
