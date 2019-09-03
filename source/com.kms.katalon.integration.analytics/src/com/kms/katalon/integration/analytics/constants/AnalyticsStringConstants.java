package com.kms.katalon.integration.analytics.constants;

public class AnalyticsStringConstants {
    
    public static final String ANALYTICS_NAME = "Katalon Analytics";
    
    public static final String ANALYTICS_BUNDLE_ID = "com.kms.katalon.integration.analytics";
    
    public static final String ANALYTICS_SCHEME_HTTPS = "https";
    
    public static final String ANALYTICS_SERVER_URL_SEPARATOR = "@";

    public static final String ANALYTICS_API_TOKEN = "/oauth/token";

    public static final String ANALYTICS_API_PROJECTS = "/api/v1/projects";
    
    public static final String ANALYTICS_API_TEAMS = "/api/v1/teams";
    
    public static final String ANALYTICS_API_FROM_KS = "/from-ks?";

    public static final String ANALYTICS_USERS_ME = "/api/v1/users/me";
    
    public static final String ANALYTICS_API_KATALON_TEST_REPORTS = "/api/v1/katalon-test-reports";
    
    public static final String ANALYTICS_API_KATALON_MULTIPLE_TEST_REPORTS = "/api/v1/katalon/test-reports/multiple";
    
    public static final String ANALYTICS_API_KATALON_TEST_RUN_RESULT = "/api/v1/katalon/test-reports/update-result";
    
    public static final String ANALYTICS_API_UPLOAD_URL = "/api/v1/files/upload-url";

    public static final String ANALYTICS_API_UPLOAD_URLS = "/api/v1/files/upload-urls";
    
    public static final String ANALYTICS_API_UPLOAD_TEST_PROJECT = "/api/v1/test-projects/upload";
    
    public static final String ANALYTICS_API_CREATE_TEST_PLAN = "/api/v1/run-configurations";

    public static final String ANALYTICS_API_TRACKING_ACTIVITY = "/api/v1/tracking";
    
    public static final String ANALYTICS_STOREAGE = "s3";
    
    public static final String ANALYTICS_REPORT_FILE_EXTENSION_PATTERN = "([^\\s]+(\\.(?i)(log))$)";
    
    public static final String ANALYTICS_LOG_FILE_EXTENSION_PATTERN = "([^\\s]+(\\.(?i)(properties|xml|json))$)";
    
    public static final String ANALYTICS_VIDEO_FILE_EXTENSION_PATTERN = "([^\\s]+(\\.(?i)(avi|mov|srt))$)";
    
    public static final String ANALYTICS_SCREENSHOT_FILE_EXTENSION_PATTERN = "([^\\s]+(\\.(?i)(png|jpg))$)";
    
    public static final String ANALYTICS_HAR_FILE_EXTENSION_PATTERN = "([^\\s]+(\\.(?i)(har))$)";
    
    public static final String ANALYTICS_RP_FILE_EXTENSION_PATTERN = "([^\\s]+(\\.(?i)(rp))$)";
    
    public static final String ANALYTICS_BASIC_REPORT_FILE_EXTENSION_PATTERN = "([^\\s]+(\\.(?i)(pdf|html|csv))$)";
    
    public static final String ANALYTICS_UUID_FILE_EXTENSION_PATTERN = "([^\\s]+(\\.(?i)(uuid))$)";

    public static final String ANALYTICS_URL_TEST_PLAN = "/team/%d/project/%d/grid/plan/%d/job";
    
    public static final String ANALYTICS_URL_TEST_PROJECT = "/team/%d/project/%d/test-project";
    
    public static final String ANALYTICS_CLOUD_TYPE_CIRCLE_CI = "CIRCLE_CI";
    
    public static final String ANALYTICS_CONFIG_TYPE_TEST_SUITE_COLLECTION = "TSC";

    public static final String ANALYTICS_FEATURES_URL = "/api/v1/features";
}
