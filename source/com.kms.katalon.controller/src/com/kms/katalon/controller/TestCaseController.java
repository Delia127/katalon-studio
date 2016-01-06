package com.kms.katalon.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.e4.core.di.annotations.Creatable;

import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

@Creatable
public class TestCaseController extends EntityController {
    private static EntityController _instance;

    private TestCaseController() {
        super();
    }

    public static TestCaseController getInstance() {
        if (_instance == null) {
            _instance = new TestCaseController();
        }
        return (TestCaseController) _instance;
    }

    public TestCaseEntity addNewTestCase(Object selectionObject, String testCaseName) throws Exception {
        ProjectEntity projectEntity = DataProviderState.getInstance().getCurrentProject();

        if (projectEntity != null) {
            FolderEntity parentFolder = null;
            if (selectionObject instanceof FolderEntity
                    && ((FolderEntity) selectionObject).getFolderType() == FolderType.TESTCASE) {
                parentFolder = (FolderEntity) selectionObject;
            } else if (selectionObject instanceof TestCaseEntity) {
                parentFolder = ((TestCaseEntity) selectionObject).getParentFolder();
            } else {
                parentFolder = dataProviderSetting.getFolderDataProvider().getTestCaseRoot(projectEntity);
            }
            return dataProviderSetting.getTestCaseDataProvider().addNewTestCase(parentFolder, testCaseName);

        }
        return null;

    }

    public TestCaseEntity getTestCase(String testCasePK) throws Exception {
        return dataProviderSetting.getTestCaseDataProvider().getTestCase(testCasePK);
    }

    public void deleteTestCase(TestCaseEntity testCase) throws Exception {
        dataProviderSetting.getTestCaseDataProvider().deleteTestCase(testCase);
    }

    public TestCaseEntity updateTestCase(TestCaseEntity testCase) throws Exception {
        return dataProviderSetting.getTestCaseDataProvider().updateTestCase(testCase);
    }

    public TestCaseEntity copyTestCase(TestCaseEntity testCaseEntity, FolderEntity destinationFolder) throws Exception {
        return dataProviderSetting.getTestCaseDataProvider().copyTestCase(testCaseEntity, destinationFolder);
    }

    public TestCaseEntity moveTestCase(TestCaseEntity testCaseEntity, FolderEntity destinationFolder) throws Exception {
        return dataProviderSetting.getTestCaseDataProvider().moveTestCase(testCaseEntity, destinationFolder);
    }

    /**
     * Get entity ID for display This function is deprecated. Please use {@link TestCaseEntity#getIdForDisplay()}
     * instead.
     * 
     * @param entity
     * @return Test Case ID for display
     * @throws Exception
     */
    @Deprecated
    public String getIdForDisplay(TestCaseEntity entity) throws Exception {
        if (entity == null) {
            return "";
        }
        return dataProviderSetting.getTestCaseDataProvider().getIdForDisplay(entity);
    }

    public List<String> getSibblingTestCaseNames(TestCaseEntity testCase) throws Exception {
        List<TestCaseEntity> sibblingTestCases = FolderController.getInstance().getTestCaseChildren(
                testCase.getParentFolder());
        List<String> sibblingName = new ArrayList<String>();
        for (TestCaseEntity sibblingTestCase : sibblingTestCases) {
            if (!dataProviderSetting.getEntityPk(sibblingTestCase).equals(dataProviderSetting.getEntityPk(testCase))) {
                sibblingName.add(sibblingTestCase.getName());
            }
        }
        return sibblingName;
    }

    public TestCaseEntity getTestCaseByDisplayId(String testCaseDisplayId) throws Exception {
        return dataProviderSetting.getTestCaseDataProvider().getTestCaseByDisplayId(testCaseDisplayId);
    }

    public String getGroovyClassName(TestCaseEntity testCase) {
        File testCaseScriptFolder = GroovyUtil.getTestCaseScriptFolder(testCase);
        for (File file : testCaseScriptFolder.listFiles()) {
            if (FilenameUtils.getExtension(file.getName()).equals("groovy")) {
                return FilenameUtils.getBaseName(file.getName());
            }
        }
        return GroovyUtil.getGroovyClassName(testCase);
    }

    public String getGroovyScriptFilePath(TestCaseEntity testCase) {
        File testCaseScriptFolder = GroovyUtil.getTestCaseScriptFolder(testCase);
        for (File file : testCaseScriptFolder.listFiles()) {
            if (FilenameUtils.getExtension(file.getName()).equals("groovy")) {
                return file.getAbsolutePath();
            }
        }
        return "";
    }

    public VariableEntity getVariable(TestCaseEntity testCase, String variableId) {
        if (testCase == null) {
            throw new IllegalArgumentException(StringConstants.CTRL_EXC_TEST_CASE_CANNOT_BE_NULL);
        }

        if (variableId == null) {
            throw new IllegalArgumentException(StringConstants.CTRL_EXC_VAR_ID_CANNOT_BE_NULL);
        }

        for (VariableEntity variable : testCase.getVariables()) {
            if (variableId.equals(variable.getId())) {
                return variable;
            }
        }
        return null;
    }

    public VariableEntity getVariable(String testCaseDisplayId, String variableName) throws Exception {
        TestCaseEntity testCase = getTestCaseByDisplayId(testCaseDisplayId);

        for (VariableEntity variable : testCase.getVariables()) {
            if (variable.getName().equals(variableName)) {
                return variable;
            }
        }
        return null;
    }

    public String getAvailableTestCaseName(FolderEntity parentFolder, String name) throws Exception {
        return dataProviderSetting.getTestCaseDataProvider().getAvailableTestCaseName(parentFolder, name);
    }

    public TestCaseEntity getTestCaseByScriptName(String scriptFileName) throws Exception {
        return dataProviderSetting.getTestCaseDataProvider().getTestCaseByScriptFileName(scriptFileName);
    }

    /**
     * Get TestCaseEntity by its script file path
     * 
     * @param scriptFilePath raw location file path
     * @return TestCaseEntity
     * @throws Exception
     */
    public TestCaseEntity getTestCaseByScriptFilePath(String scriptFilePath) throws Exception {
        return dataProviderSetting.getTestCaseDataProvider().getTestCaseByScriptFilePath(scriptFilePath);
    }

    /**
     * Find all Test Suite which is using the Test Case
     * 
     * @param testCase Test Case Entity
     * @return List of Test Suite Entity
     */
    public List<TestSuiteEntity> getTestCaseReferences(TestCaseEntity testCase) {
        try {
            return dataProviderSetting.getTestCaseDataProvider().getTestCaseReferences(testCase);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
