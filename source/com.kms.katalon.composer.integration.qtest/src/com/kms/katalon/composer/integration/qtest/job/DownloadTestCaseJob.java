package com.kms.katalon.composer.integration.qtest.job;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.dialog.TestCaseRootSelectionDialog;
import com.kms.katalon.composer.integration.qtest.dialog.TestCaseTreeDownloadedPreviewDialog;
import com.kms.katalon.composer.integration.qtest.dialog.model.DownloadedPreviewTreeNode;
import com.kms.katalon.composer.integration.qtest.dialog.model.ModuleDownloadedPreviewTreeNode;
import com.kms.katalon.composer.integration.qtest.dialog.model.TestCaseDownloadedPreviewTreeNode;
import com.kms.katalon.composer.util.groovy.GroovyGuiUtil;
import com.kms.katalon.console.utils.EntityTrackingHelper;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.ast.GroovyParser;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.integration.qtest.QTestIntegrationFolderManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestCaseManager;
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestStep;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;
import com.kms.katalon.integration.qtest.setting.QTestSettingCredential;

public class DownloadTestCaseJob extends QTestJob {

    private UISynchronize sync;

    private Object[] selectedElements;

    private QTestModule qTestSelectedModule;

    private boolean isMonitorCanceled;

    private boolean isMergeFolderConfirmed;

    private boolean isMergeTestCaseConfimed;

    private IQTestCredential credential;

    public DownloadTestCaseJob(UISynchronize sync) {
        super(StringConstants.JOB_TASK_DOWNLOAD_TEST_CASE);
        setUser(true);
        this.sync = sync;
        qTestSelectedModule = null;
        isMonitorCanceled = false;
        credential = QTestSettingCredential.getCredential(getProjectDir());
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        monitor.beginTask(StringConstants.JOB_SUB_TASK_DOWNLOAD_TEST_CASE, 2);

        monitor.subTask(StringConstants.JOB_SUB_TASK_CHECK_SYSTEM);
        FileEntity fileEntity = getFileEntities().get(0);
        final FolderEntity folderEntity = (FolderEntity) fileEntity;
        monitor.worked(1);

        try {
            monitor.subTask(MessageFormat.format(StringConstants.JOB_SUB_TASK_FETCH_CHILDREN, folderEntity.getName()));

            IntegratedEntity folderIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(folderEntity);
            String projectDir = projectEntity.getFolderLocation();

            QTestProject qTestProject = QTestIntegrationUtil.getTestCaseRepo(folderEntity, projectEntity)
                    .getQTestProject();

            if (folderIntegratedEntity != null) {
                qTestSelectedModule = QTestIntegrationFolderManager.getQTestModuleByFolderEntity(folderEntity);

                QTestIntegrationFolderManager.updateModule(credential, qTestProject.getId(), qTestSelectedModule, true);
            } else {
                // users have not specified root folder of test case on qTest,
                // let them choose one.
                QTestModule moduleRoot = QTestIntegrationFolderManager.getModuleRoot(
                        QTestSettingCredential.getCredential(projectDir), qTestProject.getId());
                QTestIntegrationFolderManager.updateModule(credential, qTestProject.getId(), moduleRoot, true);

                performTestCaseRootSelection(moduleRoot);
            }
            monitor.worked(1);

            if (qTestSelectedModule == null || monitor.isCanceled() || isMonitorCanceled) {
                return Status.CANCEL_STATUS;
            }

            // Let user choose test cases they want to create.
            performTestCaseCreatedSelection(folderEntity);

            if (monitor.isCanceled() || isMonitorCanceled) {
                return Status.CANCEL_STATUS;
            }

            if (selectedElements != null && selectedElements.length > 0) {
                if (folderIntegratedEntity == null) {
                    // create new integrated entity for test case root
                    folderIntegratedEntity = QTestIntegrationFolderManager.getFolderIntegratedEntityByQTestModule(qTestSelectedModule);
                    folderEntity.getIntegratedEntities().add(folderIntegratedEntity);
                    FolderController.getInstance().saveFolder(folderEntity);
                }
                monitor.done();
                monitor.beginTask(StringConstants.JOB_TASK_CREATE_TEST_CASE, selectedElements.length);
                for (int index = 0; index < selectedElements.length; index++) {
                    try {
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }

                        doUpdateSelectedItem(qTestProject, selectedElements[index], monitor);
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                    }
                }

                GroovyUtil.refreshInfiniteScriptTestCaseClasspath(projectEntity, folderEntity, null);
            }

            EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_REFRESH, null);
            return Status.OK_STATUS;
        } catch (Exception e) {
            monitor.setCanceled(true);
            return Status.CANCEL_STATUS;
        } finally {
            monitor.done();
        }
    }

    /**
     * Create a confirmation dialog for users can choose test cases they want to create.
     * 
     * @param folderEntity
     */
    private void performTestCaseCreatedSelection(final FolderEntity folderEntity) {
        sync.syncExec(new Runnable() {
            @Override
            public void run() {
                TestCaseTreeDownloadedPreviewDialog dialog = new TestCaseTreeDownloadedPreviewDialog(
                        Display.getDefault().getActiveShell(), folderEntity, qTestSelectedModule);

                if (dialog.open() == Dialog.OK) {
                    selectedElements = dialog.selectedElements();
                } else {
                    isMonitorCanceled = true;
                }
            }
        });
    }

    /**
     * Open a confirmation dialog that requires user choose test case root folder
     * 
     * @param moduleRoot
     */
    private void performTestCaseRootSelection(final QTestModule moduleRoot) {
        sync.syncExec(new Runnable() {
            @Override
            public void run() {
                TestCaseRootSelectionDialog testCaseRootSelectionDialog = new TestCaseRootSelectionDialog(
                        Display.getDefault().getActiveShell(), moduleRoot, false);
                if (testCaseRootSelectionDialog.open() == Dialog.OK) {
                    qTestSelectedModule = testCaseRootSelectionDialog.getSelectedModule();
                } else {
                    isMonitorCanceled = true;
                }
            }
        });
    }

    /**
     * Open a confirmation dialog that requires user choose they want to merge folders or not.
     * 
     * @param moduleRoot
     */
    private void openMergeFoldersDialog(final QTestModule qTestModule) {
        sync.syncExec(new Runnable() {
            @Override
            public void run() {
                isMergeFolderConfirmed = MessageDialog.openConfirm(null, StringConstants.DIA_TITLE_FOLDER_DUPLICATION,
                        MessageFormat.format(StringConstants.DIA_MSG_CONFIRM_MERGE_DOWNLOADED_TEST_CASE_FOLDER,
                                qTestModule.getName()));
            }
        });
    }

    /**
     * Open a confirmation dialog that requires user choose they want to merge test cases or not.
     * 
     * @param moduleRoot
     */
    private void openMergeTestCasesDialog(final QTestTestCase qTestCase) {
        sync.syncExec(new Runnable() {
            @Override
            public void run() {
                isMergeTestCaseConfimed = MessageDialog.openConfirm(
                        null,
                        StringConstants.DIA_TITLE_TEST_CASE_DUPLICATION,
                        MessageFormat.format(StringConstants.DIA_MSG_CONFIRM_MERGE_DOWNLOADED_TEST_CASE,
                                qTestCase.getName()));
            }
        });
    }

    /**
     * Get integrated entity for the given folder entity by using
     * {@link QTestIntegrationFolderManager#getFolderIntegratedEntityByQTestModule(QTestModule)} on qTestModule of the
     * given module tree
     * 
     * @param testCaseEntity
     * @param testCaseTree
     * @throws Exception
     */
    private void addIntegratedEntityToFolder(FolderEntity folderEntity, ModuleDownloadedPreviewTreeNode moduleTree)
            throws Exception {
        IntegratedEntity newIntegratedEntity = QTestIntegrationFolderManager.getFolderIntegratedEntityByQTestModule(moduleTree.getModule());
        folderEntity.getIntegratedEntities().add(newIntegratedEntity);

        moduleTree.setFolderEntity(folderEntity);

        FolderController.getInstance().saveFolder(folderEntity);
    }

    /**
     * Get integrated entity for the given test case entity by using
     * {@link QTestIntegrationTestCaseManager#getIntegratedEntityByQTestTestCase(QTestTestCase)} on qTestCase of the
     * given test case tree
     * 
     * @param testCaseEntity
     * @param testCaseTree
     * @throws Exception
     */
    private void addIntegratedEntityToTestCase(TestCaseEntity testCaseEntity,
            TestCaseDownloadedPreviewTreeNode testCaseTree, QTestProject qTestProject) throws Exception {

        QTestTestCase qTestCase = testCaseTree.getTestCase();
        if (qTestCase.getVersionId() == 0) {
            qTestCase.setVersionId(QTestIntegrationTestCaseManager.getTestCaseVersionId(credential,
                    qTestProject.getId(), qTestCase.getId()));
        }
        IntegratedEntity newIntegratedEntity = QTestIntegrationTestCaseManager.getIntegratedEntityByQTestTestCase(testCaseTree.getTestCase());
        testCaseEntity.getIntegratedEntities().add(newIntegratedEntity);

        TestCaseController.getInstance().updateTestCase(testCaseEntity);
    }

    /**
     * Creates new test case on folder that is confirmed to be created by user. If the given selectedItem is a test
     * case, system also generate test script that includes qTest steps of the updated test case as groovy comments.
     */
    private void doUpdateSelectedItem(QTestProject qTestProject, Object selectedItem, IProgressMonitor monitor)
            throws Exception {
        isMergeFolderConfirmed = false;
        isMergeTestCaseConfimed = false;

        if (!(selectedItem instanceof DownloadedPreviewTreeNode)) {
            return;
        }

        DownloadedPreviewTreeNode treeItem = (DownloadedPreviewTreeNode) selectedItem;

        FolderEntity parentFolder = treeItem.getParent().getFolderEntity();

        String dialogDisplayedName = getWrappedName(treeItem.getName());

        if (selectedItem instanceof ModuleDownloadedPreviewTreeNode) {
            ModuleDownloadedPreviewTreeNode moduleTree = (ModuleDownloadedPreviewTreeNode) selectedItem;
            if (moduleTree.getFolderEntity() == null) {
                monitor.subTask(MessageFormat.format(StringConstants.JOB_SUB_TASK_CREATE_TEST_CASE_FOLDER,
                        dialogDisplayedName));

                FolderEntity existingFolder = FolderController.getInstance().getFolder(
                        parentFolder.getId() + File.separator + treeItem.getName());

                if (existingFolder != null) {
                    IntegratedEntity existingIntegratedFolderEntity = QTestIntegrationUtil.getIntegratedEntity(existingFolder);

                    if (existingIntegratedFolderEntity == null) {
                        // They are duplicated but can be merged, let users
                        // decide to merge or not
                        openMergeFoldersDialog(moduleTree.getModule());
                    }
                }

                if (isMergeFolderConfirmed) {
                    addIntegratedEntityToFolder(existingFolder, moduleTree);
                } else {
                    FolderEntity newFolderEntity = FolderController.getInstance().addNewFolder(parentFolder,
                            moduleTree.getModule().getName());
                    addIntegratedEntityToFolder(newFolderEntity, moduleTree);
                }

            }
        } else if (selectedItem instanceof TestCaseDownloadedPreviewTreeNode) {
            TestCaseDownloadedPreviewTreeNode testCaseTree = (TestCaseDownloadedPreviewTreeNode) selectedItem;

            QTestTestCase qTestCase = testCaseTree.getTestCase();
            monitor.subTask(MessageFormat.format(StringConstants.JOB_SUB_TASK_CREATE_TEST_CASE, dialogDisplayedName));

            TestCaseEntity existingTestCase = TestCaseController.getInstance().getTestCase(
                    parentFolder.getId() + File.separator + treeItem.getName()
                            + TestCaseEntity.getTestCaseFileExtension());

            if (existingTestCase != null) {
                IntegratedEntity existingIntegratedTestCaseEntity = QTestIntegrationUtil.getIntegratedEntity(existingTestCase);
                if (existingIntegratedTestCaseEntity == null) {
                    // They are duplicated but can be merged, let users decide
                    // to merge or not
                    openMergeTestCasesDialog(qTestCase);
                }
            }

            if (isMergeTestCaseConfimed) {
                addIntegratedEntityToTestCase(existingTestCase, testCaseTree, qTestProject);

                // notify to the opening part of existing test case that it has
                // been updated.
                EventBrokerSingleton.getInstance()
                        .getEventBroker()
                        .post(EventConstants.TESTCASE_UPDATED,
                                new Object[] { existingTestCase.getId(), existingTestCase });
            } else {
                TestCaseEntity newTestCaseEntity = TestCaseController.getInstance().newTestCase(parentFolder,
                        qTestCase.getName());
                EntityTrackingHelper.trackTestCaseCreated();

                addDescriptionForTestCase(qTestProject, qTestCase, newTestCaseEntity);

                addIntegratedEntityToTestCase(newTestCaseEntity, testCaseTree, qTestProject);
            }
        }

        monitor.worked(1);
    }

    /**
     * Add description of the given qTestCase into description of testCaseEntity. For each step of the given qTestCase,
     * generate a comment that has test step order, description and expected result in test case's script.
     */
    private void addDescriptionForTestCase(QTestProject qTestProject, QTestTestCase qTestCase,
            TestCaseEntity testCaseEntity) throws Exception {
        GroovyCompilationUnit unit = (GroovyCompilationUnit) GroovyGuiUtil.getGroovyScriptForTestCase(testCaseEntity);

        ClassNode clazzNode = unit.getModuleNode().getClasses().get(0);
        Statement statement = clazzNode.getMethods("run").get(0).getCode();

        if (statement instanceof BlockStatement) {
            BlockStatement blockStatement = (BlockStatement) statement;
            blockStatement.getStatements().clear();
            List<QTestStep> qTestSteps = QTestIntegrationTestCaseManager.getListSteps(credential, qTestProject.getId(),
                    qTestCase);

            for (QTestStep qTestStep : qTestSteps) {
                BlockStatement stepBlock = new BlockStatement();
                int stepOrder = qTestSteps.indexOf(qTestStep) + 1;
                // format qTest step as a comment:
                // Step order: [Step Description: <description>, Expected
                // Result: <expected result>]
                ExpressionStatement descriptionStatement = new ExpressionStatement(new ConstantExpression("Step "
                        + Integer.toString(stepOrder) + ": [Step Description: " + qTestStep.getDescription()
                        + ", Expected Result: " + qTestStep.getExpectedResult() + "]"));
                stepBlock.addStatement(descriptionStatement);

                blockStatement.addStatement(stepBlock);
            }
        }

        testCaseEntity.setDescription(qTestCase.getDescription());

        GroovyParser parser = new GroovyParser(new StringBuilder());
        List<ASTNode> astNodes = new ArrayList<ASTNode>();
        astNodes.add(clazzNode);
        astNodes.add(statement);
        parser.parseGroovyAstIntoScript(astNodes);
        FileUtils.writeStringToFile(new File(TestCaseController.getInstance().getGroovyScriptFilePath(testCaseEntity)),
                parser.getValue());

    }
}
