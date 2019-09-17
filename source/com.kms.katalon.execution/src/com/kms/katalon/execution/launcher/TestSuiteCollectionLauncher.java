package com.kms.katalon.execution.launcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.katalon.platform.api.event.ExecutionEvent;
import com.katalon.platform.api.execution.TestSuiteExecutionContext;
import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.logging.model.TestSuiteCollectionLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportItemDescription;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity.ExecutionMode;
import com.kms.katalon.execution.entity.TestSuiteCollectionExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteCollectionExecutionContextImpl;
import com.kms.katalon.execution.handler.OrganizationHandler;
import com.kms.katalon.execution.integration.ReportIntegrationContribution;
import com.kms.katalon.execution.integration.ReportIntegrationFactory;
import com.kms.katalon.execution.launcher.listener.LauncherEvent;
import com.kms.katalon.execution.launcher.listener.LauncherListener;
import com.kms.katalon.execution.launcher.listener.LauncherNotifiedObject;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.result.ExecutionEntityResult;
import com.kms.katalon.execution.launcher.result.ILauncherResult;
import com.kms.katalon.execution.launcher.result.LauncherResult;
import com.kms.katalon.execution.launcher.result.LauncherStatus;
import com.kms.katalon.execution.platform.TestSuiteCollectionExecutionEvent;
import com.kms.katalon.logging.LogUtil;

public class TestSuiteCollectionLauncher extends BasicLauncher implements LauncherListener {

    public static final int MAX_NUMBER_INSTANCES_IN_PARALLEL_MODE = 8;

    protected List<ReportableLauncher> subLaunchers;

    private LauncherResult result;

    protected TestSuiteCollectionLauncherManager subLauncherManager;

    private LauncherManager parentManager;

    private Thread watchDog;

    private TestSuiteCollectionExecutedEntity executedEntity;

    private ExecutionMode executionMode;

    private ReportCollectionEntity reportCollection;
    
    private ReportableLauncher reportLauncher;

    private Date startTime;

    private Date endTime;
    
    public TestSuiteCollectionLauncher(TestSuiteCollectionExecutedEntity executedEntity, LauncherManager parentManager,
            List<ReportableLauncher> subLaunchers, ExecutionMode executionMode,
            ReportCollectionEntity reportCollection,
            String execytionUUID) {
        super.setExecutionUUID(execytionUUID);
        this.subLauncherManager = new TestSuiteCollectionLauncherManager();
        this.subLaunchers = subLaunchers;
        for (ReportableLauncher subLauncher : subLaunchers) {
            subLauncher.setExecutionUUID(super.getExecutionUUID());
        }
        this.result = new LauncherResult(executedEntity.getTotalTestCases());
        this.parentManager = parentManager;
        this.executedEntity = executedEntity;
        this.executionMode = executionMode;
        this.reportCollection = reportCollection;
        addListenerForChildren(subLaunchers);
    }

    private void addListenerForChildren(List<? extends ReportableLauncher> subLaunchers) {
        for (ReportableLauncher childLauncher : subLaunchers) {
            childLauncher.addListener(this);
            childLauncher.setParentLauncher(this);
        }
        reportLauncher = subLaunchers.get(0);
    }

    @Override
    public void start() throws IOException {
        setStatus(LauncherStatus.RUNNING);

        preStarting();

        scheduleSubLaunchers();

        startWatchDog();

        startTime = new Date();
        
        sendTrackingActivity();
        fireTestSuiteExecutionEvent(ExecutionEvent.TEST_SUITE_COLLECTION_STARTED_EVENT);
    }
    
    private void sendTrackingActivity() {
       ReportIntegrationContribution analyticsProvider = ReportIntegrationFactory.getInstance().getAnalyticsProvider();
       String machineId = getMachineId();
       String sessionId = getExecutionUUID();
       Date startTime = getStartTime(); 
       Date endTime = getEndTime(); 
       String ksVersion = VersionUtil.getCurrentVersion().getVersion();
       Long organizationId = OrganizationHandler.getOrganizationId();
       ExecutorService executors = Executors.newFixedThreadPool(2);
       executors.submit(() -> {
           analyticsProvider.sendTrackingActivity(organizationId, machineId, sessionId, startTime, endTime, ksVersion);
       });
    }

    private void scheduleSubLaunchers() {
        for (ReportableLauncher launcher : subLaunchers) {
            subLauncherManager.addLauncher(launcher);
            launcher.setManager(subLauncherManager);
        }
    }

    protected void preStarting() {
        // Children may override this
    }

    private void startWatchDog() {
        watchDog = new Thread(new Runnable() {
            @Override
            public void run() {
                while (subLauncherManager.isAnyLauncherRunning()) {
                    try {
                        Thread.sleep(IWatcher.DF_TIME_OUT_IN_MILLIS);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                
                endTime = new Date();
                
                prepareReport();
                
                setStatus(LauncherStatus.UPLOAD_REPORT);
                reportLauncher.uploadReportTestSuiteCollection(
                        reportCollection.getReportItemDescriptions(),
                        reportCollection.getParentFolder().getLocation());
                setStatus(LauncherStatus.DONE);
                postExecution();
            }
        });
        watchDog.start();
    }
    
    private TestSuiteCollectionLogRecord prepareReport() {
        try {
            List<TestSuiteLogRecord> suiteLogRecords = new ArrayList<>();
            for (ReportableLauncher subLauncher : subLaunchers) {
                TestSuiteLogRecord suiteLogRecord = subLauncher.getTestSuiteLogRecord();
                if (suiteLogRecord != null) {
                   suiteLogRecords.add(suiteLogRecord); 
                }
            }

            TestSuiteCollectionLogRecord suiteCollectionLogRecord = new TestSuiteCollectionLogRecord();
            suiteCollectionLogRecord.setTestSuiteCollectionId(executedEntity.getEntity().getName());
            suiteCollectionLogRecord.setTestSuiteRecords(suiteLogRecords);
            suiteCollectionLogRecord.setStartTime(startTime != null ? startTime.getTime() : 0L);
            suiteCollectionLogRecord.setEndTime(endTime != null ? endTime.getTime() : 0L);
            suiteCollectionLogRecord.setTotalPassedTestCases(String.valueOf(result.getNumPasses()));
            suiteCollectionLogRecord.setTotalFailedTestCases(String.valueOf(result.getNumFailures()));
            suiteCollectionLogRecord.setTotalErrorTestCases(String.valueOf(result.getNumErrors()));

            ReportUtil.writeJUnitReport(suiteCollectionLogRecord, getReportFolder());

            return suiteCollectionLogRecord;
        } catch(Exception e) {
            LogUtil.printAndLogError(e);
            return null;
        }
    }
    
    protected File getReportFolder() {
        return new File(reportCollection.getParentFolder().getLocation());
    }

    protected void postExecution() {
        schedule();
        
        sendTrackingActivity();
        
        fireTestSuiteExecutionEvent(ExecutionEvent.TEST_SUITE_COLLECTION_FINISHED_EVENT);
    }

    protected void schedule() {
        try {
            parentManager.stopRunningAndSchedule(this);
        } catch (InterruptedException e) {
            LogUtil.logError(e);
        }
    }

    @Override
    public void setStatus(LauncherStatus status) {
        super.setStatus(status);
        if (LauncherStatus.DONE == status || LauncherStatus.TERMINATED == status) {
            ExecutionEntityResult executionResult = new ExecutionEntityResult();
            executionResult.setEnd(true);
            notifyProccess(status, executedEntity, executionResult);
        }
    }
    
    @Override
    public void stop() {
        setStatus(LauncherStatus.TERMINATED);

        if (watchDog != null && watchDog.isAlive()) {
            watchDog.interrupt();
        }

        subLauncherManager.stopAllLauncher();

        postExecution();
    }

    @Override
    public void clean() {
        for (ReportableLauncher launcher : subLaunchers) {
            launcher.clean();
        }
    }

    @Override
    public String getId() {
        return executedEntity.getId();
    }

    @Override
    public String getName() {
        return executedEntity.getSourceId() + " - " + executedEntity.getId();
    }

    @Override
    public ILauncherResult getResult() {
        return result;
    }

    public class TestSuiteCollectionLauncherManager extends LauncherManager {
        protected boolean isLauncherReadyToRun(ILauncher launcher) {
            if (executionMode == ExecutionMode.PARALLEL) {
                return getRunningLaunchers().size() < executedEntity.getEntity().getMaxConcurrentInstances();
            }
            return getRunningLaunchers().isEmpty();
        }

        @Override
        public String getChildrenLauncherStatus(int consoleWidth) {
            return super.getChildrenLauncherStatus(consoleWidth);
        }

        @Override
        protected void schedule() {
            try {
                Thread.sleep(IWatcher.DF_TIME_OUT_IN_MILLIS);
            } catch (InterruptedException e) {
                LogUtil.logError(e);
            }
            super.schedule();
        }

        @Override
        public void addLauncher(ILauncher subLauncher) {
            addNewLauncher((SubLauncher) subLauncher);
            super.addLauncher(subLauncher);
        }
    }

    private void addNewLauncher(SubLauncher subLauncher) {
        ReportableLauncher subReportableLauncher = (ReportableLauncher) subLauncher;

        if (this.subLaunchers.contains(subReportableLauncher)) {
            return;
        }
        this.subLaunchers.add(subReportableLauncher);
        subReportableLauncher.addListener(this);
        subReportableLauncher.setParentLauncher(this);

        ILauncherResult subLauncherResult = subLauncher.getResult();
        LauncherResult newResult = new LauncherResult(
                result.getTotalTestCases() + subLauncherResult.getTotalTestCases());
        newResult.setNumPasses(result.getNumPasses() + subLauncherResult.getNumPasses());
        newResult.setNumFailures(result.getNumFailures() + subLauncherResult.getNumFailures());
        newResult.setNumIncomplete(result.getNumIncomplete() + subLauncherResult.getNumIncomplete());
        newResult.setNumErrors(result.getNumErrors() + subLauncherResult.getNumErrors());
        result = newResult;

        reportCollection.getReportItemDescriptions()
                .add(ReportItemDescription.from(subReportableLauncher.getReportEntity().getIdForDisplay(),
                        subLauncher.getRunConfigurationDescription()));
        try {
            ReportController.getInstance().updateReportCollection(reportCollection);
        } catch (DALException e) {
            LogUtil.logError(e);
        }
        onNewLauncherAdded();
    }

    protected void onNewLauncherAdded() {
        // Children may override this
    }

    @Override
    public void handleLauncherEvent(LauncherEvent event, LauncherNotifiedObject object) {
        if (event == LauncherEvent.UPDATE_RESULT) {
            TestStatusValue statusValue = (TestStatusValue) object.getObject();
            switch (statusValue) {
                case ERROR:
                    result.increaseErrors();
                    break;
                case FAILED:
                    result.increaseFailures();
                    break;
                case PASSED:
                    result.increasePasses();
                    break;
                default:
                    break;
            }
            onUpdateResult(statusValue);
        }
    }

    protected TestSuiteCollectionExecutionEvent fireTestSuiteExecutionEvent(String eventName) {
        List<TestSuiteExecutionContext> testSuiteContexts = new ArrayList<>();

        for (ReportableLauncher subLauncher : subLaunchers) {
            testSuiteContexts.add(subLauncher.getTestSuiteExecutionContext());
        }

        TestSuiteCollectionExecutionContextImpl executionContext = TestSuiteCollectionExecutionContextImpl.Builder
                .create(getId(), executedEntity.getSourceId())
                .withReportId(reportCollection.getIdForDisplay())
                .withTestSuiteContexts(testSuiteContexts)
                .withProjectLocation(executedEntity.getEntity().getProject().getFolderLocation())
                .withStartTime(startTime != null ? startTime.getTime() : 0L)
                .withEndTime(endTime != null ? endTime.getTime() : 0L)
                .build();
        TestSuiteCollectionExecutionEvent eventObject = new TestSuiteCollectionExecutionEvent(eventName,
                executionContext);
        EventBrokerSingleton.getInstance().getEventBroker().post(eventName, eventObject);

        return eventObject;
    }

    public List<ReportableLauncher> getSubLaunchers() {
        return Collections.unmodifiableList(subLaunchers);
    }
    

    public TestSuiteCollectionExecutedEntity getExecutedEntity() {
        return executedEntity;
    }

    public Date getStartTime() {
        return startTime;
    }
    
    public Date getEndTime() {
        return endTime;
    }

}
