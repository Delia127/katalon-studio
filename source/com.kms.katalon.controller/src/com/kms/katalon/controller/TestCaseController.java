package com.kms.katalon.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.di.annotations.Creatable;

import com.kms.katalon.controller.constants.StringConstants;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.util.Util;
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

    /**
     * Create and save new Test Case
     * 
     * @param parentFolder parent folder
     * @param testCaseName Test Case name
     * @return {@link TestCaseEntity}
     * @throws Exception
     */
    public TestCaseEntity newTestCase(FolderEntity parentFolder, String testCaseName) throws ControllerException {
        try {
            return saveNewTestCase(newTestCaseWithoutSave(parentFolder, testCaseName));
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

    /**
     * Create new Test Case without save action
     * 
     * @param parentFolder
     * @param defaultName Test Case name. Default name (New Test Case) will be used if this null or empty
     * @return {@link TestCaseEntity}
     * @throws Exception
     */
    public TestCaseEntity newTestCaseWithoutSave(FolderEntity parentFolder, String testCaseName) throws Exception {
        if (parentFolder == null) {
            return null;
        }

        if (StringUtils.isBlank(testCaseName)) {
            testCaseName = StringConstants.CTRL_NEW_TEST_CASE;
        }

        TestCaseEntity newTestCase = new TestCaseEntity();
        newTestCase.setTestCaseGuid(Util.generateGuid());
        newTestCase.setName(getAvailableTestCaseName(parentFolder, testCaseName));
        newTestCase.setParentFolder(parentFolder);
        newTestCase.setProject(parentFolder.getProject());
        return newTestCase;
    }

    /**
     * Save a NEW Test Case entity.<br>
     * Please use {@link #updateTestCase(TestCaseEntity)} if you want to save an existing Test Case.
     * 
     * @param testCase new Test Case entity which is created by {@link #newTestCaseWithoutSave(FolderEntity, String)}
     * @return {@link TestCaseEntity}
     * @throws Exception
     */
    public TestCaseEntity saveNewTestCase(TestCaseEntity newTestCase) throws ControllerException {
        if (newTestCase == null || newTestCase.getProject() == null || newTestCase.getParentFolder() == null) {
            return null;
        }

        try {
            return getDataProviderSetting().getTestCaseDataProvider().saveNewTestCase(newTestCase);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

    public TestCaseEntity getTestCase(String testCasePK) throws Exception {
        return getDataProviderSetting().getTestCaseDataProvider().getTestCase(testCasePK);
    }

    public void deleteTestCase(TestCaseEntity testCase) throws Exception {
        getDataProviderSetting().getTestCaseDataProvider().deleteTestCase(testCase);
    }

    public TestCaseEntity updateTestCase(TestCaseEntity testCase) throws Exception {
        return getDataProviderSetting().getTestCaseDataProvider().updateTestCase(testCase);
    }

    public TestCaseEntity copyTestCase(TestCaseEntity testCaseEntity, FolderEntity destinationFolder) throws Exception {
        return getDataProviderSetting().getTestCaseDataProvider().copyTestCase(testCaseEntity, destinationFolder);
    }

    public TestCaseEntity moveTestCase(TestCaseEntity testCaseEntity, FolderEntity destinationFolder) throws Exception {
        return getDataProviderSetting().getTestCaseDataProvider().moveTestCase(testCaseEntity, destinationFolder);
    }

    public List<String> getSibblingTestCaseNames(TestCaseEntity testCase) throws Exception {
        List<TestCaseEntity> sibblingTestCases = FolderController.getInstance()
                .getTestCaseChildren(testCase.getParentFolder());
        List<String> sibblingName = new ArrayList<String>();
        for (TestCaseEntity sibblingTestCase : sibblingTestCases) {
            if (!getDataProviderSetting().getEntityPk(sibblingTestCase)
                    .equals(getDataProviderSetting().getEntityPk(testCase))) {
                sibblingName.add(sibblingTestCase.getName());
            }
        }
        return sibblingName;
    }

    public TestCaseEntity getTestCaseByDisplayId(String testCaseDisplayId) throws ControllerException {
        try {
            return getDataProviderSetting().getTestCaseDataProvider().getTestCaseByDisplayId(testCaseDisplayId);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
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

    public String getAvailableTestCaseName(FolderEntity parentFolder, String name) throws ControllerException {
        try {
            return getDataProviderSetting().getTestCaseDataProvider().getAvailableTestCaseName(parentFolder, name);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

    public TestCaseEntity getTestCaseByScriptName(String scriptFileName) throws Exception {
        return getDataProviderSetting().getTestCaseDataProvider().getTestCaseByScriptFileName(scriptFileName);
    }

    /**
     * Get TestCaseEntity by its script file path
     * 
     * @param scriptFilePath raw location file path
     * @return TestCaseEntity
     * @throws Exception
     */
    public TestCaseEntity getTestCaseByScriptFilePath(String scriptFilePath) throws Exception {
        return getDataProviderSetting().getTestCaseDataProvider().getTestCaseByScriptFilePath(scriptFilePath);
    }

    /**
     * Find all Test Suite which is using the Test Case
     * 
     * @param testCase Test Case Entity
     * @return List of Test Suite Entity
     */
    public List<TestSuiteEntity> getTestCaseReferences(TestCaseEntity testCase) {
        try {
            return getDataProviderSetting().getTestCaseDataProvider().getTestCaseReferences(testCase);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public void reloadTestCase(TestCaseEntity testCase, Entity entity) throws Exception {
        entity = testCase = getTestCase(entity.getId());
    }

    public void loadAllDescentdantEntities(TestCaseEntity testCase) throws Exception {
        GroovyUtil.loadScriptContentIntoTestCase(testCase);
    }
}
