package com.kms.katalon.execution.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.impl.DefaultExecutionSetting;
import com.kms.katalon.execution.configuration.impl.LocalHostConfiguration;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.IExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.generator.TestCaseScriptGenerator;
import com.kms.katalon.execution.generator.TestSuiteScriptGenerator;
import com.kms.katalon.execution.session.ExecutionSessionSocketServer;
import com.kms.katalon.execution.util.ExecutionUtil;

public abstract class AbstractRunConfiguration implements IRunConfiguration {

    protected IHostConfiguration hostConfiguration;

    protected DefaultExecutionSetting executionSetting;

    public AbstractRunConfiguration() {
        initExecutionSetting();
    }

    @Override
    public final IExecutionSetting build(FileEntity fileEntity, IExecutedEntity executedEntity)
            throws IOException, ExecutionException {
        init(fileEntity);

        executionSetting.setExecutedEntity(executedEntity);

        hostConfiguration = new LocalHostConfiguration();

        generateLogFolder(fileEntity);

        generateExecutionProperties();

        File scriptFile = generateTempScriptFile(fileEntity);

        executionSetting.setScriptFile(scriptFile);

        return executionSetting;
    }

    protected File generateTempScriptFile(FileEntity fileEntity) throws ExecutionException {
        try {
            if (fileEntity instanceof TestSuiteEntity) {
                return new TestSuiteScriptGenerator((TestSuiteEntity) fileEntity, this,
                        (TestSuiteExecutedEntity) this.getExecutionSetting().getExecutedEntity()).generateScriptFile();
            } else {
                return new TestCaseScriptGenerator((TestCaseEntity) fileEntity, this).generateScriptFile();
            }
        } catch (Exception ex) {
            throw new ExecutionException(ex.getMessage());
        }
    }

    protected void init(FileEntity fileEntity) throws IOException {
        if (fileEntity == null) {
            return;
        }

        int timeOut = (fileEntity instanceof TestSuiteEntity
                && !((TestSuiteEntity) fileEntity).isPageLoadTimeoutDefault())
                        ? ((TestSuiteEntity) fileEntity).getPageLoadTimeout()
                        : ExecutionUtil.getDefaultImplicitTimeout();
        executionSetting.setTimeout(timeOut);
    }

    protected void initExecutionSetting() {
        executionSetting = new DefaultExecutionSetting();
    }

    protected String getLogFolderLocation(TestCaseEntity testCase) {
        try {
            return ReportController.getInstance().generateReportFolder(testCase);
        } catch (Exception e) {
            return "";
        }
    }

    protected String getLogFolderLocation(TestSuiteEntity testSuite) {
        try {
            return ReportController.getInstance().generateReportFolder(testSuite);
        } catch (Exception e) {
            return "";
        }
    }

    public void generateLogFolder(FileEntity fileEntity) {
        String logFolderPath = "";
        if (fileEntity instanceof TestCaseEntity) {
            logFolderPath = getLogFolderLocation((TestCaseEntity) fileEntity);
        } else if (fileEntity instanceof TestSuiteEntity) {
            logFolderPath = getLogFolderLocation((TestSuiteEntity) fileEntity);
        }

        executionSetting.setFolderPath(logFolderPath);
    }

    @Override
    public String getProjectFolderLocation() {
        return ProjectController.getInstance().getCurrentProject().getFolderLocation().replace(File.separator, "/");
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> propertyMap = new LinkedHashMap<String, Object>();

        propertyMap.put(StringConstants.NAME, getName());

        propertyMap.put(RunConfiguration.PROJECT_DIR_PROPERTY, getProjectFolderLocation());

        propertyMap.put(RunConfiguration.HOST, hostConfiguration.getProperties());

        if (executionSetting == null) {
            return propertyMap;
        }
        propertyMap.putAll(ExecutionUtil.getExecutionProperties(executionSetting, getDriverConnectors()));
        IExecutedEntity executedEntity = executionSetting.getExecutedEntity();
        if (executedEntity == null) {
            return propertyMap;
        }
        propertyMap.put(RunConfiguration.EXCUTION_SOURCE_ID, executedEntity.getSourceId());
        propertyMap.put(RunConfiguration.EXCUTION_SOURCE_NAME, executedEntity.getSourceName());
        propertyMap.put(RunConfiguration.EXCUTION_SOURCE_DESCRIPTION, executedEntity.getSourceDescription());
        propertyMap.put(RunConfiguration.EXCUTION_SOURCE, executedEntity.getSourcePath());

        ExecutionSessionSocketServer sessionServer = ExecutionSessionSocketServer.getInstance();
        propertyMap.put(RunConfiguration.SESSION_SERVER_HOST, sessionServer.getServerHost());
        propertyMap.put(RunConfiguration.SESSION_SERVER_PORT, sessionServer.getServerPort());
        return propertyMap;
    }

    @Override
    public String getName() {
        StringBuilder nameStringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (IDriverConnector driverConnector : getDriverConnectors().values()) {
            if (!isFirst) {
                nameStringBuilder.append(" + ");
            }
            nameStringBuilder.append(driverConnector.getDriverType().toString());
            isFirst = false;
        }
        return nameStringBuilder.toString();
    }

    public IHostConfiguration getHostConfiguration() {
        return hostConfiguration;
    }

    @Override
    public IExecutionSetting getExecutionSetting() {
        return executionSetting;
    }

    public final void generateExecutionProperties() throws IOException {
        File settingFile = new File(executionSetting.getSettingFilePath());

        Gson gsonObj = new Gson();
        String strJson = gsonObj.toJson(getProperties());
        FileUtils.writeStringToFile(settingFile, strJson);
    }

    @Override
    public Map<String, String> getAdditionalEnvironmentVariables() throws IOException, ExecutionException {
        return new HashMap<>();
    }
    
    @Override
    public boolean allowsRecording() {
        return false;
    }
}
