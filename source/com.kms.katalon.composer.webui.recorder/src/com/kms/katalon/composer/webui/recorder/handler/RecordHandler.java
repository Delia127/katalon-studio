package com.kms.katalon.composer.webui.recorder.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
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
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.handlers.NewTestCaseHandler;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
import com.kms.katalon.composer.webui.recorder.constants.StringConstants;
import com.kms.katalon.composer.webui.recorder.dialog.RecorderDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.objectspy.dialog.SaveToObjectRepositoryDialog.SaveToObjectRepositoryDialogResult;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebFrame;
import com.kms.katalon.objectspy.element.WebPage;

public class RecordHandler {

    @Inject
    private EModelService modelService;

    @Inject
    private MApplication application;

    @Inject
    private IEventBroker eventBroker;

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
        Shell activeShell = Display.getCurrent().getActiveShell();
        if (activeShell == null) {
            return;
        }
        try {
            TestCaseCompositePart testCaseCompositePart = getSelectedTestCasePart();
            if (testCaseCompositePart != null && !verifyTestCase(testCaseCompositePart)) {
                return;
            }

            List<? extends ASTNodeWrapper> wrapper = new ArrayList<>();
            List<VariableEntity> variables = new ArrayList<>();
            TestCaseEntity testCaseEntity = null;
            if (testCaseCompositePart != null) {
                String scriptContent = StringUtils.defaultString(testCaseCompositePart.getScriptContent());

                wrapper = GroovyWrapperParser.parseGroovyScriptIntoNodeWrapper(scriptContent)
                        .getRunMethod()
                        .getBlock()
                        .getAstChildren();
                testCaseEntity = testCaseCompositePart.getTestCase();
                variables = testCaseCompositePart.getTestCase().clone().getVariables();
            }
            if (recordDialog == null || recordDialog.isDisposed()) {
                shell = getShell(activeShell);
                recordDialog = new RecorderDialog(shell, testCaseEntity, wrapper, variables);
            } else {
                recordDialog.getShell().forceActive();
                return;
            }
            int responseCode = recordDialog.open();
            if (responseCode != Window.OK) {
                return;
            }
            final SaveToObjectRepositoryDialogResult folderSelectionResult = recordDialog.getTargetFolderTreeEntity();
            final List<HTMLActionMapping> recordedActions = recordDialog.getActions();
            final List<WebPage> recordedElements = recordDialog.getElements();
            boolean shouldOverride = true;
            if (recordedActions.isEmpty()) {
                return;
            }
            if (testCaseCompositePart == null || testCaseCompositePart.isDisposed()) {
                testCaseCompositePart = createNewTestCase();
                shouldOverride = false;
            }
            updateRecordedElementsAfterSavingToObjectRepository(recordedElements,
                    folderSelectionResult != null ? folderSelectionResult.getEntitySavedMap() : Collections.emptyMap());
            doGenerateTestScripts(testCaseCompositePart, folderSelectionResult, recordedActions, recordedElements,
                    recordDialog.getScriptWrapper(), recordDialog.getVariables(), shouldOverride);
        } catch (Exception e) {
            MessageDialog.openError(activeShell, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_CANNOT_GEN_TEST_STEPS);
            LoggerSingleton.logError(e);
        } finally {
            if (shell != null && !shell.isDisposed()) {
                shell.dispose();
            }
        }
    }

    /**
     * Update the structure of recorded elements with the structures decided by user
     * via entitySavedMap. Cloned WebPages will be created in recordedElements
     * and references of their children are removed from the original WebPages
     * 
     * @see com.kms.katalon.objectspy.dialog.ObjectRepositoryService#saveObject(SaveToObjectRepositoryDialogResult)
     * @param recordedElements The list of WebPages and their children of a test case
     * @param entitySavedMap A map of physical folders indexed by WebPage and WebEntity
     */
    private void updateRecordedElementsAfterSavingToObjectRepository(List<WebPage> recordedElements,
            Map<WebElement, FileEntity> entitySavedMap) {

        List<String> recordedElementNames = recordedElements.stream()
                .map(b -> b.getName())
                .collect(Collectors.toList());

        entitySavedMap.entrySet()
                .stream()
                .filter(a -> a.getKey() instanceof WebPage && a.getKey().getTag() != null
                        && a.getKey().getTag().equals("cloned"))
                .map(a -> (WebPage) a.getKey())
                .forEach(a -> {
                    try {
                        WebPage recordedWebPage = recordedElements.get(recordedElementNames.indexOf(a.getName()));
                        List<String> aChildNames = a.getChildren()
                                .stream()
                                .map(b -> b.getName())
                                .collect(Collectors.toList());
                        recordedWebPage.getChildren().removeIf(b -> aChildNames.contains(b.getName()));
                        recordedElements.add(a);
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                    }
                });
    }

    private Shell getShell(Shell activeShell) {
        String os = Platform.getOS();
        if (Platform.OS_WIN32.equals(os) || Platform.OS_LINUX.equals(os)) {
            return null;
        }
        Shell shell = new Shell();
        Rectangle activeShellSize = activeShell.getBounds();
        shell.setLocation((activeShellSize.width - shell.getBounds().width) / 2,
                (activeShellSize.height - shell.getBounds().height) / 2);
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
            final List<HTMLActionMapping> recordedActions, final List<WebPage> recordedElements,
            final ScriptNodeWrapper wrapper, final VariableEntity[] variables, final boolean shouldOverride) {
        if (testCaseCompositePart == null) {
            return;
        }
        final TestCasePart testCasePart = testCaseCompositePart.getChildTestCasePart();
        if (testCasePart.getTreeTableInput() == null) {
            testCaseCompositePart.loadTreeTableInput();
        }
        Job job = new Job(StringConstants.JOB_GENERATE_SCRIPT_MESSAGE) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    monitor.beginTask(StringConstants.JOB_GENERATE_SCRIPT_MESSAGE,
                            recordedActions.size() + recordedElements.size());

                    addRecordedElements(recordedElements, folderSelectionResult, monitor);
                    sync.syncExec(new Runnable() {
                        @SuppressWarnings("unchecked")
                        @Override
                        public void run() {
                            try {
                                List<StatementWrapper> children = (List<StatementWrapper>) wrapper.getBlock()
                                        .getAstChildren();
                                if (children.isEmpty()) {
                                    return;
                                }
                                testCasePart.addDefaultImports();
                                testCasePart.getTreeTableInput().getMainClassNode().addImport(Keys.class);

                                // append generated steps at the end of test case's steps
                                testCasePart.addStatements(children, NodeAddType.Add, true);
                                testCasePart.addVariables(variables);
                                testCaseCompositePart.refreshScript();
                                testCaseCompositePart.save();
                            } catch (Exception e) {
                                LoggerSingleton.logError(e);
                            }
                        }
                    });

                    if (folderSelectionResult != null) {
                        FolderTreeEntity targetFolderTreeEntity = folderSelectionResult.getSelectedParentFolder();
                        eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
                                targetFolderTreeEntity.getParent());
                        eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, targetFolderTreeEntity);
                        eventBroker.send(EventConstants.EXPLORER_EXPAND_TREE_ENTITY, targetFolderTreeEntity);
                    }

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

    private void addRecordedElements(List<WebPage> recordedElements,
            SaveToObjectRepositoryDialogResult folderSelectionResult, IProgressMonitor monitor) throws Exception {
        for (WebPage pageElement : recordedElements) {
            FolderEntity importedFolder = (FolderEntity) folderSelectionResult.getEntitySavedMap().get(pageElement);
            if (importedFolder != null) {
                pageElement.setFolderAlias(importedFolder);
            }
            for (WebElement childElement : ((WebFrame) pageElement).getChildren()) {
                addRecordedElement(childElement, importedFolder, folderSelectionResult);
            }
            monitor.worked(1);
        }
    }

    private void addRecordedElement(WebElement element, FolderEntity parentFolder,
            SaveToObjectRepositoryDialogResult folderSelectionResult) throws Exception {
        // Replace test object name with it's linked WebElementEntity
        FileEntity entity = folderSelectionResult.getEntitySavedMap().entrySet().stream().filter(e -> {
            WebElement savedElement = e.getKey();
            WebPage savedRoot = savedElement.getRoot();
            if (savedRoot == null) {
                return false;
            }
            WebPage root = element.getRoot();
            return savedElement.getName().equals(element.getName()) && root != null
                    && savedRoot.getName().equals(root.getName());
        }).map(e -> e.getValue()).findFirst().orElse(null);
        // If the entity doesn't exist that means user doesn't want to create new WebElementEntity
        if (entity != null) {
            element.setName(entity.getName());
        }
        if (element instanceof WebFrame) {
            for (WebElement childElement : ((WebFrame) element).getChildren()) {
                addRecordedElement(childElement, parentFolder, folderSelectionResult);
            }
        }
    }

}
