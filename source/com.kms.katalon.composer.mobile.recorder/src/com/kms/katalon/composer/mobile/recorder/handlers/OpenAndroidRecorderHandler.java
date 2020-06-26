package com.kms.katalon.composer.mobile.recorder.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.parts.ExplorerPart;
import com.kms.katalon.composer.mobile.objectspy.components.MobileLocalAppComposite;
import com.kms.katalon.composer.mobile.objectspy.dialog.AddElementToObjectRepositoryDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.SaveTestCaseDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.SaveTestCaseDialog.ExportTestCaseOption;
import com.kms.katalon.composer.mobile.objectspy.dialog.SaveTestCaseDialog.ExportTestCaseSelectionResult;
import com.kms.katalon.composer.mobile.objectspy.element.CapturedMobileElementConverterV2;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.composer.mobile.recorder.components.MobileRecorderDialog;
import com.kms.katalon.composer.mobile.recorder.components.MobileRecorderDialog.RecordActionResult;
import com.kms.katalon.composer.mobile.recorder.constants.MobileRecoderMessagesConstants;
import com.kms.katalon.composer.mobile.recorder.constants.MobileRecorderStringConstants;
import com.kms.katalon.composer.mobile.util.MobileUtil;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.handlers.OpenTestCaseHandler;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.MobileElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.tracking.service.Trackings;

public class OpenAndroidRecorderHandler {
    private MobileRecorderDialog recorderDialog;

    private Shell activeShell;

    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void registerEvent() {
        eventBroker.subscribe(EventConstants.OBJECT_SPY_MOBILE, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                if (!canExecute()) {
                    return;
                }
                execute(Display.getCurrent().getActiveShell());
            }
        });
    }

    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
        MobileUtil.detectAppiumAndNodeJs(activeShell);
        openRecorderDialog(activeShell);
    }

    private boolean openRecorderDialog(Shell activeShell) {
        try {
            if (this.activeShell == null) {
                this.activeShell = activeShell;
            }
            recorderDialog = new MobileRecorderDialog(activeShell,
                    new MobileLocalAppComposite(MobileDriverType.ANDROID_DRIVER));
            if (recorderDialog.open() != Window.OK) {
                return false;
            }

            RecordActionResult recordActionResult = recorderDialog.getRecordActionResult();
            if (recordActionResult.getScript().getBlock().getAstChildren().isEmpty()) {
                return true;
            }

            saveTestObject(activeShell, recordActionResult, recorderDialog.getCurrentMobileDriverType());
            saveToTestCase(activeShell, recorderDialog.getRecordActionResult());
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(activeShell, MobileRecorderStringConstants.ERROR, e.getMessage());
            return false;
        }
    }

    private void saveTestObject(Shell activeShell, RecordActionResult recordResult, MobileDriverType driverType)
            throws ControllerException {
        if (recordResult.getScript().getBlock().getAstChildren().isEmpty()) {
            return;
        }
        if (!recordResult.getMobileElements().isEmpty()) {
            AddElementToObjectRepositoryDialog objectRepositoryDialog = new AddElementToObjectRepositoryDialog(
                    activeShell);
            if (objectRepositoryDialog.open() != AddElementToObjectRepositoryDialog.OK) {
                return;
            }
            FolderTreeEntity selectedTreeFolder = objectRepositoryDialog.getSelectedFolderTreeEntity();
            FolderEntity folder = getFolder(selectedTreeFolder);

            CapturedMobileElementConverterV2 converter = new CapturedMobileElementConverterV2();
            List<ITreeEntity> selectedTreeEntities = new ArrayList<ITreeEntity>();
            for (CapturedMobileElement capturedElement : recordResult.getMobileElements()) {
                MobileElementEntity mobileElement;
                try {
                    mobileElement = converter.convert(capturedElement, folder, driverType);
                    ObjectRepositoryController.getInstance().updateTestObject(mobileElement);
                    capturedElement.setScriptId(mobileElement.getIdForDisplay());
                    selectedTreeEntities.add(new WebElementTreeEntity(mobileElement, selectedTreeFolder));
                } catch (Exception e) {
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
                }
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

    private void saveToTestCase(Shell activeShell, RecordActionResult actionResult)
            throws ControllerException, Exception {
        SaveTestCaseDialog dialog = new SaveTestCaseDialog(activeShell);
        if (dialog.open() != SaveTestCaseDialog.OK) {
            return;
        }

        ExportTestCaseSelectionResult exportResult = dialog.getResult();

        FolderEntity selectedFolder = exportResult.getFolder();
        FolderTreeEntity selectedFolderTreeEntity = TreeEntityUtil.getFolderTreeEntity(selectedFolder);

        TestCaseEntity testCaseEntity = getTestCase(exportResult);
        TestCaseTreeEntity testCaseTreeEntity = new TestCaseTreeEntity(testCaseEntity, selectedFolderTreeEntity);

        ExplorerPart.getInstance().refreshTreeEntity(selectedFolderTreeEntity);
        ExplorerPart.getInstance().setSelectedItems(new Object[] { testCaseTreeEntity });

        MCompositePart part = OpenTestCaseHandler.getInstance().openTestCase(testCaseEntity);

        boolean shouldOverride = false;
        if (exportResult
                .getOption() == ExportTestCaseOption.OVERWRITE_TEST_CASE) {
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

    private TestCaseEntity getTestCase(ExportTestCaseSelectionResult result)
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
}
