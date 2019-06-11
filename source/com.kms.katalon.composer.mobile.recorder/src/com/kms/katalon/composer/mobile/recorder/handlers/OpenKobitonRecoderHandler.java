package com.kms.katalon.composer.mobile.recorder.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.mobile.objectspy.actions.MobileActionMapping;
import com.kms.katalon.composer.mobile.objectspy.components.KobitonAppComposite;
import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.composer.mobile.objectspy.util.MobileActionUtil;
import com.kms.katalon.composer.mobile.recorder.components.MobileRecorderDialog;
import com.kms.katalon.composer.mobile.recorder.constants.MobileRecoderMessagesConstants;
import com.kms.katalon.composer.mobile.recorder.constants.MobileRecorderStringConstants;
import com.kms.katalon.composer.mobile.recorder.utils.MobileElementConverter;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.handlers.NewTestCaseHandler;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.tracking.service.Trackings;

public class OpenKobitonRecoderHandler {
    private MobileRecorderDialog recorderDialog;

    private Shell activeShell;

    @Inject
    private MApplication application;

    @Inject
    private EModelService modelService;

    @Inject
    private IEventBroker eventBroker;

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
        openRecorderDialog(activeShell);
    }

    private boolean openRecorderDialog(Shell activeShell) {
        try {
            if (this.activeShell == null) {
                this.activeShell = activeShell;
            }
            TestCaseCompositePart testCaseCompositePart = getSelectedTestCasePart();
            if (testCaseCompositePart != null && !verifyTestCase(testCaseCompositePart)) {
                return false;
            }
            recorderDialog = new MobileRecorderDialog(activeShell, 
                    new KobitonAppComposite());
            Trackings.trackOpenMobileRecord();
            if (recorderDialog.open() != Window.OK) {
                return false;
            }
            if (testCaseCompositePart == null) {
                testCaseCompositePart = createNewTestCase();
            }
            exportRecordedActionsToScripts(recorderDialog.getRecordedActions(), recorderDialog.getTargetFolderEntity(),
                    recorderDialog.getCurrentMobileDriverType(), testCaseCompositePart);
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(activeShell, MobileRecorderStringConstants.ERROR, e.getMessage());
            return false;
        }
    }

    private TestCaseCompositePart createNewTestCase() throws Exception {
        TestCaseEntity testCase = NewTestCaseHandler.doCreateNewTestCase(
                new FolderTreeEntity(FolderController.getInstance()
                        .getTestCaseRoot(ProjectController.getInstance().getCurrentProject()), null),
                EventBrokerSingleton.getInstance().getEventBroker());
        if (testCase == null) {
            return null;
        }

        return getTestCasePartByTestCase(testCase);
    }

    private TestCaseCompositePart getTestCasePartByTestCase(TestCaseEntity testCase) throws Exception {
        MPart selectedPart = (MPart) modelService.find(EntityPartUtil.getTestCaseCompositePartId(testCase.getId()),
                application);
        if (selectedPart == null || !(selectedPart.getObject() instanceof TestCaseCompositePart)) {
            return null;
        }
        return (TestCaseCompositePart) selectedPart.getObject();
    }

    private TestCaseCompositePart getSelectedTestCasePart() {
        MPart selectedPart = getSelectedPart();
        if (selectedPart == null || !(selectedPart.getObject() instanceof TestCaseCompositePart)) {
            return null;
        }
        final TestCaseCompositePart testCaseCompositePart = (TestCaseCompositePart) selectedPart.getObject();
        return testCaseCompositePart;
    }

    private void exportRecordedActionsToScripts(List<MobileActionMapping> recordedActions,
            FolderTreeEntity targetFolderTreeEntity, MobileDriverType mobileDriverType,
            TestCaseCompositePart testCaseCompositePart) {
        if (testCaseCompositePart == null) {
            return;
        }
        Job job = new Job(MobileRecoderMessagesConstants.MSG_TASK_GENERATE_SCRIPT) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    final TestCasePart testCasePart = testCaseCompositePart.getChildTestCasePart();
                    final List<StatementWrapper> generatedStatementWrappers = generateStatementWrappersFromRecordedActions(
                            recordedActions, testCasePart, targetFolderTreeEntity, mobileDriverType, monitor);
                    UISynchronizeService.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                testCasePart.addDefaultImports();
                                testCasePart.addStatements(generatedStatementWrappers, NodeAddType.InserAfter);
                                testCaseCompositePart.refreshScript();
                                testCaseCompositePart.save();
                            } catch (Exception e) {
                                LoggerSingleton.logError(e);
                            }
                        }
                    });

                    eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, targetFolderTreeEntity.getParent());
                    eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, targetFolderTreeEntity);
                    eventBroker.send(EventConstants.EXPLORER_EXPAND_TREE_ENTITY, targetFolderTreeEntity);

                    return Status.OK_STATUS;
                } catch (final Exception e) {
                    UISynchronizeService.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            MessageDialog.openError(Display.getCurrent().getActiveShell(),
                                    MobileRecorderStringConstants.ERROR,
                                    MobileRecoderMessagesConstants.MSG_ERR_CANNOT_GENERATE_TEST_STEPS);
                            LoggerSingleton.logError(e);
                        }
                    });
                    LoggerSingleton.logError(e);
                    return Status.CANCEL_STATUS;
                } finally {
                    monitor.done();
                }
            }
        };

        job.setUser(true);
        job.schedule();
    }

    private boolean verifyTestCase(TestCaseCompositePart testCaseCompositePart) throws Exception {
        if (testCaseCompositePart.getDirty().isDirty()) {
            if (!MessageDialog.openConfirm(activeShell, MobileRecorderStringConstants.WARN,
                    MobileRecoderMessagesConstants.MSG_ERR_TEST_CASE_HAVE_UNSAVE_CHANGES)) {
                return false;
            }
            testCaseCompositePart.save();
        }
        try {
            testCaseCompositePart.getAstNodesFromScript();
        } catch (CompilationFailedException compilationFailedExcption) {
            MessageDialog.openWarning(activeShell, MobileRecorderStringConstants.WARN,
                    MobileRecoderMessagesConstants.MSG_ERR_TEST_CASE_HAVE_ERRORS);
            return false;
        }
        return true;
    }

    private WebElementEntity addRecordedElement(MobileElement element, FolderEntity parentFolder,
            MobileDriverType mobileDriverType, Map<MobileElement, WebElementEntity> entitySavedMap) throws Exception {
        if (element == null) {
            return null;
        }
        if (entitySavedMap != null && entitySavedMap.get(element) != null) {
            return entitySavedMap.get(element);
        }
        WebElementEntity importedElement = ObjectRepositoryController.getInstance().importWebElement(
                new MobileElementConverter().convert(element, parentFolder, mobileDriverType), parentFolder);
        entitySavedMap.put(element, importedElement);
        return importedElement;
    }

    private List<StatementWrapper> generateStatementWrappersFromRecordedActions(
            List<MobileActionMapping> recordedActions, TestCasePart testCasePart,
            FolderTreeEntity folderSelectionResult, MobileDriverType mobileDriverType, IProgressMonitor monitor)
            throws Exception {
        Map<MobileElement, WebElementEntity> entitySavedMap = new HashMap<>();
        FolderEntity targetFolder = folderSelectionResult.getObject();

        monitor.beginTask(MobileRecoderMessagesConstants.MSG_TASK_GENERATE_SCRIPT, recordedActions.size());

        ASTNodeWrapper mainClassNode = testCasePart.getTreeTableInput().getMainClassNode();
        List<StatementWrapper> resultStatementWrappers = new ArrayList<StatementWrapper>();
        for (MobileActionMapping action : recordedActions) {
            WebElementEntity createdTestObject = addRecordedElement(action.getTargetElement(), targetFolder,
                    mobileDriverType, entitySavedMap);
            StatementWrapper generatedStatementWrapper = MobileActionUtil.generateMobileTestStep(action,
                    createdTestObject, mainClassNode);
            monitor.worked(1);
            resultStatementWrappers.add(generatedStatementWrapper);
        }
        return resultStatementWrappers;
    }

    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    protected MPart getSelectedPart() {
        MPartStack composerStack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
                application);
        if (!composerStack.isVisible() || !(composerStack.getSelectedElement() instanceof MPart)) {
            return null;
        }
        return (MPart) composerStack.getSelectedElement();
    }
}
