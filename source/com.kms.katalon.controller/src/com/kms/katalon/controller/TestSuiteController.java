package com.kms.katalon.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.di.annotations.Creatable;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.constants.StringConstants;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.FilteringTestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.entity.variable.VariableEntity;

@Creatable
public class TestSuiteController extends EntityController {

    private static EntityController _instance;

    private TestSuiteController() {
        super();
    }

    public static TestSuiteController getInstance() {
        if (_instance == null) {
            _instance = new TestSuiteController();
        }
        return (TestSuiteController) _instance;
    }

    /**
     * Create and save new Test Suite
     * 
     * @param parentFolder Parent folder
     * @param testSuiteName Test Suite name. Default name (New Test Suite) will be used if this null or empty
     * @return {@link TestSuiteEntity} the saved Test Suite
     * @throws Exception
     */
    public TestSuiteEntity newTestSuite(FolderEntity parentFolder, String testSuiteName) throws Exception {
        return saveNewTestSuite(newTestSuiteWithoutSave(parentFolder, testSuiteName));
    }

    /**
     * Create a NEW Test Suite without save
     * 
     * @param parentFolder Parent folder
     * @param testSuiteName Test Suite name. Default name (New Test Suite) will be used if this null or empty
     * @return {@link TestSuiteEntity}
     * @throws Exception
     */
    public TestSuiteEntity newTestSuiteWithoutSave(FolderEntity parentFolder, String testSuiteName) throws Exception {
        if (parentFolder == null) {
            return null;
        }

        if (StringUtils.isBlank(testSuiteName)) {
            testSuiteName = StringConstants.CTRL_NEW_TEST_SUITE;
        }

        TestSuiteEntity newTestSuite = new TestSuiteEntity();
        newTestSuite.setTestSuiteGuid(Util.generateGuid());
        newTestSuite.setName(getAvailableTestSuiteName(parentFolder, testSuiteName));
        newTestSuite.setParentFolder(parentFolder);
        newTestSuite.setProject(parentFolder.getProject());

        return newTestSuite;
    }

    /**
     * Create a NEW Filtering Test Suite without save
     * 
     * @param parentFolder Parent folder
     * @param testSuiteName Test Suite name. Default name (New Test Suite) will be used if this null or empty
     * @return {@link TestSuiteEntity}
     * @throws DALException 
     * @throws Exception
     */
    public FilteringTestSuiteEntity newFilteringTestSuiteWithoutSave(FolderEntity parentFolder, String testSuiteName) throws DALException {
        if (parentFolder == null) {
            return null;
        }

        if (StringUtils.isBlank(testSuiteName)) {
            testSuiteName = StringConstants.CTRL_NEW_TEST_SUITE;
        }

        FilteringTestSuiteEntity newTestSuite = new FilteringTestSuiteEntity();
        newTestSuite.setTestSuiteGuid(Util.generateGuid());
        try {
            newTestSuite.setName(getAvailableTestSuiteName(parentFolder, testSuiteName));
        } catch (Exception e) {
            throw new DALException(e);
        }
        newTestSuite.setParentFolder(parentFolder);
        newTestSuite.setProject(parentFolder.getProject());
        newTestSuite.setFilteringText("");
        return newTestSuite;
    }

    /**
     * Save a NEW Test Suite.<br>
     * Please user {@link #updateTestSuite(TestSuiteEntity)} if you want to save an existing Test Suite.
     * 
     * @param newTestSuite a new Test Suite which is created by {@link #newTestSuiteWithoutSave(FolderEntity, String)}
     * @return {@link TestSuiteEntity} the saved Test Suite
     * @throws Exception
     */
    public TestSuiteEntity saveNewTestSuite(TestSuiteEntity newTestSuite) throws Exception {
        return getDataProviderSetting().getTestSuiteDataProvider().saveNewTestSuite(newTestSuite);
    }

    public void deleteTestSuite(TestSuiteEntity testSuite) throws Exception {
        getDataProviderSetting().getTestSuiteDataProvider().deleteTestSuite(testSuite);
    }

    public synchronized TestSuiteEntity updateTestSuite(TestSuiteEntity testSuite) throws Exception {
        return getDataProviderSetting().getTestSuiteDataProvider().updateTestSuite(testSuite);
    }

    public TestSuiteEntity renameTestSuite(String newName, TestSuiteEntity testSuite) throws Exception {
        return getDataProviderSetting().getTestSuiteDataProvider().renameTestSuite(newName, testSuite);
    }

    public TestSuiteEntity copyTestSuite(TestSuiteEntity testSuite, FolderEntity targetFolder) throws Exception {
        return getDataProviderSetting().getTestSuiteDataProvider().copyTestSuite(testSuite, targetFolder);
    }

    public TestSuiteEntity moveTestSuite(TestSuiteEntity testSuite, FolderEntity targetFolder) throws Exception {
        return getDataProviderSetting().getTestSuiteDataProvider().moveTestSuite(testSuite, targetFolder);
    }

    /**
     * Get entity ID for display This function is deprecated. Please use {@link TestSuiteEntity#getIdForDisplay()}
     * instead.
     * 
     * @param entity
     * @return Test Suite ID for display
     * @throws Exception
     */
    @Deprecated
    public String getIdForDisplay(TestSuiteEntity entity) throws Exception {
        return getDataProviderSetting().getTestSuiteDataProvider().getIdForDisplay(entity).replace(File.separator,
                GlobalStringConstants.ENTITY_ID_SEPARATOR);
    }

    public TestSuiteEntity getTestSuiteByDisplayId(String testSuiteId, ProjectEntity projectEntity) throws Exception {
        String testSuitePk = projectEntity.getFolderLocation() + File.separator
                + testSuiteId.replace(GlobalStringConstants.ENTITY_ID_SEPARATOR, File.separator)
                + TestSuiteEntity.getTestSuiteFileExtension();
        return getTestSuite(testSuitePk);
    }

    public TestSuiteEntity getTestSuiteByTestSuitePartId(String elementId) throws Exception {
        String testSuitePkWithDoubleBrackets = elementId.replaceFirst(IdConstants.TESTSUITE_CONTENT_PART_ID_PREFIX, "");
        String testSuitePk = testSuitePkWithDoubleBrackets.substring(1, testSuitePkWithDoubleBrackets.length() - 1);

        return getDataProviderSetting().getTestSuiteDataProvider().getTestSuite(testSuitePk);
    }

    public List<String> getSibblingTestSuiteNames(TestSuiteEntity testSuite) throws Exception {
        List<FileEntity> sibblingEntities = FolderController.getInstance().getChildren(testSuite.getParentFolder());
        List<String> sibblingName = new ArrayList<String>();
        for (FileEntity sibblingEntity : sibblingEntities) {
            if (sibblingEntity instanceof TestSuiteEntity && !getDataProviderSetting().getEntityPk(sibblingEntity)
                    .equals(getDataProviderSetting().getEntityPk(testSuite))) {
                sibblingName.add(sibblingEntity.getName());
            }
        }
        return sibblingName;
    }

    public List<TestSuiteTestCaseLink> getTestSuiteTestCaseRun(TestSuiteEntity testSuite) {
        List<TestSuiteTestCaseLink> runnableTestCases = new ArrayList<TestSuiteTestCaseLink>();
        for (TestSuiteTestCaseLink link : testSuite.getTestSuiteTestCaseLinks()) {
            if (link.getIsRun()) {
                runnableTestCases.add(link);
            }
        }
        return runnableTestCases;
    }

    public VariableEntity getVariable(TestSuiteTestCaseLink testCaseLink, VariableLink variableLink) throws Exception {
        String testCaseId = testCaseLink.getTestCaseId();
        TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByDisplayId(testCaseId);
        if (testCase != null) {
            return TestCaseController.getInstance().getVariable(testCase, variableLink.getVariableId());
        } else {
            return null;
        }
    }

    public VariableEntity getVariable(String testCaseId, VariableLink variableLink) throws Exception {
        TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByDisplayId(testCaseId);
        return testCase != null ? TestCaseController.getInstance().getVariable(testCase, variableLink.getVariableId())
                : null;
    }

    public List<TestCaseTestDataLink> getTestDataLinkUsedInTestCase(TestSuiteTestCaseLink testCaseLink) {
        List<TestCaseTestDataLink> testDataLinkUsed = new ArrayList<TestCaseTestDataLink>();

        Map<String, TestCaseTestDataLink> testDataLinkMap = new HashMap<String, TestCaseTestDataLink>();

        for (TestCaseTestDataLink testDataLink : testCaseLink.getTestDataLinks()) {
            testDataLinkMap.put(testDataLink.getId(), testDataLink);
        }

        for (VariableLink variableLink : testCaseLink.getVariableLinks()) {
            if (variableLink.getType() == VariableType.DEFAULT || variableLink.getTestDataLinkId() == null
                    || variableLink.getValue() == null)
                continue;

            String testDataLinkId = variableLink.getTestDataLinkId();

            TestCaseTestDataLink testDataLink = testDataLinkMap.get(testDataLinkId);
            if (testDataLink != null && !testDataLinkUsed.contains(testDataLink)) {
                testDataLinkUsed.add(testDataLink);
            }
        }

        return testDataLinkUsed;
    }

    public String[] mailRcpStringToArray(String mailRcpString) {
        if (mailRcpString != null && !mailRcpString.isEmpty()) {
            return mailRcpString.split(";");
        }
        return new String[0];
    }

    public String arrayMailRcpToString(String[] mailRcpArray) {
        StringBuilder builder = new StringBuilder("");
        for (String mailRcp : mailRcpArray) {
            builder.append(mailRcp + ";");
        }
        return builder.toString();
    }

    public TestSuiteEntity getTestSuite(String testSuitePk) throws Exception {
        return getDataProviderSetting().getTestSuiteDataProvider().getTestSuite(testSuitePk);
    }

    /**
     * 
     * @param testDataLinkId : GUID of test data link
     * @return find test data link in tree that has id equals the give id
     */
    public TestCaseTestDataLink getTestDataLink(String testDataLinkId, TestSuiteTestCaseLink testCaseLink) {
        for (TestCaseTestDataLink dataLink : testCaseLink.getTestDataLinks()) {
            if (dataLink.getId().equals(testDataLinkId)) {
                return dataLink;
            }
        }
        return null;
    }

    public String getAvailableTestSuiteName(FolderEntity parentFolder, String name) throws Exception {
        return getDataProviderSetting().getTestSuiteDataProvider().getAvailableTestSuiteName(parentFolder, name);
    }

    public TestSuiteTestCaseLink getTestCaseLink(String testCaseId, TestSuiteEntity testSuite) {
        return getDataProviderSetting().getTestSuiteDataProvider().getTestCaseLink(testSuite, testCaseId);
    }

    public void reloadTestSuite(TestSuiteEntity testSuite, Entity entity) throws Exception {
        entity = testSuite = getTestSuite(entity.getId());
    }

    public List<TestSuiteCollectionEntity> getTestSuiteCollectionReferences(TestSuiteEntity testSuite)
            throws DALException {
        return getDataProviderSetting().getTestSuiteCollectionDataProvider().getTestSuiteCollectionReferences(testSuite,
                ProjectController.getInstance().getCurrentProject());
    }

    public void removeTestSuiteCollectionReferences(TestSuiteEntity testSuite,
            List<TestSuiteCollectionEntity> testSuiteCollectionReferences) throws DALException {
        getDataProviderSetting().getTestSuiteCollectionDataProvider().removeTestSuiteCollectionReferences(testSuite,
                testSuiteCollectionReferences);
    }

    public File getTestSuiteScriptFile(TestSuiteEntity testSuite) throws DALException {
        return getDataProviderSetting().getTestSuiteDataProvider().getTestSuiteScriptFile(testSuite);
    }

    public File newTestSuiteScriptFile(TestSuiteEntity testSuite) throws DALException {
        return getDataProviderSetting().getTestSuiteDataProvider().newTestSuiteScriptFile(testSuite);
    }
}
