package com.kms.katalon.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.di.annotations.Creatable;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
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

    public TestSuiteEntity addNewTestSuite(Object selectionObject, String testSuiteName) throws Exception {
        if (selectionObject != null) {
            ProjectEntity projectEntity = DataProviderState.getInstance().getCurrentProject();
            if (projectEntity != null) {
                FolderEntity parentFolder;
                if (selectionObject instanceof FolderEntity) {
                    parentFolder = ((FolderEntity) selectionObject);

                } else if (selectionObject instanceof TestSuiteEntity) {
                    parentFolder = ((TestSuiteEntity) selectionObject).getParentFolder();
                } else {
                    parentFolder = dataProviderSetting.getFolderDataProvider().getTestSuiteRoot(projectEntity);
                }
                return dataProviderSetting.getTestSuiteDataProvider().addNewTestSuite(parentFolder, testSuiteName,
                        TestEnvironmentController.getInstance().getPageLoadTimeOutDefaultValue());
            }
        }
        return null;
    }

    public void deleteTestSuite(TestSuiteEntity testSuite) throws Exception {
        dataProviderSetting.getTestSuiteDataProvider().deleteTestSuite(testSuite);
    }

    public void updateTestSuite(TestSuiteEntity testSuite) throws Exception {
        dataProviderSetting.getTestSuiteDataProvider().updateTestSuite(testSuite);
    }

    public TestSuiteEntity copyTestSuite(TestSuiteEntity testSuite, FolderEntity targetFolder) throws Exception {
        return dataProviderSetting.getTestSuiteDataProvider().copyTestSuite(testSuite, targetFolder);
    }

    public TestSuiteEntity moveTestSuite(TestSuiteEntity testSuite, FolderEntity targetFolder) throws Exception {
        return dataProviderSetting.getTestSuiteDataProvider().moveTestSuite(testSuite, targetFolder);
    }

    public String getIdForDisplay(TestSuiteEntity entity) throws Exception {
        return dataProviderSetting.getTestSuiteDataProvider().getIdForDisplay(entity)
                .replace(File.separator, GlobalStringConstants.ENTITY_ID_SEPERATOR);
    }

    public TestSuiteEntity getTestSuiteByDisplayId(String testSuiteId, ProjectEntity projectEntity) throws Exception {
        String testSuitePk = projectEntity.getFolderLocation() + File.separator
                + testSuiteId.replace(GlobalStringConstants.ENTITY_ID_SEPERATOR, File.separator)
                + TestSuiteEntity.getTestSuiteFileExtension();
        return getTestSuite(testSuitePk);
    }

    public TestSuiteEntity getTestSuiteByTestSuitePartId(String elementId) throws Exception {
        String testSuitePkWithDoubleBrackets = elementId.replaceFirst(IdConstants.TESTSUITE_CONTENT_PART_ID_PREFIX, "");
        String testSuitePk = testSuitePkWithDoubleBrackets.substring(1, testSuitePkWithDoubleBrackets.length() - 1);

        return dataProviderSetting.getTestSuiteDataProvider().getTestSuite(testSuitePk);
    }

    public List<String> getSibblingTestSuiteNames(TestSuiteEntity testSuite) throws Exception {
        List<FileEntity> sibblingEntities = FolderController.getInstance().getChildren(testSuite.getParentFolder());
        List<String> sibblingName = new ArrayList<String>();
        for (FileEntity sibblingEntity : sibblingEntities) {
            if (sibblingEntity instanceof TestSuiteEntity
                    && !dataProviderSetting.getEntityPk(sibblingEntity).equals(
                            dataProviderSetting.getEntityPk(testSuite))) {
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

    public List<TestCaseTestDataLink> getTestDataLinkUsedInTestCase(TestSuiteTestCaseLink testCaseLink) {
        List<TestCaseTestDataLink> testDataLinkUsed = new ArrayList<TestCaseTestDataLink>();

        Map<String, TestCaseTestDataLink> testDataLinkMap = new HashMap<String, TestCaseTestDataLink>();

        for (TestCaseTestDataLink testDataLink : testCaseLink.getTestDataLinks()) {
            testDataLinkMap.put(testDataLink.getId(), testDataLink);
        }

        for (VariableLink variableLink : testCaseLink.getVariableLinks()) {
            if (variableLink.getType() == VariableType.SCRIPT || variableLink.getTestDataLinkId() == null
                    || variableLink.getValue() == null) continue;

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
        return dataProviderSetting.getTestSuiteDataProvider().getTestSuite(testSuitePk);
    }

    /**
     * 
     * @param testDataLinkId
     *            : GUID of test data link
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
        return dataProviderSetting.getTestSuiteDataProvider().getAvailableTestSuiteName(parentFolder, name);
    }
    
    public TestSuiteTestCaseLink getTestCaseLink(String testCaseId, TestSuiteEntity testSuite) {
        return dataProviderSetting.getTestSuiteDataProvider().getTestCaseLink(testSuite, testCaseId);
    }
}
