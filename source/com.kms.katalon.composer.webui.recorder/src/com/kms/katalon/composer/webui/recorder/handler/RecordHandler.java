package com.kms.katalon.composer.webui.recorder.handler;

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
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.TupleExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
import com.kms.katalon.composer.webui.recorder.constants.StringConstants;
import com.kms.katalon.composer.webui.recorder.dialog.RecorderDialog;
import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLFrameElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;
import com.kms.katalon.objectspy.util.HTMLElementUtil;

public class RecordHandler {

    @Inject
    private EModelService modelService;

    @Inject
    private MApplication application;

    @Inject
    private IEventBroker eventBroker;

    private Map<HTMLElement, FileEntity> entitySavedMap;

    @Inject
    private UISynchronize sync;

    @CanExecute
    public boolean canExecute() {
        if (ProjectController.getInstance().getCurrentProject() != null) {
            MPartStack composerStack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
            if (composerStack.isVisible() && composerStack.getSelectedElement() != null) {
                MPart part = (MPart) composerStack.getSelectedElement();
                if (part.getElementId().startsWith(IdConstants.TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
        try {
            MPartStack composerStack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
            MPart selectedPart = (MPart) composerStack.getSelectedElement();
            if (!selectedPart.getElementId().startsWith(IdConstants.TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX)
                    || !(selectedPart.getObject() instanceof TestCaseCompositePart)) {
                return;
            }
            final TestCaseCompositePart testCaseCompositePart = (TestCaseCompositePart) selectedPart.getObject();
            boolean isVerified = verifyTestCase(activeShell, testCaseCompositePart);
            if (!isVerified) {
                return;
            }
            final RecorderDialog recordDialog = new RecorderDialog(activeShell, LoggerSingleton.getInstance().getLogger(),
                    eventBroker);
            int responseCode = recordDialog.open();
            if (responseCode != Window.OK) {
                return;
            }
            Job job = new Job(StringConstants.JOB_GENERATE_SCRIPT_MESSAGE) {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    try {
                        monitor.beginTask(StringConstants.JOB_GENERATE_SCRIPT_MESSAGE, recordDialog.getActions().size()
                                + recordDialog.getElements().size());
                        final List<StatementWrapper> generatedStatementWrappers = generateStatementWrappersFromRecordedActions(
                                recordDialog.getActions(), recordDialog.getElements(), testCaseCompositePart.getTestCase(),
                                (FolderEntity) recordDialog.getTargetFolderTreeEntity().getObject(), monitor);
                        sync.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    testCaseCompositePart.addStatements(generatedStatementWrappers);
                                } catch (Exception e) {
                                    LoggerSingleton.logError(e);
                                }
                            }
                        });
                        eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, recordDialog.getTargetFolderTreeEntity()
                                .getParent());
                        eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, recordDialog.getTargetFolderTreeEntity());
                        eventBroker.send(EventConstants.EXPLORER_EXPAND_TREE_ENTITY, recordDialog.getTargetFolderTreeEntity());

                        return Status.OK_STATUS;
                    } catch (final Exception e) {
                        sync.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                                        StringConstants.HAND_ERROR_MSG_CANNOT_GEN_TEST_STEPS);
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
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_CANNOT_GEN_TEST_STEPS);
            LoggerSingleton.logError(e);
        }
    }

    private boolean verifyTestCase(Shell activeShell, TestCaseCompositePart testCaseCompositePart) throws Exception {
        if (testCaseCompositePart.getDirty().isDirty()) {
            MessageDialog.openError(activeShell, StringConstants.ERROR_TITLE, StringConstants.HAND_ERROR_MSG_PLS_SAVE_TEST_CASE);
            return false;
        }
        try {
            testCaseCompositePart.getAstNodesFromScript();
        } catch (CompilationFailedException compilationFailedExcption) {
            MessageDialog.openError(activeShell, StringConstants.ERROR_TITLE, StringConstants.HAND_ERROR_MSG_PLS_FIX_TEST_CASE);
            return false;
        }
        return true;
    }

    private void addRecordedElement(HTMLElement element, FolderEntity parentFolder, WebElementEntity refElement) throws Exception {
        WebElementEntity importedElement = ObjectRepositoryController.getInstance().importWebElement(
                HTMLElementUtil.convertElementToWebElementEntity(element, refElement, parentFolder), parentFolder);
        entitySavedMap.put(element, importedElement);
        if (element instanceof HTMLFrameElement) {
            for (HTMLElement childElement : ((HTMLFrameElement) element).getChildElements()) {
                addRecordedElement(childElement, parentFolder, importedElement);
            }
        }
    }

    private void addRecordedElements(List<HTMLPageElement> recordedElements, FolderEntity parentFolder, IProgressMonitor monitor)
            throws Exception {
        entitySavedMap = new HashMap<HTMLElement, FileEntity>();
        for (HTMLPageElement pageElement : recordedElements) {
            FolderEntity importedFolder = ObjectRepositoryController.getInstance().importWebElementFolder(
                    HTMLElementUtil.convertPageElementToFolderEntity(pageElement, parentFolder), parentFolder);
            entitySavedMap.put(pageElement, importedFolder);
            for (HTMLElement childElement : pageElement.getChildElements()) {
                addRecordedElement(childElement, (importedFolder != null) ? importedFolder : parentFolder, null);
            }
            monitor.worked(1);
        }
    }

    private List<StatementWrapper> generateStatementWrappersFromRecordedActions(List<HTMLActionMapping> recordedActions,
            List<HTMLPageElement> recordedElements, TestCaseEntity selectedTestCase, FolderEntity targetFolder,
            IProgressMonitor monitor) throws Exception {

        monitor.subTask(StringConstants.JOB_ADDING_OBJECT);
        addRecordedElements(recordedElements, targetFolder, monitor);

        monitor.subTask(StringConstants.JOB_GENERATE_STATEMENT_MESSAGE);
        List<StatementWrapper> resultStatementWrappers = new ArrayList<StatementWrapper>();

        // add open browser keyword
        MethodCallExpressionWrapper methodCallExpressionWrapper = new MethodCallExpressionWrapper(
                WebUiBuiltInKeywords.class.getSimpleName(), "openBrowser", null);
        List<ExpressionWrapper> arguments = ((TupleExpressionWrapper) methodCallExpressionWrapper.getArguments())
                .getExpressions();
        arguments.add(new ConstantExpressionWrapper("", methodCallExpressionWrapper.getArguments()));
        arguments.add(AstKeywordsInputUtil.getNewFailureHandlingPropertyExpression(methodCallExpressionWrapper.getArguments()));

        resultStatementWrappers.add(new ExpressionStatementWrapper(methodCallExpressionWrapper, null));

        // add switch to window keyword if action in another window
        recordedActions = addSwitchToWindowKeyword(recordedActions);

        for (HTMLActionMapping action : recordedActions) {
            WebElementEntity createdTestObject = null;
            if (action.getTargetElement() != null && entitySavedMap.get(action.getTargetElement()) instanceof WebElementEntity) {
                createdTestObject = (WebElementEntity) entitySavedMap.get(action.getTargetElement());
            }
            StatementWrapper generatedStatementWrapper = HTMLActionUtil.generateWebUiTestStep(action, createdTestObject);
            if (generatedStatementWrapper != null) {
                resultStatementWrappers.add(generatedStatementWrapper);
            }
            monitor.worked(1);
        }

        // add close browser keyword
        methodCallExpressionWrapper = new MethodCallExpressionWrapper(WebUiBuiltInKeywords.class.getSimpleName(), "closeBrowser",
                null);
        arguments = ((TupleExpressionWrapper) methodCallExpressionWrapper.getArguments()).getExpressions();
        arguments.add(AstKeywordsInputUtil.getNewFailureHandlingPropertyExpression(methodCallExpressionWrapper.getArguments()));
        resultStatementWrappers.add(new ExpressionStatementWrapper(methodCallExpressionWrapper, null));

        return resultStatementWrappers;
    }

    private List<HTMLActionMapping> addSwitchToWindowKeyword(List<HTMLActionMapping> recordedActions) {
        List<HTMLActionMapping> newActions = new ArrayList<HTMLActionMapping>();
        String currentWindowId = null;
        for (HTMLActionMapping action : recordedActions) {
            String newId = action.getWindowId();
            if (newId != null) {
                if (currentWindowId == null) {
                    currentWindowId = newId;
                } else if (!newId.equals(currentWindowId)) {
                    HTMLActionMapping switchToWindowAction = HTMLActionUtil.createNewSwitchToWindowAction(HTMLActionUtil
                            .getPageTitleForAction(action));
                    newActions.add(switchToWindowAction);
                    currentWindowId = newId;
                }
            } else {
                currentWindowId = "";
            }
            newActions.add(action);
        }
        return newActions;
    }
}
