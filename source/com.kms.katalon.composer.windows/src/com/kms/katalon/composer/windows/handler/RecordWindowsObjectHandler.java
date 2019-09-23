package com.kms.katalon.composer.windows.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WindowsElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.parts.ExplorerPart;
import com.kms.katalon.composer.mobile.objectspy.dialog.AddElementToObjectRepositoryDialog;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.handlers.OpenTestCaseHandler;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.composer.windows.dialog.ExportReportToTestCaseSelectionDialog;
import com.kms.katalon.composer.windows.dialog.WindowsAppComposite;
import com.kms.katalon.composer.windows.dialog.WindowsRecorderDialog;
import com.kms.katalon.composer.windows.dialog.WindowsRecorderDialog.RecordActionResult;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.composer.windows.element.CapturedWindowsElementConverter;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.WindowsElementController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WindowsElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class RecordWindowsObjectHandler {
    
   @CanExecute
   public boolean canExecute() {
       return ProjectController.getInstance().getCurrentProject() != null;
   }

    private TestCaseEntity getTestCase(ExportReportToTestCaseSelectionDialog.ExportTestCaseSelectionResult result)
            throws ControllerException {
        switch (result.getOption()) {
            case APPEND_TO_TEST_CASE:
            case OVERWRITE_TEST_CASE:
                return TestCaseController.getInstance()
                        .getTestCaseByDisplayId(result.getFolder().getIdForDisplay() + "/" + result.getTestCaseName());
            case EXPORT_TO_NEW_TEST_CASE:
                return TestCaseController.getInstance().newTestCase(result.getFolder(), result.getTestCaseName());
            default:
                return null;
        }
    }

    @Execute
    public void execute(Shell activeShell) {
        Shell shell = null;
        try {
            WindowsRecorderDialog recordActionDialog = new WindowsRecorderDialog(getShell(activeShell),
                    new WindowsAppComposite());
            if (recordActionDialog.open() != WindowsRecorderDialog.OK) {
                return;
            }
            RecordActionResult recordActionResult = recordActionDialog.getRecordActionResult();
            if (recordActionResult.getScript().getBlock().getAstChildren().isEmpty()) {
                return;
            }
            saveTestObject(activeShell, recordActionResult);

            saveToTestCase(activeShell, recordActionDialog.getRecordActionResult());

        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, GlobalStringConstants.WARN, e.getMessage());
        } finally {
            if (shell != null) {
                shell.dispose();
            }
        }
    }

    private void saveToTestCase(Shell activeShell, WindowsRecorderDialog.RecordActionResult actionResult)
            throws ControllerException, Exception {
        ExportReportToTestCaseSelectionDialog dialog = new ExportReportToTestCaseSelectionDialog(activeShell);
        if (dialog.open() != ExportReportToTestCaseSelectionDialog.OK) {
            return;
        }

        ExportReportToTestCaseSelectionDialog.ExportTestCaseSelectionResult exportResult = dialog.getResult();
        MCompositePart part = OpenTestCaseHandler.getInstance().openTestCase(getTestCase(exportResult));

        boolean shouldOverride = false;
        if (exportResult
                .getOption() == ExportReportToTestCaseSelectionDialog.ExportTestCaseOption.OVERWRITE_TEST_CASE) {
            shouldOverride = true;
        }
        TestCaseCompositePart testCaseCompositePart = (TestCaseCompositePart) part.getObject();
        TestCasePart testCasePart = testCaseCompositePart.getChildTestCasePart();
        testCaseCompositePart.setScriptContentToManual();
        StringBuilder stringBuilder = new StringBuilder();
        new GroovyWrapperParser(stringBuilder).parseGroovyAstIntoScript(actionResult.getScript());
        ScriptNodeWrapper script = GroovyWrapperParser.parseGroovyScriptIntoNodeWrapper(stringBuilder.toString());
        @SuppressWarnings("unchecked")
        List<StatementWrapper> children = (List<StatementWrapper>) script.getBlock().getAstChildren();
        if (shouldOverride) {
            testCasePart.clearAndAddStatementsToMainBlock(children, NodeAddType.Add, true);
        } else {
            testCasePart.addStatementsToMainBlock(children, NodeAddType.Add, true);
        }
        testCasePart.addImports(script.getImports());
        testCasePart.getTreeTableInput().setChanged(true);
        testCaseCompositePart.changeScriptNode(testCasePart.getTreeTableInput().getMainClassNode());
        testCaseCompositePart.save();
    }

    private void saveTestObject(Shell activeShell, WindowsRecorderDialog.RecordActionResult recordResult)
            throws ControllerException {
        if (recordResult.getScript().getBlock().getAstChildren().isEmpty()) {
            return;
        }
        if (!recordResult.getWindowsElements().isEmpty()) {
            AddElementToObjectRepositoryDialog objectRepositoryDialog = new AddElementToObjectRepositoryDialog(
                    activeShell);
            if (objectRepositoryDialog.open() != AddElementToObjectRepositoryDialog.OK) {
                return;
            }
            FolderTreeEntity selectedTreeFolder = objectRepositoryDialog.getSelectedFolderTreeEntity();
            FolderEntity folder = getFolder(selectedTreeFolder);

            CapturedWindowsElementConverter converter = new CapturedWindowsElementConverter();
            List<ITreeEntity> selectedTreeEntities = new ArrayList<ITreeEntity>();
            for (CapturedWindowsElement capturedElement : recordResult.getWindowsElements()) {
                WindowsElementEntity windowsElement = converter.convert(capturedElement);
                windowsElement.setParentFolder(folder);
                windowsElement.setProject(folder.getProject());
                WindowsElementController.getInstance().updateWindowsElementEntity(windowsElement);

                capturedElement.setScriptId(windowsElement.getIdForDisplay());

                selectedTreeEntities.add(new WindowsElementTreeEntity(windowsElement, selectedTreeFolder));
            }
            ExplorerPart.getInstance().setSelectedItems(selectedTreeEntities.toArray());
        }
    }

    private FolderEntity getFolder(FolderTreeEntity selectedTreeFolder) {
        FolderEntity folder = null;
        try {
            folder = selectedTreeFolder.getObject();
        } catch (Exception ignored) {}
        return folder;
    }

    private Shell getShell(Shell activeShell) {
        String os = Platform.getOS();
        if (Platform.OS_WIN32.equals(os) || Platform.OS_LINUX.equals(os)) {
            return null;
        }
        Shell shell = new Shell();
        return shell;
    }
}
