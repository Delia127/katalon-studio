package com.kms.katalon.composer.testdata.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.composer.testcase.util.TestCaseEntityUtil.getTestCaseEntities;
import static com.kms.katalon.groovy.util.GroovyRefreshUtil.findReferencesInTestCaseScripts;
import static com.kms.katalon.groovy.util.GroovyRefreshUtil.removeReferencesInTestCaseScripts;
import static java.text.MessageFormat.format;
import static org.eclipse.jface.dialogs.MessageDialog.openError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityHandler;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.composer.testdata.dialog.TestDataReferencesDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class DeleteTestDataHandler extends AbstractDeleteReferredEntityHandler {

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return TestDataTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity entity, IProgressMonitor monitor) {
        try {
            if (entity == null || !(entity instanceof TestDataTreeEntity)) {
                return false;
            }

            DataFileEntity testData = (DataFileEntity) entity.getObject();

            if (testData == null) {
                return false;
            }

            monitor.beginTask(
                    format(StringConstants.HAND_JOB_DELETING_ENTITY_W_TYPE_NAME, entity.getTypeName(), entity.getText()),
                    1);

            String testDataId = testData.getIdForDisplay();
            if (performDeleteTestData(testData, findReferencesInTestCaseScripts(testDataId, testData.getProject()))) {
                eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, testDataId);
                return true;
            }
        } catch (Exception e) {
            logError(e);
            openError(null, StringConstants.ERROR_TITLE, StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_DATA);
        } finally {
            monitor.done();
        }
        return false;
    }

    protected boolean performDeleteTestData(final DataFileEntity testData, final List<IFile> affectedTestCaseScripts) {
        isDeleted = false;
        sync.syncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    String testDataId = testData.getIdForDisplay();
                    TestDataController tdController = TestDataController.getInstance();
                    Map<String, List<TestSuiteTestCaseLink>> referencesInTestSuite = tdController.getTestDataReferences(testData);

                    // Find all Test Case script which has relationship with the Test Data
                    List<TestCaseEntity> affectedTestCases = getTestCaseEntities(affectedTestCaseScripts);

                    if (!referencesInTestSuite.values().isEmpty() || !affectedTestCases.isEmpty()) {
                        if (isDefaultResponse()) {
                            TestDataReferencesDialog dialog = new TestDataReferencesDialog(null, testDataId,
                                    referencesInTestSuite, affectedTestCases, needYesNoToAllButtons());
                            setResponse(dialog.open());
                        }

                        if (isCancelResponse()) {
                            return;
                        }

                        if (isYesResponse()) {
                            // remove test data references in test suite
                            removeReferencesInTestSuites(testData, referencesInTestSuite);

                            // remove test data references in test case script
                            removeReferencesInTestCaseScripts(testDataId, affectedTestCaseScripts);
                        }
                    }

                    // remove TestCase part from its partStack if it exists
                    EntityPartUtil.closePart(testData);

                    // remove Test Data
                    tdController.deleteDataFile(testData);

                    if (!isYesNoToAllResponse()) {
                        resetResponse();
                    }

                    isDeleted = true;
                } catch (Exception e) {
                    logError(e);
                }
            }
        });
        return isDeleted;
    }

    protected void removeReferencesInTestSuites(DataFileEntity dataFileEntity,
            Map<String, List<TestSuiteTestCaseLink>> testDataReferences) throws Exception {
        String dataFileId = dataFileEntity.getIdForDisplay();
        for (Entry<String, List<TestSuiteTestCaseLink>> entry : testDataReferences.entrySet()) {
            for (TestSuiteTestCaseLink testCaseLink : entry.getValue()) {
                List<TestCaseTestDataLink> removedTestDataLinks = new ArrayList<TestCaseTestDataLink>();
                for (TestCaseTestDataLink testDataLink : testCaseLink.getTestDataLinks()) {
                    if (!dataFileId.equals(testDataLink.getTestDataId())) {
                        continue;
                    }
                    removedTestDataLinks.add(testDataLink);

                    for (VariableLink variableLink : testCaseLink.getVariableLinks()) {
                        if (!testDataLink.getId().equals(variableLink.getTestDataLinkId())) {
                            continue;
                        }

                        variableLink.setTestDataLinkId(StringConstants.EMPTY);
                        variableLink.setType(VariableType.SCRIPT);
                        variableLink.setValue(StringConstants.EMPTY);
                    }
                }

                testCaseLink.getTestDataLinks().removeAll(removedTestDataLinks);
            }

            TestSuiteController tsController = TestSuiteController.getInstance();
            TestSuiteEntity testSuite = tsController.getTestSuiteByDisplayId(entry.getKey(),
                    ProjectController.getInstance().getCurrentProject());
            tsController.updateTestSuite(testSuite);
            eventBroker.post(EventConstants.TEST_SUITE_UPDATED, new Object[] { testSuite.getId(), testSuite });
        }
    }
}
