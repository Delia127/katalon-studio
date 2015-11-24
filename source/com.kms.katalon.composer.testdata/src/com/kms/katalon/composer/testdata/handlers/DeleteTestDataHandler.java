package com.kms.katalon.composer.testdata.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityHandler;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
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
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;

public class DeleteTestDataHandler extends AbstractDeleteReferredEntityHandler implements IDeleteEntityHandler {

    @Inject
    private IEventBroker eventBroker;

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

            final DataFileEntity testData = (DataFileEntity) entity.getObject();

            if (testData == null) {
                return false;
            }

            String taskName = "Deleting " + entity.getTypeName() + " '" + entity.getText() + "'...";
            monitor.beginTask(taskName, 1);

            UISynchronize sync = UISynchronizeService.getInstance().getSync();

            final Map<String, List<TestSuiteTestCaseLink>> testDataReferences = TestDataController.getInstance()
                    .getTestDataReferences(testData);

            if (testDataReferences.values().size() > 0) {
                if (!canDelete()) {
                    if (!needToShowPreferenceDialog()) {
                        return false;
                    }

                    final AbstractDeleteReferredEntityHandler handler = this;

                    sync.syncExec(new Runnable() {

                        @Override
                        public void run() {
                            TestDataReferencesDialog dialog = new TestDataReferencesDialog(Display.getCurrent()
                                    .getActiveShell(), testData, testDataReferences, handler);
                            dialog.open();
                        }
                    });

                    if (canDelete()) {
                        deleteTestDataReferences(testData, testDataReferences, eventBroker);
                    } else {
                        return false;
                    }
                }

            }

            final String testDataId = TestDataController.getInstance().getIdForDisplay(testData);
            sync.syncExec(new Runnable() {

                @Override
                public void run() {
                    try {
                        // Find all Test Case script which has relationship with the Test Data
                        List<IFile> affectedTestCaseScripts = GroovyRefreshUtil.findReferencesInTestCaseScripts(
                                testDataId, testData.getProject());
                        // Give a confirmation message for current Test Data to remove its references
                        if (!affectedTestCaseScripts.isEmpty()) {
                            // Depend on the user response, references will be removed or not
                            boolean isRemovingRef = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                                    StringConstants.HAND_TITLE_DELETE, StringConstants.HAND_MSG_REMOVE_ENTITY_REF);
                            if (isRemovingRef) {
                                GroovyRefreshUtil
                                        .removeReferencesInTestCaseScripts(testDataId, affectedTestCaseScripts);
                            }
                        }
                    } catch (CoreException | IOException e) {
                        LoggerSingleton.logError(e);
                    }
                }
            });

            // remove TestCase part from its partStack if it exists
            EntityPartUtil.closePart(testData);

            TestDataController.getInstance().deleteDataFile(testData);

            eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, testDataId);
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_DATA);
            return false;
        } finally {
            monitor.done();
        }
    }

    public static void deleteTestDataReferences(DataFileEntity dataFileEntity,
            Map<String, List<TestSuiteTestCaseLink>> testDataReferences, IEventBroker eventBroker) throws Exception {
        final String dataFileId = TestDataController.getInstance().getIdForDisplay(dataFileEntity);

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

                        variableLink.setTestDataLinkId("");
                        variableLink.setType(VariableType.SCRIPT);
                        variableLink.setValue("");
                    }
                }

                testCaseLink.getTestDataLinks().removeAll(removedTestDataLinks);
            }

            TestSuiteEntity testSuite = TestSuiteController.getInstance().getTestSuiteByDisplayId(entry.getKey(),
                    ProjectController.getInstance().getCurrentProject());
            TestSuiteController.getInstance().updateTestSuite(testSuite);
            eventBroker.post(EventConstants.TEST_SUITE_UPDATED, new Object[] { testSuite.getId(), testSuite });
        }
    }

}
