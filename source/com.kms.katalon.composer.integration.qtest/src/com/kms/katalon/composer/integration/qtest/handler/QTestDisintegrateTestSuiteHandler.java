package com.kms.katalon.composer.integration.qtest.handler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;

public class QTestDisintegrateTestSuiteHandler extends AbstractQTestHandler {
    @Inject
    private IEventBroker eventBroker;

    @Inject
    private ESelectionService selectionService;

    //Represents a list of test suite that each one can be dis-integrated.
    private List<TestSuiteEntity> fTestSuites;

    /**
     * @return true if the selected test suite has at least one {@link QTestSuite} that had been uploaded. Otherwise,
     *         false.
     */
    @CanExecute
    public boolean canExecute() {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();

            Object selectedEntity = getFirstSelectedObject(selectionService);
            if (selectedEntity == null) {
                return false;
            }

            // Check if test suite is in any TestSuiteRepo or not.
            if (!QTestIntegrationUtil.canBeDownloadedOrDisintegrated((IntegratedFileEntity) selectedEntity,
                    projectEntity)) {
                return false;
            }

            fTestSuites = new ArrayList<TestSuiteEntity>();
            if (selectedEntity instanceof TestSuiteEntity) {
                if (getUploadedQTestSuites((TestSuiteEntity) selectedEntity).size() > 0) {
                    fTestSuites.add((TestSuiteEntity) selectedEntity);
                } else {
                    return false;
                }
            } else if (selectedEntity instanceof FolderEntity) {
                FolderEntity folderEntity = (FolderEntity) selectedEntity;
                if (folderEntity.getFolderType() != FolderType.TESTSUITE) {
                    return false;
                }

                for (Object childObject : FolderController.getInstance().getAllDescentdantEntities(folderEntity)) {
                    if (childObject instanceof TestSuiteEntity
                            && getUploadedQTestSuites((TestSuiteEntity) childObject).size() > 0) {
                        fTestSuites.add((TestSuiteEntity) childObject);
                    }
                }
            }

            return fTestSuites.size() > 0;

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    /**
     * Removes all {@link QTestSuite} in the current <code>testSuite</code>
     */
    @Execute
    public void execute() {
        if (fTestSuites == null) {
            return;
        }

        if (!MessageDialog.openConfirm(null, StringConstants.CONFIRMATION,
                StringConstants.VIEW_CONFIRM_DISINTEGRATE_TEST_SUITE)) {
            return;
        }
        
        for (TestSuiteEntity testSuite : fTestSuites) {
            String testSuiteId = null;
            try {
                testSuiteId = TestSuiteController.getInstance().getIdForDisplay(testSuite);
                List<QTestSuite> qTestSuites = QTestIntegrationTestSuiteManager
                        .getQTestSuiteListByIntegratedEntity(QTestIntegrationUtil.getIntegratedEntity(testSuite));

                for (QTestSuite availableQTestSuite : qTestSuites) {
                    if (availableQTestSuite.getId() > 0) {
                        availableQTestSuite.setId(0);
                        availableQTestSuite.setPid("");
                        availableQTestSuite.setSelected(false);
                        availableQTestSuite.getTestRuns().clear();
                    }
                }

                QTestIntegrationUtil.updateFileIntegratedEntity(testSuite,
                        QTestIntegrationTestSuiteManager.getIntegratedEntityByTestSuiteList(qTestSuites));
                TestSuiteController.getInstance().updateTestSuite(testSuite);
                eventBroker.post(EventConstants.TEST_SUITE_UPDATED, new Object[] { testSuite.getId(), testSuite });
            } catch (QTestInvalidFormatException e) {
                MessageDialog.openError(null, StringConstants.WARN,
                        MessageFormat.format(StringConstants.JOB_MSG_TEST_SUITE_INVALID_FORMAT, testSuiteId));
            } catch (Exception e) {
                LoggerSingleton.logError(e);
                return;
            }
        }
    }

    /**
     * Returns a list of {@link QTestSuite} that each item has been uploaded of a {@link TestSuiteEntity}.
     * 
     * @param testSuite
     * @return
     * @throws QTestInvalidFormatException
     *             : thrown if the test suite is invalid qTest integrated information.
     */
    private List<QTestSuite> getUploadedQTestSuites(TestSuiteEntity testSuite) throws QTestInvalidFormatException {
        List<QTestSuite> qTestSuites = QTestIntegrationTestSuiteManager
                .getQTestSuiteListByIntegratedEntity(QTestIntegrationUtil.getIntegratedEntity(testSuite));
        List<QTestSuite> uploadedQTestSuites = new ArrayList<QTestSuite>();

        for (QTestSuite availableQTestSuite : qTestSuites) {
            if (availableQTestSuite.getId() > 0) {
                uploadedQTestSuites.add(availableQTestSuite);
            }
        }

        return uploadedQTestSuites;
    }
}
