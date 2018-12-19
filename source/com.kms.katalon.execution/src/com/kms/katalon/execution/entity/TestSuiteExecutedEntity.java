package com.kms.katalon.execution.entity;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;

import com.katalon.platform.api.exception.PlatformException;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.composer.components.impl.util.EntityIndexingUtil;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FilterController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.core.testdata.TestDataInfo;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestDataCombinationType;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.FilteringTestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.util.MailUtil;

public class TestSuiteExecutedEntity extends ExecutedEntity implements Reportable, Rerunable, ConsoleOptionContributor {
    private List<IExecutedEntity> executedItems;

    private Map<String, TestData> testDataMap;

    private ReportLocationSetting reportLocationSetting;

    private DefaultRerunSetting rerunSetting;

    private EmailSettings emailSettings;

    public TestSuiteExecutedEntity() {
        testDataMap = new HashMap<>();
        reportLocationSetting = new ReportLocationSetting();
        emailSettings = new EmailSettings();
        rerunSetting = new DefaultRerunSetting();
        executedItems = new ArrayList<IExecutedEntity>();
    }

    public TestSuiteExecutedEntity(TestSuiteEntity testSuite) throws Exception {
        this();
        setTestSuite(testSuite);
    }

    public TestSuiteExecutedEntity(TestSuiteEntity testSuite, Rerunable rerunnable) throws Exception {
        this(testSuite);
        rerunSetting.setPreviousRerunTimes(rerunnable.getPreviousRerunTimes());
        rerunSetting.setRemainingRerunTimes(rerunnable.getRemainingRerunTimes());
        rerunSetting.setRerunFailedTestCaseOnly(rerunnable.isRerunFailedTestCasesOnly());
    }

    public void setTestSuite(TestSuiteEntity testSuite) throws IOException, Exception {
        updateEntity(testSuite);
        getEmailConfig(testSuite.getProject())
                .addRecipients(MailUtil.splitRecipientsString(testSuite.getMailRecipient()));
        rerunSetting.setRemainingRerunTimes(testSuite.getNumberOfRerun());
        rerunSetting.setRerunFailedTestCaseOnly(testSuite.isRerunFailedTestCasesOnly());
    }

    public void prepareTestCases() throws Exception {
        TestSuiteEntity testSuite = (TestSuiteEntity) getEntity();
        List<IExecutedEntity> executedItems;
        if (testSuite instanceof FilteringTestSuiteEntity) {
            if (ApplicationManager.getInstance().getPluginManager().getPlugin(IdConstants.PLUGIN_TAGS) == null) {
                throw new PlatformException(
                        MessageFormat.format(ExecutionMessageConstants.LAU_TS_REQUIRES_TAGS_PLUGIN_TO_EXECUTE,
                                testSuite.getIdForDisplay(), IdConstants.PLUGIN_TAGS));
            }
            executedItems = loadTestCasesForFilteringTestSuite((FilteringTestSuiteEntity) testSuite);
        } else {
            executedItems = loadTestCases(testSuite);
        }
        setTestCaseExecutedEntities(executedItems);
    }

    private List<IExecutedEntity> loadTestCasesForFilteringTestSuite(FilteringTestSuiteEntity testSuite)
            throws IOException {
        List<String> testCaseIds = EntityIndexingUtil.getInstance(ProjectController.getInstance().getCurrentProject())
                .getIndexedEntityIds("tc");
        List<TestCaseEntity> filteredTestCases = new ArrayList<>();
        List<TestCaseEntity> testCaseEntities = testCaseIds.stream().map(id -> {
            try {
                return TestCaseController.getInstance().getTestCaseByDisplayId(id);
            } catch (Exception e1) {
                return null;
            }
        }).collect(Collectors.toList());
        filteredTestCases = FilterController.getInstance().filter(testCaseEntities, testSuite.getFilteringText());
        return filteredTestCases.stream().map(tc -> {
            TestCaseExecutedEntity executedTestCase = new TestCaseExecutedEntity(tc);
            executedTestCase.setLoopTimes(1);
            return executedTestCase;
        }).collect(Collectors.toList());
    }

    /**
     * Store a map of test data used in the given test suite into the newly
     * created TestSuiteExecutedEntity
     * 
     * @param testSuite
     */
    private List<IExecutedEntity> loadTestCases(TestSuiteEntity testSuite) throws Exception {
        String projectDir = testSuite.getProject().getFolderLocation();

        testDataMap.clear();

        List<IExecutedEntity> executedItems = new ArrayList<>();
        for (TestSuiteTestCaseLink testCaseLink : TestSuiteController.getInstance()
                .getTestSuiteTestCaseRun(testSuite)) {
            TestCaseEntity testCase = TestCaseController.getInstance()
                    .getTestCaseByDisplayId(testCaseLink.getTestCaseId());

            if (testCase == null) {
                throw new IllegalArgumentException(MessageFormat.format(StringConstants.UTIL_EXC_TEST_CASE_X_NOT_FOUND,
                        testCaseLink.getTestCaseId()));
            }

            TestCaseExecutedEntity testCaseExecutedEntity = new TestCaseExecutedEntity(testCase);
            testCaseExecutedEntity.setLoopTimes(1);

            prepareTestCaseExecutedEntity(projectDir, testCaseLink, testCaseExecutedEntity);
            // make sure all TestDataExecutedEntity in testCaseExecutedEntity
            // has the same rows to prevent NullPointerException

            executedItems.add(testCaseExecutedEntity);
        }
        return executedItems;
    }

    private void prepareTestCaseExecutedEntity(String projectDir, TestSuiteTestCaseLink testCaseLink,
            TestCaseExecutedEntity testCaseExecutedEntity) throws Exception {
        List<TestCaseTestDataLink> testDataLinkUsedList = TestSuiteController.getInstance()
                .getTestDataLinkUsedInTestCase(testCaseLink);
        if (testDataLinkUsedList.size() <= 0) {
            return;
        }
        int numberTestCaseUsedOnce = 0;
        int numTestDataRowUsedManyTimes = 1;
        for (TestCaseTestDataLink testDataLink : testDataLinkUsedList) {
            TestData testData = findTestData(projectDir, testDataMap, testDataLink);

            testDataMap.put(testDataLink.getTestDataId(), testData);

            TestDataExecutedEntity testDataExecutedEntity = getTestDataExecutedEntity(testCaseLink, testDataLink,
                    testData);
            if (testDataExecutedEntity == null) {
                continue;
            }

            int rowCount = testDataExecutedEntity.getRowIndexes().length;

            if (testDataLink.getCombinationType() == TestDataCombinationType.ONE) {
                numberTestCaseUsedOnce = updateNumberTestCaseUsedOnce(numberTestCaseUsedOnce, rowCount);
            } else {
                numTestDataRowUsedManyTimes *= rowCount;
                updateMultiplierForSibblingTestDataExecuted(testCaseExecutedEntity, rowCount);
            }
            testCaseExecutedEntity.getTestDataExecutions().add(testDataExecutedEntity);
        }

        testCaseExecutedEntity.setLoopTimes(numTestDataRowUsedManyTimes * Math.max(numberTestCaseUsedOnce, 1));
        cutRedundantIndexes(testCaseExecutedEntity, Math.max(numberTestCaseUsedOnce, 1));
    }

    private int updateNumberTestCaseUsedOnce(int numberTestCaseUsedOnce, int rowCount) {
        if (numberTestCaseUsedOnce < 1) {
            return rowCount;
        }
        return Math.min(numberTestCaseUsedOnce, rowCount);
    }

    private void updateMultiplierForSibblingTestDataExecuted(TestCaseExecutedEntity testCaseExecutedEntity,
            int rowCount) {
        for (TestDataExecutedEntity siblingDataExecuted : testCaseExecutedEntity.getTestDataExecutions()) {
            if (siblingDataExecuted.getType() != TestDataCombinationType.MANY) {
                continue;
            }
            siblingDataExecuted.setMultiplier(siblingDataExecuted.getMultiplier() * rowCount);
        }
    }

    private TestData findTestData(String projectDir, Map<String, TestData> testDataUsedMap,
            TestCaseTestDataLink testDataLink) throws Exception, IOException {
        // check test data in the test data map first, if is doesn't
        // exist, find it by using TestDataFactory to read its
        // source.
        TestData testData = testDataUsedMap.get(testDataLink.getTestDataId());

        if (testData == null) {
            testData = TestDataFactory.findTestDataForExternalBundleCaller(testDataLink.getTestDataId(), projectDir);
        }

        if (testData == null || testData.getRowNumbers() < 1) {
            throw new IllegalArgumentException(MessageFormat.format(StringConstants.UTIL_EXC_TD_DATA_SRC_X_UNAVAILABLE,
                    testDataLink.getTestDataId()));
        }
        return testData;
    }

    /**
     * Make sure all TestDataExecutedEntity in the given <code>testCaseExecutedEntity</code> has the same rows with the
     * given <code>numberTestCaseUsedOnce</code>
     * 
     * @param testCaseExecutedEntity
     * @see {@link TestCaseExecutedEntity}
     * @param numberTestCaseUsedOnce
     */
    private static void cutRedundantIndexes(TestCaseExecutedEntity testCaseExecutedEntity, int numberTestCaseUsedOnce) {
        if (numberTestCaseUsedOnce <= 1) {
            return;
        }

        for (TestDataExecutedEntity siblingDataExecuted : testCaseExecutedEntity.getTestDataExecutions()) {
            if ((siblingDataExecuted.getType() == TestDataCombinationType.ONE)
                    && (siblingDataExecuted.getRowIndexes().length > numberTestCaseUsedOnce)) {

                int[] newRowIndexs = ArrayUtils.remove(siblingDataExecuted.getRowIndexes(), numberTestCaseUsedOnce);

                siblingDataExecuted.setRowIndexes(newRowIndexs);
            }
        }
    }

    /**
     * Create new TestDataExecutedEntity that's based on the given params.
     * 
     * @param testCaseLink
     * @param testDataLink
     * @param testData
     * @throws Exception
     */
    private static TestDataExecutedEntity getTestDataExecutedEntity(TestSuiteTestCaseLink testCaseLink,
            TestCaseTestDataLink testDataLink, TestData testData) throws Exception {

        TestDataExecutedEntity testDataExecutedEntity = new TestDataExecutedEntity(testDataLink.getId(),
                testDataLink.getTestDataId());
        testDataExecutedEntity.setType(testDataLink.getCombinationType());
        int[] rowIndexes = new int[0];
        switch (testDataLink.getIterationEntity().getIterationType()) {
            case ALL:
                rowIndexes = getRowIndexesForAllIterationType(testData, testDataLink);
                break;
            case RANGE:
                rowIndexes = getRowIndexesForRangeIterationType(testCaseLink, testDataLink, testData);
                break;
            case SPECIFIC:
                rowIndexes = getRowIndexesForSpecificIterationType(testCaseLink, testDataLink, testData);
                break;
        }
        testDataExecutedEntity.setRowIndexes(rowIndexes);
        return testDataExecutedEntity;
    }

    private static int[] getRowIndexesForSpecificIterationType(TestSuiteTestCaseLink testCaseLink,
            TestCaseTestDataLink testDataLink, TestData testData) throws IOException {
        String[] rowIndexesString = testDataLink.getIterationEntity().getValue().replace(" ", "").split(",");
        int totalRowCount = testData.getRowNumbers();
        List<Integer> rowIndexArray = new ArrayList<Integer>();
        for (int index = 0; index < rowIndexesString.length; index++) {
            String rowIndexString = rowIndexesString[index];
            if (rowIndexString.isEmpty()) {
                continue;
            }
            if (rowIndexString.contains("-")) {
                String[] rowIndexStartEndString = rowIndexString.split("-");
                int rowStart = Integer.valueOf(rowIndexStartEndString[0]);
                int rowEnd = Integer.valueOf(rowIndexStartEndString[1]);

                if (rowStart > totalRowCount) {
                    throw new IllegalArgumentException(MessageFormat.format(
                            StringConstants.UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_START_AT_ROW_IDX,
                            testDataLink.getTestDataId(), Integer.toString(totalRowCount), testCaseLink.getTestCaseId(),
                            Integer.toString(rowStart)));
                }

                if (rowEnd > totalRowCount) {
                    throw new IllegalArgumentException(
                            MessageFormat.format(StringConstants.UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_ENDS_AT_ROW_IDX,
                                    testDataLink.getTestDataId(), Integer.toString(totalRowCount),
                                    testCaseLink.getTestCaseId(), Integer.toString(rowEnd)));
                }
                for (int rowIndex = rowStart; rowIndex <= rowEnd; rowIndex++) {
                    rowIndexArray.add(rowIndex);
                }
                continue;
            }
            int rowIndex = Integer.valueOf(rowIndexString);
            if (rowIndex < TestData.BASE_INDEX || rowIndex > totalRowCount) {
                throw new IllegalArgumentException(
                        MessageFormat.format(StringConstants.UTIL_EXC_IDX_X_INVALID_TC_Y_TD_Z, rowIndexString,
                                testCaseLink.getTestCaseId(), testDataLink.getTestDataId()));
            }
            rowIndexArray.add(rowIndex);
        }
        return ArrayUtils.toPrimitive(rowIndexArray.toArray(new Integer[rowIndexArray.size()]));
    }

    private static int[] getRowIndexesForRangeIterationType(TestSuiteTestCaseLink testCaseLink,
            TestCaseTestDataLink testDataLink, TestData testData) throws IOException {
        int rowStart = testDataLink.getIterationEntity().getFrom();
        int rowEnd = testDataLink.getIterationEntity().getTo();
        int totalRowCount = testData.getRowNumbers();
        if (rowStart > totalRowCount) {
            throw new IllegalArgumentException(
                    MessageFormat.format(StringConstants.UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_START_AT_ROW_IDX,
                            testDataLink.getTestDataId(), totalRowCount, testCaseLink.getTestCaseId(), rowStart));
        }

        if (rowEnd > totalRowCount) {
            throw new IllegalArgumentException(
                    MessageFormat.format(StringConstants.UTIL_EXC_TD_X_HAS_ONLY_Y_ROWS_BUT_TC_Z_ENDS_AT_ROW_IDX,
                            testDataLink.getTestDataId(), totalRowCount, testCaseLink.getTestCaseId(), rowEnd));
        }
        int rowCount = rowEnd - rowStart + 1;

        int[] rowIndexes = new int[rowCount];
        for (int index = 0; index < rowCount; index++) {
            rowIndexes[index] = index + rowStart;
        }
        return rowIndexes;
    }

    private static int[] getRowIndexesForAllIterationType(TestData testData, TestCaseTestDataLink testDataLink)
            throws IOException {
        int rowCount = testData.getRowNumbers();

        if (rowCount <= 0) {
            throw new IllegalArgumentException(MessageFormat
                    .format(StringConstants.UTIL_EXC_TD_X_DOES_NOT_CONTAIN_ANY_RECORDS, testDataLink.getTestDataId()));
        }

        int[] rowIndexes = new int[rowCount];
        for (int index = 0; index < rowCount; index++) {
            rowIndexes[index] = index + TestData.BASE_INDEX;
        }
        return rowIndexes;
    }

    public List<IExecutedEntity> getExecutedItems() {
        return executedItems;
    }

    public void setTestCaseExecutedEntities(List<IExecutedEntity> testCaseExecutedEntities) {
        this.executedItems = testCaseExecutedEntities;
    }

    @Override
    public int getTotalTestCases() {
        int total = 0;

        for (IExecutedEntity testCaseExecutionEntity : getExecutedItems()) {
            total += ((TestCaseExecutedEntity) testCaseExecutionEntity).getLoopTimes();
        }
        return total;
    }

    public Map<String, TestData> getTestDataMap() {
        return testDataMap;
    }

    public void setTestDataMap(Map<String, TestData> testDataMap) {
        this.testDataMap = testDataMap;
    }

    public ReportLocationSetting getReportLocationSetting() {
        return reportLocationSetting;
    }

    public void setReportLocation(ReportLocationSetting reportLocation) {
        this.reportLocationSetting = reportLocation;
    }

    @Override
    public int mainTestCaseDepth() {
        return 1;
    }

    @Override
    public boolean isRerunFailedTestCasesOnly() {
        return rerunSetting.isRerunFailedTestCasesOnly();
    }

    public DefaultRerunSetting getRerunSetting() {
        return rerunSetting;
    }

    public void setRerunSetting(DefaultRerunSetting rerunSetting) {
        this.rerunSetting = rerunSetting;
    }

    @Override
    public int getPreviousRerunTimes() {
        return rerunSetting.getPreviousRerunTimes();
    }

    @Override
    public int getRemainingRerunTimes() {
        return rerunSetting.getRemainingRerunTimes();
    }

    public void setEmailConfig(EmailConfig emailConfig) {
        this.emailSettings.setEmailConfig(emailConfig);
    }

    public EmailConfig getEmailConfig(ProjectEntity project) {
        return emailSettings.getEmailConfig(project);
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> consoleOptionList = new ArrayList<ConsoleOption<?>>();
        consoleOptionList.addAll(reportLocationSetting.getConsoleOptionList());
        consoleOptionList.addAll(emailSettings.getConsoleOptionList());
        consoleOptionList.addAll(rerunSetting.getConsoleOptionList());
        return consoleOptionList;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        reportLocationSetting.setArgumentValue(consoleOption, argumentValue);
        emailSettings.setArgumentValue(consoleOption, argumentValue);
        rerunSetting.setArgumentValue(consoleOption, argumentValue);
    }

    @Override
    public Map<String, String> getCollectedDataInfo() {
        Map<String, String> collectedInfo = new HashMap<>();
        for (TestData testDataUsed : testDataMap.values()) {
            TestDataInfo testDataInfo = testDataUsed.getDataInfo();
            if (testDataInfo != null) {
                collectedInfo.put(testDataInfo.getKey(), testDataInfo.getInfo());
            }
        }
        return collectedInfo;
    }
}
