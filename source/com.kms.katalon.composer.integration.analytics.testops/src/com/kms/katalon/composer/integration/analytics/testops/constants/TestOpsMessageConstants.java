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
	
	public static String LNK_EXECUTION_EMPTY;
	
	public static String LNK_ENABLE_INTEGRATION_GUIDE;

    public static String LBL_RELEASES;

    public static String LNK_VIEW_ALL_RELEASES;

    public static String RELEASE_STATUS;

    public static String RELEASE_NAME;

    public static String RELEASE_START_DATE;

    public static String RELEASE_END_DATE;

    public static String RELEASE_STATUS_ACTIVE;

    public static String RELEASE_STATUS_CLOSED;
	
    public static String LBL_PLANS;
    
    public static String LNK_VIEW_ALL_PLANS;
    
    public static String PLAN_STATUS;
    
    public static String PLAN_NAME;
    
    public static String PLAN_TEST_PROJECT;
    
    public static String PLAN_AGENTS;
    
    public static String PLAN_LAST_EXECUTION;
    
    public static String PLAN_LAST_RUN;
    
    public static String PLAN_NEXT_RUN;
    
    public static String TIME_FORMAT_AN_HOUR;
    
    public static String TIME_FORMAT_HOURS;
    
    public static String TIME_FORMAT_HOUR;
    
    public static String TIME_FORMAT_A_MINUTE;
    
    public static String TIME_FORMAT_MINUTES;
    
    public static String TIME_FORMAT_MINUTE;
    
    public static String TIME_FORMAT_SUFFIX;
    
    public static String TIME_FORMAT_PREFIX;
    
    public static String TIME_FORMAT_AFEW_SECONDS;
    
    public static String LBL_PLAN_STATUS_QUEUED;
    
    public static String LBL_PLAN_STATUS_ERROR;
    
    public static String LBL_PLAN_STATUS_CANCELED;
    
    public static String LBL_PLAN_STATUS_RUNNING;
    
    public static String LBL_PLAN_STATUS_WAITTING;
    
    public static String LNK_PLAN_EMPTY;

    public static String LNK_CREATE_PLAN_GUIDE;
    
	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, TestOpsMessageConstants.class);
    }

    private TestOpsMessageConstants() {
    }
}
