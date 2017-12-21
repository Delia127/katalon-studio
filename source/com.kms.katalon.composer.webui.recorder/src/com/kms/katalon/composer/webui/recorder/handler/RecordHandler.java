package com.kms.katalon.composer.webui.recorder.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.openqa.selenium.Keys;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.handlers.NewTestCaseHandler;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
import com.kms.katalon.composer.webui.recorder.constants.StringConstants;
import com.kms.katalon.composer.webui.recorder.dialog.RecorderDialog;
import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.objectspy.dialog.SaveToObjectRepositoryDialog.SaveToObjectRepositoryDialogResult;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebFrame;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.objectspy.util.WebElementUtils;

public class RecordHandler {

    @Inject
    private EModelService modelService;

    @Inject
    private MApplication application;

    @Inject
    private IEventBroker eventBroker;

    private Map<WebElement, FileEntity> entitySavedMap;

    @Inject
    private UISynchronize sync;

    private RecorderDialog recordDialog;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.KATALON_RECORD, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                if (!canExecute()) {
                    return;
                }
                execute();
            }
        });
    }

    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute() {
        Shell shell = null;
        try {
            TestCaseCompositePart testCaseCompositePart = getSelectedTestCasePart();
            if (testCaseCompositePart != null && !verifyTestCase(testCaseCompositePart)) {
                return;
            }
            if (recordDialog == null || recordDialog.isDisposed()) {
                shell = getShell(Display.getCurrent().getActiveShell());
                recordDialog = new RecorderDialog(shell, LoggerSingleton.getInstance().getLogger(), eventBroker);
            }

            int responseCode = recordDialog.open();
            if (responseCode != Window.OK) {
                return;
            }
            final SaveToObjectRepositoryDialogResult folderSelectionResult = recordDialog.getTargetFolderTreeEntity();
            final List<HTMLActionMapping> recordedActions = recordDialog.getActions();
            final List<WebPage> recordedElements = recordDialog.getElements();
            if (testCaseCompositePart == null) {
                testCaseCompositePart = createNewTestCase();
            }
            doGenerateTestScripts(testCaseCompositePart, folderSelectionResult, recordedActions, recordedElements);
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_CANNOT_GEN_TEST_STEPS);
            LoggerSingleton.logError(e);
        } finally {
             if (shell != null && !shell.isDisposed()) {
                 shell.dispose();
             }
        }
    }
    
    private Shell getShell(Shell activeShell) {
        if (Platform.OS_WIN32.equals(Platform.getOS())) {
            return null;
        }
        Shell shell = new Shell();
        Rectangle activeShellSize = activeShell.getBounds();
        shell.setLocation((activeShellSize.width - shell.getBounds().width) / 2, (activeShellSize.height - shell.getBounds().height) / 2);
        return shell;
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

    private void doGenerateTestScripts(final TestCaseCompositePart testCaseCompositePart,
            final SaveToObjectRepositoryDialogResult folderSelectionResult,
            final List<HTMLActionMapping> recordedActions, final List<WebPage> recordedElements) {
        if (testCaseCompositePart == null) {
            return;
        }
        final TestCasePart testCasePart = testCaseCompositePart.getChildTestCasePart();
        Job job = new Job(StringConstants.JOB_GENERATE_SCRIPT_MESSAGE) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    monitor.beginTask(StringConstants.JOB_GENERATE_SCRIPT_MESSAGE,
                            recordedActions.size() + recordedElements.size());
                    final List<StatementWrapper> generatedStatementWrappers = generateStatementWrappersFromRecordedActions(
                            recordedActions, recordedElements, testCasePart, folderSelectionResult, monitor);
                    sync.syncExec(new Runnable() {
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

                    FolderTreeEntity targetFolderTreeEntity = folderSelectionResult.getSelectedParentFolder();
                    eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, targetFolderTreeEntity.getParent());
                    eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, targetFolderTreeEntity);
                    eventBroker.send(EventConstants.EXPLORER_EXPAND_TREE_ENTITY, targetFolderTreeEntity);

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
    }

    private TestCaseCompositePart getSelectedTestCasePart() throws Exception {
        MPartStack composerStack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
                application);
        MPart selectedPart = (MPart) composerStack.getSelectedElement();
        if (selectedPart == null
                || !selectedPart.getElementId().startsWith(IdConstants.TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX)
                || !(selectedPart.getObject() instanceof TestCaseCompositePart)) {
            return null;
        }
        return (TestCaseCompositePart) selectedPart.getObject();
    }

    private TestCaseCompositePart getTestCasePartByTestCase(TestCaseEntity testCase) throws Exception {
        MPart selectedPart = (MPart) modelService.find(EntityPartUtil.getTestCaseCompositePartId(testCase.getId()),
                application);
        if (selectedPart == null || !(selectedPart.getObject() instanceof TestCaseCompositePart)) {
            return null;
        }
        return (TestCaseCompositePart) selectedPart.getObject();
    }

    private boolean verifyTestCase(TestCaseCompositePart testCaseCompositePart) throws Exception {
        Shell activeShell = Display.getCurrent().getActiveShell();
        if (testCaseCompositePart.getDirty().isDirty()) {
            if (!MessageDialog.openConfirm(activeShell, StringConstants.WARN,
                    StringConstants.HAND_ERROR_MSG_PLS_SAVE_TEST_CASE)) {
                return false;
            }
            testCaseCompositePart.save();
        }
        try {
            testCaseCompositePart.getAstNodesFromScript();
        } catch (CompilationFailedException compilationFailedExcption) {
            MessageDialog.openWarning(activeShell, StringConstants.WARN,
                    StringConstants.HAND_ERROR_MSG_PLS_FIX_TEST_CASE);
            return false;
        }
        return true;
    }

    private void addRecordedElement(WebElement element, FolderEntity parentFolder, WebElementEntity refElement)
            throws Exception {
        WebElementEntity importedElement = ObjectRepositoryController.getInstance().importWebElement(
                WebElementUtils.convertWebElementToTestObject(element, refElement, parentFolder), parentFolder);
        entitySavedMap.put(element, importedElement);
        if (element instanceof WebFrame) {
            for (WebElement childElement : ((WebFrame) element).getChildren()) {
                addRecordedElement(childElement, parentFolder, importedElement);
            }
        }
    }

    private void addRecordedElements(List<WebPage> recordedElements,
            SaveToObjectRepositoryDialogResult folderSelectionResult, IProgressMonitor monitor) throws Exception {
        entitySavedMap = new HashMap<>();
        for (WebElement pageElement : recordedElements) {
            FolderEntity importedFolder = folderSelectionResult.createFolderForPageElement((WebPage) pageElement);
            entitySavedMap.put(pageElement, importedFolder);
            for (WebElement childElement : ((WebFrame) pageElement).getChildren()) {
                addRecordedElement(childElement, importedFolder, null);
            }
            monitor.worked(1);
        }
    }

    private List<StatementWrapper> generateStatementWrappersFromRecordedActions(List<HTMLActionMapping> recordedActions,
            List<WebPage> recordedElements, TestCasePart testCasePart,
            SaveToObjectRepositoryDialogResult folderSelectionResult, IProgressMonitor monitor) throws Exception {
        monitor.subTask(StringConstants.JOB_ADDING_OBJECT);
        addRecordedElements(recordedElements, folderSelectionResult, monitor);

        monitor.subTask(StringConstants.JOB_GENERATE_STATEMENT_MESSAGE);
        List<StatementWrapper> resultStatementWrappers = new ArrayList<StatementWrapper>();

        ScriptNodeWrapper mainClassNode = testCasePart.getTreeTableInput().getMainClassNode();
        
        addAdditionalImports(mainClassNode);
        // add open browser keyword
        String webUiKwAliasName = HTMLActionUtil.getWebUiKeywordClass().getAliasName();
        MethodCallExpressionWrapper methodCallExpressionWrapper = new MethodCallExpressionWrapper(webUiKwAliasName,
                "openBrowser", mainClassNode);
        ArgumentListExpressionWrapper arguments = methodCallExpressionWrapper.getArguments();
        arguments.addExpression(new ConstantExpressionWrapper(""));

        resultStatementWrappers.add(new ExpressionStatementWrapper(methodCallExpressionWrapper));

        // add switch to window keyword if action in another window
        recordedActions = addSwitchToWindowKeyword(recordedActions);

        for (HTMLActionMapping action : recordedActions) {
            WebElementEntity createdTestObject = null;
            if (action.getTargetElement() != null
                    && entitySavedMap.get(action.getTargetElement()) instanceof WebElementEntity) {
                createdTestObject = (WebElementEntity) entitySavedMap.get(action.getTargetElement());
            }
            StatementWrapper generatedStatementWrapper = HTMLActionUtil.generateWebUiTestStep(action, createdTestObject,
                    mainClassNode);
            if (generatedStatementWrapper != null) {
                resultStatementWrappers.add(generatedStatementWrapper);
            }
            monitor.worked(1);
        }

        // add close browser keyword
        methodCallExpressionWrapper = new MethodCallExpressionWrapper(webUiKwAliasName, "closeBrowser", mainClassNode);
        arguments = methodCallExpressionWrapper.getArguments();
        resultStatementWrappers.add(new ExpressionStatementWrapper(methodCallExpressionWrapper));

        return resultStatementWrappers;
    }

    private void addAdditionalImports(ScriptNodeWrapper mainClassNode) {
        mainClassNode.addImport(Keys.class);
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
                    HTMLActionMapping switchToWindowAction = HTMLActionUtil
                            .createNewSwitchToWindowAction(HTMLActionUtil.getPageTitleForAction(action));
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
