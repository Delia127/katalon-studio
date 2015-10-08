package com.kms.katalon.execution.configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.util.ExecutionUtil;

public abstract class AbstractRunConfiguration implements IRunConfiguration {
    protected String logFolderPath;
    protected String logFilePath;
    protected String projectFolderLocation;
    protected int timeOut;
    protected String hostName;
    protected String hostOS;
    protected String source;
    protected String sourceId;
    protected String sourceName;
    protected String sourceDescription;
    protected String executionSettingFilePath;

    public AbstractRunConfiguration(TestCaseEntity testCaseEntity) {
        init(testCaseEntity);
    }

    public AbstractRunConfiguration(TestSuiteEntity testSuiteEntity) {
        init(testSuiteEntity);
    }

    protected void init(FileEntity fileEntity) {
        if (fileEntity == null) {
            return;
        }
        generateLogFolder(fileEntity);
        generateLogFilePath(fileEntity);
        generateExecutionSettingFilePath(fileEntity);
        projectFolderLocation = fileEntity.getProject().getFolderLocation().replace(File.separator, "/");
        timeOut = (fileEntity instanceof TestSuiteEntity && !((TestSuiteEntity) fileEntity).isPageLoadTimeoutDefault()) ? ((TestSuiteEntity) fileEntity)
                .getPageLoadTimeout() : ExecutionUtil.getDefaultPageLoadTimeout();
        hostName = ExecutionUtil.getLocalHostName();
        hostOS = ExecutionUtil.getLocalOS();
        source = fileEntity.getLocation();
        sourceId = fileEntity.getRelativePathForUI();
        sourceName = fileEntity.getName();
        sourceDescription = fileEntity.getDescription();
    }

    protected String getLogFolderLocation(TestCaseEntity testCase) {
        if (testCase == null) {
            return "";
        }
        String reportFolderName;
        try {
            reportFolderName = ReportController.getInstance().generateReportFolder(testCase);
        } catch (Exception e) {
            return "";
        }
        if (reportFolderName == null) {
            return "";
        }
        return reportFolderName;
    }

    protected String getLogFolderLocation(TestSuiteEntity testSuite) {
        if (testSuite == null) {
            return "";
        }
        String reportFolderName;
        try {
            reportFolderName = ReportController.getInstance().generateReportFolder(testSuite);
        } catch (Exception e) {
            return "";
        }
        if (reportFolderName == null) {
            return "";
        }
        return reportFolderName;
    }

    protected String getLogFileLocation(TestCaseEntity testCase, String reportFolderName) {
        File logFile;
        try {
            logFile = ReportController.getInstance().getLogFile(testCase, reportFolderName);
        } catch (Exception e) {
            return "";
        }
        if (logFile == null) {
            return "";
        }
        return logFile.getAbsolutePath().replace(File.separator, "/");
    }

    protected String getLogFileLocation(TestSuiteEntity testSuite, String reportFolderName) {
        File logFile;
        try {
            logFile = ReportController.getInstance().getLogFile(testSuite, reportFolderName);
        } catch (Exception e) {
            return null;
        }
        if (logFile == null) {
            return "";
        }
        return logFile.getAbsolutePath().replace(File.separator, "/");
    }

    public void generateLogFolder(FileEntity fileEntity) {
        if (fileEntity instanceof TestCaseEntity) {
            logFolderPath = getLogFolderLocation((TestCaseEntity) fileEntity);
        } else if (fileEntity instanceof TestSuiteEntity) {
            logFolderPath = getLogFolderLocation((TestSuiteEntity) fileEntity);
        }
    }

    public void generateLogFilePath(FileEntity fileEntity) {
        if (fileEntity instanceof TestCaseEntity) {
            logFilePath = getLogFileLocation((TestCaseEntity) fileEntity, logFolderPath);
        } else if (fileEntity instanceof TestSuiteEntity) {
            logFilePath = getLogFileLocation((TestSuiteEntity) fileEntity, logFolderPath);
        }
    }

    @Override
    public String getExecutionSettingFilePath() {
        return executionSettingFilePath;
    }

    protected String getExecutionSettingFileLocation(TestCaseEntity testCase, String reportFolderName) {
        File executionSettingFile;
        try {
            executionSettingFile = ReportController.getInstance().getExecutionSettingFile(testCase, reportFolderName);
        } catch (Exception e) {
            return "";
        }
        if (executionSettingFile == null) {
            return "";
        }
        return executionSettingFile.getAbsolutePath().replace(File.separator, "/");
    }

    protected String getExecutionSettingFileLocation(TestSuiteEntity testSuite, String reportFolderName) {
        File executionSettingFile;
        try {
            executionSettingFile = ReportController.getInstance().getExecutionSettingFile(testSuite, reportFolderName);
        } catch (Exception e) {
            return null;
        }
        if (executionSettingFile == null) {
            return "";
        }
        return executionSettingFile.getAbsolutePath().replace(File.separator, "/");
    }

    protected void generateExecutionSettingFilePath(FileEntity fileEntity) {
        if (fileEntity instanceof TestCaseEntity) {
            executionSettingFilePath = getExecutionSettingFileLocation((TestCaseEntity) fileEntity, logFolderPath);
        } else if (fileEntity instanceof TestSuiteEntity) {
            executionSettingFilePath = getExecutionSettingFileLocation((TestSuiteEntity) fileEntity, logFolderPath);
        }
    }

    @Override
    public String getLogFilePath() {
        return logFilePath;

    }

    @Override
    public String getProjectFolderLocation() {
        return projectFolderLocation;
    }

    @Override
    public int getTimeOut() {
        return timeOut;
    }

    @Override
    public String getHostName() {
        return hostName;
    }

    @Override
    public String getOS() {
        return hostOS;
    }

    @Override
    public String getSource() {
        return source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getSourceDescription() {
        return sourceDescription;
    }

    @Override
    public Map<String, Object> getPropertyMap() {
        Map<String, Object> propertyMap = new HashMap<String, Object>();
        propertyMap.put(RunConfiguration.PROJECT_DIR_PROPERTY, getProjectFolderLocation());
        propertyMap.put(RunConfiguration.LOG_FILE_PATH_PROPERTY, getLogFilePath());
        propertyMap.put(RunConfiguration.TIMEOUT_PROPERTY, String.valueOf(getTimeOut()));
        propertyMap.put(RunConfiguration.HOST_NAME, getHostName());
        propertyMap.put(RunConfiguration.HOST_OS, getOS());
        propertyMap.put(RunConfiguration.EXCUTION_SOURCE, getSource());
        propertyMap.put(RunConfiguration.EXCUTION_SOURCE_NAME, getSourceName());
        propertyMap.put(RunConfiguration.EXCUTION_SOURCE_ID, getSourceId());
        propertyMap.put(RunConfiguration.EXCUTION_SOURCE_DESCRIPTION, getSourceDescription());
        for (IDriverConnector driverConnector : getDriverConnectors()) {
            if (driverConnector == null) {
                continue;
            }
            propertyMap.putAll(driverConnector.getExecutionSettingPropertyMap());
        }
        return propertyMap;
    }

    public void setHostOS(String hostOS) {
        this.hostOS = hostOS;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setSourcedescription(String sourcedescription) {
        this.sourceDescription = sourcedescription;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public void setProjectFolderLocation(String projectFolderLocation) {
        this.projectFolderLocation = projectFolderLocation;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getName() {
        StringBuilder nameStringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (IDriverConnector driverConnector : getDriverConnectors()) {
            if (!isFirst) {
                nameStringBuilder.append(" + ");
            }
            nameStringBuilder.append(driverConnector.getDriverType().toString());
            isFirst = false;
        }
        return nameStringBuilder.toString();
    }
}
