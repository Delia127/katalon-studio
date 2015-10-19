package com.kms.katalon.execution.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.generator.TestCaseScriptGenerator;
import com.kms.katalon.execution.generator.TestSuiteScriptGenerator;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.launcher.model.LauncherStatus;
import com.kms.katalon.execution.logging.LogFileWatcher;
import com.kms.katalon.execution.util.ExecutionUtil;
import com.kms.katalon.groovy.util.GroovyUtil;

@SuppressWarnings("restriction")
public class IDELauncher extends AbstractLauncher {
    private IEventBroker eventBroker;
    private Logger systemLogger;
    private LaunchMode launchMode;
    private int reRunTime;

    public IDELauncher(IEventBroker eventBroker, Logger systemLogger, LaunchMode launchMode, IRunConfiguration runConfig) {
        super(runConfig);
        this.eventBroker = eventBroker;
        this.systemLogger = systemLogger;
        this.launchMode = launchMode;
    }

    public LaunchMode getLaunchMode() {
        return launchMode;
    }

    public void launch(TestCaseEntity testCase) throws Exception {
        if (testCase != null) {
            executedEntity = testCase;
            writeRunConfigToFile();
            scriptFile = generateTempTestCaseScript(testCase, runConfig);
            LauncherManager.getInstance().addLauncher(this);
            eventBroker.post(EventConstants.CONSOLE_LOG_RESET, this.getId());
            eventBroker.post(EventConstants.JOB_REFRESH, null);
        }
    }

    public void launch(TestSuiteEntity testSuite, TestSuiteExecutedEntity testSuiteExecutedEntity, int reRunTime)
            throws Exception {

        if (testSuite != null) {
            executedEntity = testSuite;
            this.testSuiteExecutedEntity = testSuiteExecutedEntity;
            writeRunConfigToFile();
            scriptFile = generateTempTestSuiteScript(testSuite, runConfig);
            this.reRunTime = reRunTime;
            LauncherManager.getInstance().addLauncher(this);
            eventBroker.post(EventConstants.CONSOLE_LOG_RESET, this.getId());
            eventBroker.post(EventConstants.JOB_REFRESH, null);
        }
    }

    private IFile generateTempTestSuiteScript(TestSuiteEntity testSuite, IRunConfiguration runConfig) throws Exception {
        if (testSuite != null) {
            File testSuiteScriptFile = new TestSuiteScriptGenerator(testSuite, runConfig, testSuiteExecutedEntity)
                    .generateScriptFile();
            return GroovyUtil.getTempScriptIFile(testSuiteScriptFile, testSuite.getProject());
        }
        return null;
    }

    private static IFile generateTempTestCaseScript(TestCaseEntity testCase, IRunConfiguration runConfig)
            throws Exception {
        if (testCase != null) {
            File testSuiteScriptFile = new TestCaseScriptGenerator(testCase, runConfig).generateScriptFile();
            return GroovyUtil.getTempScriptIFile(testSuiteScriptFile, testCase.getProject());
        }
        return null;
    }

    private void terminateProcess() throws DebugException {
        if (launch.getProcesses() != null && launch.getProcesses().length > 0) {
            RuntimeProcess process = (RuntimeProcess) launch.getProcesses()[0];
            if (process.canTerminate()) {
                process.terminate();
            }
        }
    }

    private void handleLogEvents(final File logFile, final TestSuiteEntity testSuite, final IFile scriptFile)
            throws FileNotFoundException {
        final int readingDelayTime = 1; // in seconds

        logRecords = new ArrayList<XmlLogRecord>();
        logDepth = 0;

        final LogFileWatcher watcher = new LogFileWatcher(logFile, readingDelayTime, systemLogger, this);
        final Thread threadWatcher = new Thread(watcher);
        threadWatcher.start();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean terminated = false;
                while (true) {
                    if (stopSignal && !terminated) {
                        try {
                            terminateProcess();
                            terminated = true;
                        } catch (DebugException e) {
                            systemLogger.error(e);
                        }
                    }

                    if (launch.getProcesses() != null && launch.getProcesses().length > 0
                            && launch.getProcesses()[0].isTerminated()) {
                        terminated = true;
                    }

                    if (terminated) {
                        try {
                            if (!forcedStop) {
                                long currentTime = System.currentTimeMillis();
                                while (System.currentTimeMillis() - currentTime < 30000) {
                                    try {
                                        updateLastRun(testSuite, logFile);
                                        break;
                                    } catch (Exception e) {
                                        // Concurrency modifier exception
                                    }
                                }

                                if (testSuite != null) {
                                    eventBroker.send(EventConstants.TEST_SUITE_UPDATED,
                                            new Object[] { testSuite.getId(), testSuite });
                                }
                                prepareReport(testSuite, logFile);
                            }

                            watcher.setStopSignal(true);
                            threadWatcher.join();
                            Display.getDefault().syncExec(new Runnable() {
                                public void run() {
                                    try {
                                        stopAndSchedule();
                                    } catch (CoreException | InterruptedException e) {
                                        // Exeception happened, return
                                    }
                                }
                            });

                            // update status of "Run" and "Stop" buttons
                            eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
                            eventBroker.post(EventConstants.JOB_REFRESH, null);
                            if (testSuite != null) {
                                eventBroker.post(EventConstants.JOB_COMPLETED, new Object[] { testSuite, launchMode,
                                        runConfig, reRunTime, logFile });
                            }
                        } catch (Exception e) {
                            systemLogger.error(e);
                        }
                        return;
                    }
                    try {
                        Thread.sleep(readingDelayTime * 1000);
                    } catch (InterruptedException e) {
                        // Do nothing
                    }
                }

            }
        });
        thread.start();
    }

    private boolean prepareReport(TestSuiteEntity testSuite, File logFile) {
        try {
            if (testSuite != null) {
                File reportFolder = logFile.getParentFile();
                File htmlFile = new File(reportFolder, FilenameUtils.getBaseName(reportFolder.getName()) + ".html");

                TestSuiteLogRecord suiteLog = ReportUtil.generate(reportFolder.getAbsolutePath());
                // Generate HTML file if it does not exist.
                if (!htmlFile.exists()) {
                    ReportUtil.writeLogRecordToFiles(suiteLog, reportFolder);
                }

                Date currentModified = testSuite.getDateModified();

                uploadReportToIntegratingProduct(suiteLog);

                if (testSuite.getDateModified().after(currentModified)) {
                    eventBroker.send(EventConstants.TEST_SUITE_UPDATED, new Object[] { testSuite.getId(), testSuite });
                }

                if (htmlFile.exists()) {
                    eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, null);
                    // open report page if user has set
                    if (ExecutionUtil.openReportAfterExecuting()) {
                        String reportName = FilenameUtils.getBaseName(logFile.getParent());
                        ReportEntity report = ReportController.getInstance().getReportEntity(testSuite, reportName);
                        eventBroker.send(EventConstants.REPORT_OPEN, report);
                    }
                    sendReportEmail(testSuite, logFile);
                }
            }
        } catch (Exception ex) {
            systemLogger.error(ex);
        }
        return false;
    }

    @Override
    public void execute() {
        try {
            execute(ProjectController.getInstance().getCurrentProject(), scriptFile, launchMode);
            eventBroker.post(EventConstants.JOB_REFRESH, null);
            handleLogEvents(getCurrentLogFile(),
                    (executedEntity instanceof TestSuiteEntity) ? (TestSuiteEntity) executedEntity : null, scriptFile);
        } catch (Exception e) {
            systemLogger.error(e);
        }
    }

    @Override
    public List<XmlLogRecord> getAllRecords() {
        if (logRecords != null) {
            synchronized (logRecords) {
                return new ArrayList<XmlLogRecord>(logRecords);
            }
        }
        return new ArrayList<XmlLogRecord>();
    }

    @Override
    public void addRecords(List<XmlLogRecord> records) {
        try {
            synchronized (logRecords) {
                for (XmlLogRecord record : records) {
                    logRecords.add(record);
                    if (record.getLevel().equals(LogLevel.END)) {
                        logDepth--;
                        if (record.getSourceMethodName().equals(
                                com.kms.katalon.core.constants.StringConstants.LOG_END_TEST_METHOD)) {
                            if (logDepth == 0 || logDepth == 1) {
                                XmlLogRecord resultRecord = logRecords.get(logRecords.size() - 2);
                                if (resultRecord.getLevel() == LogLevel.PASSED) {
                                    launcherResult.increasePasses();
                                } else if (resultRecord.getLevel() == LogLevel.FAILED) {
                                    launcherResult.increaseFailures();
                                } else if (resultRecord.getLevel() == LogLevel.ERROR) {
                                    launcherResult.increaseErrors();
                                }
                                eventBroker.post(EventConstants.JOB_UPDATE_PROGRESS, this.getId());
                            }
                        }

                        if (logDepth == 0) {
                            stopSignal = true;
                        }

                    } else if (record.getLevel().equals(LogLevel.START)) {
                        logDepth++;
                    } else if (record.getLevel().equals(LogLevel.ERROR)) {
                        if (logDepth == 1 && executedEntity instanceof TestSuiteEntity) {
                            launcherResult.increaseErrors();
                            eventBroker.post(EventConstants.JOB_UPDATE_PROGRESS, this.getId());
                        }
                    }
                }
            }

            if (isObserved) {
                eventBroker.send(EventConstants.CONSOLE_LOG_ADD_ITEMS, records);
            }
        } catch (Exception e) {
            systemLogger.error(e);
        }
    }

    @Override
    protected void deleteScriptFile() {
        try {
            scriptFile.delete(true, null);
            IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(ProjectController.getInstance()
                    .getCurrentProject());
            libFolder.refreshLocal(IResource.DEPTH_ONE, null);

            if (isObserved) {
                eventBroker.send(EventConstants.CONSOLE_LOG_RESET, null);
            }
        } catch (CoreException e) {
            systemLogger.error(e);
        }
    }

    public void breakPointHit() {
        if (launch.getProcesses() != null && launch.getProcesses().length > 0
                && !launch.getProcesses()[0].isTerminated()) {
            status = LauncherStatus.SUSPEND;
            eventBroker.post(EventConstants.JOB_REFRESH, null);
        }
    }

    public void suspend() throws DebugException {
        if (launch.getProcesses() != null && launch.getProcesses().length > 0
                && !launch.getProcesses()[0].isTerminated()) {
            status = LauncherStatus.SUSPEND;
            launch.getDebugTargets()[0].suspend();
            eventBroker.post(EventConstants.JOB_REFRESH, null);
        }
    }

    public void resume() throws DebugException {
        if (launch.getProcesses() != null && launch.getProcesses().length > 0
                && !launch.getProcesses()[0].isTerminated()) {
            status = LauncherStatus.RUNNING;
            launch.getDebugTargets()[0].resume();
            eventBroker.post(EventConstants.JOB_REFRESH, null);
        }
    }
}