package com.kms.katalon.constants;


public interface PreferenceConstants {
	
	public interface IPluginPreferenceConstants {
		public static final String QUALIFIER = "com.kms.katalon";
	}
	
	public interface EnginePreferenceConstants extends IPluginPreferenceConstants {
		public static final String QUALIFIER = "com.kms.katalon.engine";
		
		// Remote call preferences
		public static final String REMOTE_CALL_IS_ALLOWED = "remoteCall.isAllowed";
		public static final String REMOTE_CALL_NUMBER_OF_REMOTE_CALL = "remoteCall.numberOfRemoteCall";
		
		// Host configuration preferences 
		public static final String HOST_CONFIG_HOST_NAME = "hostConfig.hostName";
		public static final String HOST_CONFIG_HOST_IP = "hostConfig.hostIp";
		public static final String HOST_CONFIG_SUPPORTED_BROWSER = "hostConfig.supportedBrowser";
	}
	
	public interface DALPreferenceConstans extends IPluginPreferenceConstants {
		public static final String QUALIFIER = "com.kms.katalon.dal";

		// Mail configuration preferences
		public static final String MAIL_CONFIG_HOST = "mailConfig.host";
		public static final String MAIL_CONFIG_PORT = "mailConfig.port";
		public static final String MAIL_CONFIG_USERNAME = "mailConfig.username";
		public static final String MAIL_CONFIG_PASSWORD = "mailConfig.password";
		public static final String MAIL_CONFIG_REPORT_RECIPIENTS = "mailConfig.reportRecipients";
		public static final String MAIL_CONFIG_SIGNATURE = "mailConfig.signature";
		public static final String MAIL_ATTACHMENT = "mailConfig.attachment";
	}
	
	public interface ProjectPreferenceConstans extends IPluginPreferenceConstants {
		public static final String QUALIFIER = "com.kms.katalon.composer.project";

		// Recent projects preferences
		public static final String RECENT_PROJECTS = "project.recentProjects";
	}
	
	public interface ExecutionPreferenceConstans extends IPluginPreferenceConstants {
        public static final String QUALIFIER = "com.kms.katalon.composer.execution";

        //Setting
        public static final String EXECUTION_DEFAULT_CONFIGURATION = "execution.defaultExecutionConfiguration";
        public static final String EXECUTION_DEFAULT_TIMEOUT = "execution.defaultTimeout";
        public static final String EXECUTION_NOTIFY_AFTER_EXECUTING = "execution.notifyAfterExecuting";
        public static final String EXECUTION_OPEN_REPORT_AFTER_EXECUTING = "execution.openReportAfterExecuting";
        
        public static final String EXECUTION_SHOW_ALL_LOGS = "log.showAll";
        public static final String EXECUTION_SHOW_INFO_LOGS = "log.showInfo";
        public static final String EXECUTION_SHOW_PASSED_LOGS = "log.showPasses";
        public static final String EXECUTION_SHOW_FAILED_LOGS = "log.showFailures";
        public static final String EXECUTION_SHOW_ERROR_LOGS = "log.showErrors";        
        public static final String EXECUTION_SHOW_LOGS_AS_TREE = "log.treeView";
        public static final String EXECUTION_PIN_LOG = "log.pinView";
    }
	
	public interface WebUiPreferenceConstants extends IPluginPreferenceConstants {
		public static final String QUALIFIER = "com.kms.katalon.composer.webui";
        public static final String EXECUTION_WAIT_FOR_IE_HANGING = "execution.waitForIEHanging";
	}
	
	public interface TestCasePreferenceConstants extends IPluginPreferenceConstants {
	    public static final String QUALIFIER = "com.kms.katalon.composer.testcase";
	    
	    public static final String TESTCASE_DEFAULT_VARIABLE_TYPE = "default.variableType";
	    public static final String TESTCASE_GENERATE_DEFAULT_VARIABLE_VALUE = "auto.generateDefaultVariableType";
	    public static final String TESTCASE_AUTO_EXPORT_VARIABLE = "auto.exportVariable";
	    public static final String TESTCASE_DEFAULT_KEYWORD_TYPE = "default.keywordType";
	    public static final String TESTCASE_DEFAULT_KEYWORD_NAME = "default.keywordName";
	    public static final String TESTCASE_DEFAULT_FAILURE_HANDLING = "default.failureHandling";
	}
	
	public interface IntegrationSlackPreferenceConstants extends IPluginPreferenceConstants {
		public static final String QUALIFIER = "com.kms.katalon.composer.integration.slack";
		
		// Team Collaboration (Slack) preferences
		public static final String SLACK_ENABLED = "slackConfig.enabled";
		public static final String SLACK_AUTH_TOKEN = "slackConfig.token";
		public static final String SLACK_CHANNEL_GROUP = "slackConfig.channel";
		public static final String SLACK_USERNAME = "slackConfig.username";
		public static final String SLACK_AS_USER = "slackConfig.asUser";

		public static final String SLACK_SEND_OPEN_PROJECT = "slackConfig.sendOpenProject";
		public static final String SLACK_SEND_CLOSE_PROJECT = "slackConfig.sendCloseProject";
		public static final String SLACK_SEND_UPDATE_TEST_CASE = "slackConfig.sendUpdateTestCase";
		public static final String SLACK_SEND_UPDATE_TEST_SUITE = "slackConfig.sendUpdateTestSuite";
		public static final String SLACK_SEND_UPDATE_TEST_DATA = "slackConfig.sendUpdateTestData";
		public static final String SLACK_SEND_UPDATE_TEST_OBJECT = "slackConfig.sendUpdateTestObject";
		public static final String SLACK_SEND_RENAME_ITEM = "slackConfig.sendRenameItem";
		public static final String SLACK_SEND_PASTE_FROM_COPY = "slackConfig.sendPasteFromCopy";
		public static final String SLACK_SEND_PASTE_FROM_CUT = "slackConfig.sendPasteFromCut";
		public static final String SLACK_SEND_DELETE_ITEM = "slackConfig.sendDeleteItem";
		public static final String SLACK_SEND_CREATE_TEST_CASE = "slackConfig.sendCreateTestCase";
		public static final String SLACK_SEND_CREATE_TEST_SUITE = "slackConfig.sendCreateTestSuite";
		public static final String SLACK_SEND_CREATE_TEST_DATA = "slackConfig.sendCreateTestData";
		public static final String SLACK_SEND_CREATE_TEST_OBJECT = "slackConfig.sendCreateTestObject";
		public static final String SLACK_SEND_CREATE_KEYWORD = "slackConfig.sendCreateKeyword";
		public static final String SLACK_SEND_CREATE_FOLDER = "slackConfig.sendCreateFolder";
		public static final String SLACK_SEND_CREATE_PACKAGE = "slackConfig.sendCreatePackage";
	}
	
}
