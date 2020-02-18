package com.kms.katalon.execution.setting;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.constants.CoreConstants;
import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.execution.constants.ExecutionDefaultSettingConstants;

public class ExecutionDefaultSettingStore extends BundleSettingStore {

    public static final boolean EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_SUITE = true;

    public static final boolean EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_CASE = false;

    public static final boolean EXECUTION_DEFAULT_OPEN_REPORT_REPORT_VALUE = false;

    public static final int EXECUTION_DEFAULT_TIMEOUT_VALUE = 30;

    public static final String EXECUTION_DEFAULT_CONFIGURATION_FOR_GENERIC_PROJECT = "Firefox";
    
    public static final String EXECUTION_DEFAULT_CONFIGURATION_FOR_WEBSERVICE_PROJECT = "Web Service";
    
    public static final Boolean DEFAULT_AUTO_APPLY_NEIGHBOR_XPATHS_ENABLED = false;
    
    public static final Boolean DEFAULT_SMART_WAIT_MODE = true;
    
    public static final Boolean DEFAULT_LOG_TEST_STEPS = CoreConstants.DEFAULT_LOG_TEST_STEPS;

    public static ExecutionDefaultSettingStore getStore() {
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        if (projectEntity == null) {
            return null;
        }
        return new ExecutionDefaultSettingStore(projectEntity);
    }

    public ExecutionDefaultSettingStore(ProjectEntity projectEntity) {
        super(projectEntity.getFolderLocation(),
                "com.kms.katalon.execution.setting", false);
    }

    public String getExecutionConfiguration() {
        String executionDefaultConfiguration = getDefaultExecutionConfiguration();
        try {
            return getString(ExecutionDefaultSettingConstants.EXECUTION_DEFAULT_CONFIGURATION,
                    executionDefaultConfiguration);
        } catch (IOException e) {
            return executionDefaultConfiguration;
        }
    }
    
    public String getDefaultExecutionConfiguration() {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        String executionDefaultConfiguration = project.getType() == ProjectType.WEBSERVICE ?
                EXECUTION_DEFAULT_CONFIGURATION_FOR_WEBSERVICE_PROJECT :
                    EXECUTION_DEFAULT_CONFIGURATION_FOR_GENERIC_PROJECT;
        return executionDefaultConfiguration;
    }

    public void setExecutionConfiguration(String config) throws IOException {
        setProperty(ExecutionDefaultSettingConstants.EXECUTION_DEFAULT_CONFIGURATION, config);
    }

    public int getElementTimeout() {
        try {
            return getInt(ExecutionDefaultSettingConstants.EXECUTION_DEFAULT_TIMEOUT, EXECUTION_DEFAULT_TIMEOUT_VALUE);
        } catch (IOException e) {
            return EXECUTION_DEFAULT_TIMEOUT_VALUE;
        }
    }

    public void setElementTimeout(int timeout) throws IOException {
        setProperty(ExecutionDefaultSettingConstants.EXECUTION_DEFAULT_TIMEOUT, timeout);
    }

    public void setDefaultElementTimeout() throws IOException {
        setProperty(ExecutionDefaultSettingConstants.EXECUTION_DEFAULT_TIMEOUT, EXECUTION_DEFAULT_TIMEOUT_VALUE);
    }

    public boolean isPostExecOpenReport() {
        try {
            return getBoolean(ExecutionDefaultSettingConstants.EXECUTION_OPEN_REPORT_AFTER_EXECUTING,
                    EXECUTION_DEFAULT_OPEN_REPORT_REPORT_VALUE);
        } catch (IOException e) {
            return EXECUTION_DEFAULT_OPEN_REPORT_REPORT_VALUE;
        }
    }

    public void setPostExecOpenReport(boolean isOpen) throws IOException {
        setProperty(ExecutionDefaultSettingConstants.EXECUTION_OPEN_REPORT_AFTER_EXECUTING, isOpen);
    }

    public void setDefaultPostExecOpenReport() throws IOException {
        setProperty(ExecutionDefaultSettingConstants.EXECUTION_OPEN_REPORT_AFTER_EXECUTING,
                EXECUTION_DEFAULT_OPEN_REPORT_REPORT_VALUE);
    }

    public boolean isPostTestCaseExecQuitDriver() {
        try {
            return getBoolean(ExecutionDefaultSettingConstants.EXECUTION_QUIT_DRIVERS_AFTER_EXECUTING_TEST_CASE,
                    EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_CASE);
        } catch (IOException e) {
            return EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_CASE;
        }
    }

    public void setPostTestCaseExecQuitDriver(boolean flag) throws IOException {
        setProperty(ExecutionDefaultSettingConstants.EXECUTION_QUIT_DRIVERS_AFTER_EXECUTING_TEST_CASE, flag);
    }

    public void setDefaultPostTestCaseExecQuitDriver() throws IOException {
        setProperty(ExecutionDefaultSettingConstants.EXECUTION_QUIT_DRIVERS_AFTER_EXECUTING_TEST_CASE,
                EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_CASE);
    }

    public boolean isPostTestSuiteExecQuitDriver() {
        try {
            return getBoolean(ExecutionDefaultSettingConstants.EXECUTION_QUIT_DRIVERS_AFTER_EXECUTING_TEST_SUITE,
                    EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_SUITE);
        } catch (IOException e) {
            return EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_SUITE;
        }
    }

    public void setPostTestSuiteExecQuitDriver(boolean flag) throws IOException {
        setProperty(ExecutionDefaultSettingConstants.EXECUTION_QUIT_DRIVERS_AFTER_EXECUTING_TEST_SUITE, flag);
    }

    public void setDefaultPostTestSuiteExecQuitDriver() throws IOException {
        setProperty(ExecutionDefaultSettingConstants.EXECUTION_QUIT_DRIVERS_AFTER_EXECUTING_TEST_SUITE,
                EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_SUITE);
    }
    

    public void setDefaultApplyNeighborXpathsEnabled() throws IOException {
    	setProperty(ExecutionDefaultSettingConstants.WEB_UI_DEFAULT_AUTO_APPLY_NEIGHBOR_XPATHS_ENABLED, 
    			DEFAULT_AUTO_APPLY_NEIGHBOR_XPATHS_ENABLED);
    }
    
    public void setApplyNeighborXpathsEnabled(Boolean value) throws IOException {
    	setProperty(ExecutionDefaultSettingConstants.WEB_UI_DEFAULT_AUTO_APPLY_NEIGHBOR_XPATHS_ENABLED,
    			value);
    }
    
    public Boolean isAutoApplyNeighborXpathsEnabled() {
    	try{
    	return getBoolean(ExecutionDefaultSettingConstants.WEB_UI_DEFAULT_AUTO_APPLY_NEIGHBOR_XPATHS_ENABLED, 
        					DEFAULT_AUTO_APPLY_NEIGHBOR_XPATHS_ENABLED);
    	} catch ( IOException e){
    		return DEFAULT_AUTO_APPLY_NEIGHBOR_XPATHS_ENABLED;
    	}
    }
    
    public Boolean getDefaultAutoApplyNeighborXpathsEnabled() {
    	return DEFAULT_AUTO_APPLY_NEIGHBOR_XPATHS_ENABLED;
    }
    
    public Boolean getAutoApplyNeighborXpathsEnabled(){
        try{
            return getBoolean(ExecutionDefaultSettingConstants.WEB_UI_DEFAULT_AUTO_APPLY_NEIGHBOR_XPATHS_ENABLED, 
                            DEFAULT_AUTO_APPLY_NEIGHBOR_XPATHS_ENABLED);
        } catch ( IOException e){
            return DEFAULT_AUTO_APPLY_NEIGHBOR_XPATHS_ENABLED;
        }
    }

    public Boolean getDefaultSmartWaitMode() {
        try {
            return getBoolean(ExecutionDefaultSettingConstants.WEB_UI_DEFAULT_SMART_WAIT_MODE, DEFAULT_SMART_WAIT_MODE);
        } catch (IOException e) {
            return DEFAULT_SMART_WAIT_MODE;
        }
    }
    
    public void setDefaultSmartWaitMode(Boolean value) throws IOException {
        setProperty(ExecutionDefaultSettingConstants.WEB_UI_DEFAULT_SMART_WAIT_MODE,
                value);
    }
    
    public Boolean getLogTestSteps() {
        try {
            return getBoolean(ExecutionDefaultSettingConstants.WEB_UI_LOG_TEST_STEPS, DEFAULT_LOG_TEST_STEPS);
        } catch (IOException e) {
            return DEFAULT_LOG_TEST_STEPS;
        }
    }
    
    public void setLogTestSteps(Boolean value) throws IOException {
        setProperty(ExecutionDefaultSettingConstants.WEB_UI_LOG_TEST_STEPS, value);
    }
    
    public String getVmArgs() {
        try {
            return getString(ExecutionDefaultSettingConstants.LAUNCH_VM_ARGS, "");
        } catch (IOException e) {
            return "";
        }
    }
    
    public void setVmArgs(String vmArgs) throws IOException {
        setProperty(ExecutionDefaultSettingConstants.LAUNCH_VM_ARGS, vmArgs);
    }
}
