package com.kms.katalon.composer.integration.qtest.preference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.dialog.TestCaseRepoDialog;
import com.kms.katalon.composer.integration.qtest.job.DisintegrateTestCaseJob;
import com.kms.katalon.composer.integration.qtest.model.TestCaseRepo;
import com.kms.katalon.composer.integration.qtest.preference.provider.TestCaseRepoTableLabelProvider;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationFolderManager;
import com.kms.katalon.integration.qtest.QTestIntegrationProjectManager;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;

public class TestCaseRepoPreferencePage extends AbstractQTestIntegrationPage {
    public TestCaseRepoPreferencePage() {
    }

    @Inject
    private UISynchronize sync;

    private Composite container;
    private Table table;
    private ToolItem btnAdd, btnEdit, btnRemove;
    private TableViewer tableViewer;
    private List<QTestProject> qTestProjects;
    private List<TestCaseRepo> testCaseRepositories;

    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        Composite compositeToolbar = new Composite(container, SWT.NONE);
        compositeToolbar.setLayout(new FillLayout(SWT.HORIZONTAL));
        compositeToolbar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        ToolBar toolbar = new ToolBar(compositeToolbar, SWT.FLAT | SWT.RIGHT);
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());

        btnAdd = new ToolItem(toolbar, SWT.NONE);
        btnAdd.setText(StringConstants.ADD);
        btnAdd.setImage(ImageConstants.IMG_16_ADD);

        btnEdit = new ToolItem(toolbar, SWT.NONE);
        btnEdit.setText(StringConstants.EDIT);
        btnEdit.setEnabled(false);
        btnEdit.setImage(ImageConstants.IMG_16_EDIT);

        btnRemove = new ToolItem(toolbar, SWT.NONE);
        btnRemove.setText(StringConstants.REMOVE);
        btnRemove.setImage(ImageConstants.IMG_16_REMOVE);
        btnRemove.setEnabled(false);

        Composite compositeTable = new Composite(container, SWT.NONE);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tableViewer = new TableViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        table.setHeaderVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TableViewerColumn tableViewerColumnQTestProject = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnQTestProject = tableViewerColumnQTestProject.getColumn();
        tblclmnQTestProject.setText(StringConstants.DIA_TITLE_QTEST_PROJECT);

        TableViewerColumn tableViewerColumnQTestModule = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnQTestModule = tableViewerColumnQTestModule.getColumn();
        tblclmnQTestModule.setText(StringConstants.DIA_TITLE_QTEST_MODULE);

        TableViewerColumn tableViewerColumnKatalonFolder = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnKatalonFolder = tableViewerColumnKatalonFolder.getColumn();
        tblclmnKatalonFolder.setText(StringConstants.DIA_TITLE_TEST_CASE_FOLDER);

        tableViewer.setLabelProvider(new TestCaseRepoTableLabelProvider());
        tableViewer.setContentProvider(ArrayContentProvider.getInstance());

        TableColumnLayout tableLayout = new TableColumnLayout();
        tableLayout.setColumnData(tblclmnQTestProject, new ColumnWeightData(0, 100));
        tableLayout.setColumnData(tblclmnQTestModule, new ColumnWeightData(40, 100));
        tableLayout.setColumnData(tblclmnKatalonFolder, new ColumnWeightData(40, 100));
        compositeTable.setLayout(tableLayout);
        
        addControlModifySelectionListeners();
        initialize();
        return container;
    }

    @Override
    protected void initialize() {
        if (container == null || container.isDisposed()) {
            return;
        }

        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        IntegratedEntity integratedProjectEntity = QTestIntegrationUtil.getIntegratedEntity(projectEntity);

        try {
            if (integratedProjectEntity != null) {
                qTestProjects = QTestIntegrationProjectManager
                        .getQTestProjectsByIntegratedEntity(integratedProjectEntity);
            } else {
                qTestProjects = new ArrayList<QTestProject>();
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            MultiStatusErrorDialog.showErrorDialog(ex, StringConstants.DIA_MSG_UNABLE_GET_PROJECT_INFO, ex.getMessage());
        }

        testCaseRepositories = QTestIntegrationUtil.getTestCaseRepositories(projectEntity, qTestProjects);

        tableViewer.setInput(testCaseRepositories);
    }

    private void addControlModifySelectionListeners() {
        btnAdd.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                addNewTestCaseRepo();
            }
        });

        btnEdit.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                editTestCaseRepo();
            }
        });

        btnRemove.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                removeTestCaseRepo();
            }
        });

        tableViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
                if (selection == null || selection.isEmpty()) {
                    btnEdit.setEnabled(false);
                    btnRemove.setEnabled(false);
                } else {
                    btnEdit.setEnabled(true);
                    btnRemove.setEnabled(true);
                }

            }
        });
    }

    private void addNewTestCaseRepo() {
        List<String> currentFolderIds = getRegisteredFolderIds();

        TestCaseRepoDialog dialog = new TestCaseRepoDialog(btnAdd.getDisplay().getActiveShell(), qTestProjects,
                currentFolderIds, null);
        if (dialog.open() == Dialog.OK) {
            TestCaseRepo repo = dialog.getTestCaseRepo();
            testCaseRepositories.add(repo);

            qTestProjects.clear();
            for (QTestProject qTestProject : dialog.getQTestProjectsMap().values()) {
                if (qTestProject.equals(repo.getQTestProject())) {
                    qTestProject.getTestCaseFolderIds().add(repo.getFolderId());
                }

                qTestProjects.add(qTestProject);
            }
            tableViewer.refresh();
        }
    }

    private List<String> getRegisteredFolderIds() {
        List<String> currentFolderIds = new ArrayList<String>();
        for (TestCaseRepo testCaseRepo : testCaseRepositories) {
            currentFolderIds.add(testCaseRepo.getFolderId());
        }

        return currentFolderIds;
    }

    private void editTestCaseRepo() {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();        
        if (selection == null || selection.isEmpty()) {
            return;
        }

        final TestCaseRepo repo = (TestCaseRepo) selection.getFirstElement();
        List<String> currentFolderIds = getRegisteredFolderIds();
        currentFolderIds.remove(repo.getFolderId());

        TestCaseRepoDialog dialog = new TestCaseRepoDialog(btnAdd.getDisplay().getActiveShell(), qTestProjects,
                currentFolderIds, repo);
        if (dialog.open() == Dialog.OK) {
            final TestCaseRepo newRepo = dialog.getTestCaseRepo();
            
            if (repo.equals(newRepo)) {
                return;
            }
            
            final int index = testCaseRepositories.indexOf(repo);
            String folderId = repo.getFolderId();
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
            try {
                FolderEntity folderEntity = FolderController.getInstance().getFolderByDisplayId(projectEntity,
                        folderId);

                if (folderEntity == null) {
                    insertNewRepoToTable(index, newRepo);
                    return;
                }
                
                IntegratedEntity folderIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(folderEntity);
                
                if (folderIntegratedEntity == null) {
                    insertNewRepoToTable(index, newRepo);
                }

                if (confirmRemoveRepo()) {
                    performInsertTestCaseRepo(folderEntity, newRepo, index);
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
                MultiStatusErrorDialog.showErrorDialog(e, StringConstants.DIA_MSG_UNABLE_MOFIDY_TEST_CASE_REPO, e
                        .getClass().getSimpleName());
            }
        }
    }

    private void removeRepoFromTable(TestCaseRepo repo) {
        if (testCaseRepositories.contains(repo)) {
            testCaseRepositories.remove(repo);
        }
        tableViewer.refresh();
    }

    private void insertNewRepoToTable(int index, TestCaseRepo newRepo) {
        testCaseRepositories.remove(index);
        if (index >= testCaseRepositories.size()) {
            testCaseRepositories.add(newRepo);
        } else {
            testCaseRepositories.add(index, newRepo);
        }
        tableViewer.refresh();
    }

    private boolean confirmRemoveRepo() {
       return MessageDialog.openConfirm(null, StringConstants.CONFIRMATION,
               StringConstants.DIA_CONFIRM_DISINTEGRATE_TEST_CASE_FOLDER);
    }

    private void removeTestCaseRepo() {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if (selection == null || selection.isEmpty()) {
            return;
        }

        final TestCaseRepo repo = (TestCaseRepo) selection.getFirstElement();

        String folderId = repo.getFolderId();
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        try {
            FolderEntity folderEntity = FolderController.getInstance().getFolderByDisplayId(projectEntity, folderId);

            if (folderEntity == null) {
                removeRepoFromTable(repo);
                return;
            }

            IntegratedEntity folderIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(folderEntity);

            if (folderIntegratedEntity == null) {
                removeRepoFromTable(repo);
            }

            if (confirmRemoveRepo()) {
                performRemoveTestCaseRepo(folderEntity, repo);
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.DIA_MSG_UNABLE_REMOVE_TEST_CASE_REPO, e
                    .getClass().getSimpleName());
        }
    }

    private void performRemoveTestCaseRepo(final FolderEntity folderEntity, final TestCaseRepo repo) {
        DisintegrateTestCaseJob job = new DisintegrateTestCaseJob(true);
        job.setFileEntities(Arrays.asList((IntegratedFileEntity) folderEntity));
        job.doTask();
        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                sync.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        removeRepoFromTable(repo);                        
                    }
                });
            }
        });
    }

    private void performInsertTestCaseRepo(final FolderEntity folderEntity, final TestCaseRepo newRepo, final int index) {
        DisintegrateTestCaseJob job = new DisintegrateTestCaseJob(true);
        job.setFileEntities(Arrays.asList((IntegratedFileEntity) folderEntity));
        job.doTask();
        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                sync.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        insertNewRepoToTable(index, newRepo);
                    }
                });
            }
        });
    }

    @Override
    public boolean performOk() {
        // if it never be opened, just returns to the parent class
        if (container == null) {
            return true;
        }

        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();

        // Sync with the current project
        Set<QTestProject> currentProjects = new LinkedHashSet<QTestProject>();

        IntegratedEntity projectIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(projectEntity);

        if (projectIntegratedEntity != null) {
            currentProjects.addAll(QTestIntegrationProjectManager
                    .getQTestProjectsByIntegratedEntity(projectIntegratedEntity));
        }

        currentProjects.addAll(qTestProjects);

        for (QTestProject qTestProject : currentProjects) {
            qTestProject.getTestCaseFolderIds().clear();
        }

        for (TestCaseRepo repo : testCaseRepositories) {
            for (QTestProject qTestProject : currentProjects) {
                if (repo.getQTestProject().equals(qTestProject)) {
                    qTestProject.getTestCaseFolderIds().add(repo.getFolderId());
                    break;
                }
            }

            // update integrated entity of folderEntity
            try {
                FolderEntity folderEntity = FolderController.getInstance().getFolderByDisplayId(projectEntity,
                        repo.getFolderId());
                if (folderEntity != null) {
                    saveFolder(folderEntity, repo.getQTestModule());
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
                continue;
            }
        }

        qTestProjects.clear();
        qTestProjects.addAll(currentProjects);

        saveProject(projectEntity);
        return true;
    }

    private void saveFolder(FolderEntity folderEntity, QTestModule qTestModule) {
        IntegratedEntity folderNewIntegratedEntity = QTestIntegrationFolderManager
                .getFolderIntegratedEntityByQTestModule(qTestModule);

        folderEntity = (FolderEntity) QTestIntegrationUtil.updateFileIntegratedEntity(folderEntity,
                folderNewIntegratedEntity);

        try {
            FolderController.getInstance().saveFolder(folderEntity);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void saveProject(ProjectEntity projectEntity) {
        IntegratedEntity projectNewIntegratedEntity = QTestIntegrationProjectManager
                .getIntegratedEntityByQTestProjects(qTestProjects);

        ProjectEntity currentProject = (ProjectEntity) QTestIntegrationUtil.updateFileIntegratedEntity(projectEntity,
                projectNewIntegratedEntity);

        try {
            ProjectController.getInstance().updateProject(currentProject);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected void performDefaults() {
        initialize();
    }

}
