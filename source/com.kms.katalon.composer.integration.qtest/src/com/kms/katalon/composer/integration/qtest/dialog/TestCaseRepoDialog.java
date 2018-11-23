package com.kms.katalon.composer.integration.qtest.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.dialog.provider.TestCaseFolderEntityProvider;
import com.kms.katalon.composer.integration.qtest.model.TestCaseRepo;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationFolderManager;
import com.kms.katalon.integration.qtest.QTestIntegrationProjectManager;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.setting.QTestSettingCredential;

public class TestCaseRepoDialog extends Dialog {

    private Composite container;

    private Text txtQTestModule;

    private Text txtKatalonFolder;

    private Button btnUpdateProjects;

    private Button btnFindQTestModule;

    private Button btnBrowseKatalonFolder;

    private Combo cbProjects;

    private List<String> folderIds;

    private QTestModule qTestModule;

    private QTestProject qTestProject;

    private String folderId;

    private Map<Long, QTestProject> qTestProjectsMap;

    public Map<Long, QTestProject> getQTestProjectsMap() {
        return qTestProjectsMap;
    }

    public TestCaseRepoDialog(Shell parentShell, List<QTestProject> qTestProjects, List<String> folderIds,
            TestCaseRepo testCaseRepo) {
        super(parentShell);
        this.qTestProjectsMap = new LinkedHashMap<Long, QTestProject>();
        updateQTestProjectsMap(qTestProjects);
        this.folderIds = folderIds;

        if (testCaseRepo != null) {
            qTestProject = testCaseRepo.getQTestProject();
            qTestModule = testCaseRepo.getQTestModule();
            folderId = testCaseRepo.getFolderId();
        }
    }

    private void updateQTestProjectsMap(List<QTestProject> qTestProjects) {
        for (QTestProject qTestProject : qTestProjects) {
            qTestProjectsMap.put(qTestProject.getId(), qTestProject);
        }
    }

    protected Control createDialogArea(Composite parent) {
        container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) container.getLayout();
        GridData gridData = (GridData) container.getLayoutData();
        gridData.widthHint = 500;
        gridLayout.numColumns = 3;

        Label lblQTestProject = new Label(container, SWT.NONE);
        lblQTestProject.setText(StringConstants.DIA_TITLE_QTEST_PROJECT);

        cbProjects = new Combo(container, SWT.NONE);
        cbProjects.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        btnUpdateProjects = new Button(container, SWT.NONE);
        btnUpdateProjects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnUpdateProjects.setText(StringConstants.UPDATE);

        Label lblQTestModule = new Label(container, SWT.NONE);
        lblQTestModule.setText(StringConstants.DIA_TITLE_QTEST_MODULE);

        txtQTestModule = new Text(container, SWT.BORDER | SWT.READ_ONLY);
        txtQTestModule.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        btnFindQTestModule = new Button(container, SWT.NONE);
        btnFindQTestModule.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnFindQTestModule.setText(StringConstants.FIND);

        Label lblKatalonFolder = new Label(container, SWT.NONE);
        lblKatalonFolder.setText(StringConstants.DIA_TITLE_KATALON_FOLDER);

        txtKatalonFolder = new Text(container, SWT.BORDER);
        txtKatalonFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        btnBrowseKatalonFolder = new Button(container, SWT.NONE);
        btnBrowseKatalonFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnBrowseKatalonFolder.setText(StringConstants.BROWSE);

        return container;
    }

    @Override
    public void create() {
        super.create();

        addButtonSelectionListeners();
        initialize();
        validate();
    }

    private void initialize() {
        if (qTestProject != null) {
            cbProjects.setText(qTestProject.getName());
        }
        updateProjectComboboxItems();

        if (folderId != null) {
            txtKatalonFolder.setText(folderId);
        }

        if (qTestModule != null) {
            txtQTestModule.setText(qTestModule.getName());
        }
    }

    private void updateProjectComboboxItems() {
        String selectedProjectName = cbProjects.getText();

        List<String> projectNames = new ArrayList<String>();
        for (QTestProject qTestProject : qTestProjectsMap.values()) {
            projectNames.add(qTestProject.getName());
        }

        cbProjects.setItems(projectNames.toArray(new String[projectNames.size()]));

        if (cbProjects.getItemCount() <= 0) {
            return;
        }

        if (selectedProjectName.isEmpty()) {
            cbProjects.select(0);
        } else {
            int index = projectNames.indexOf(selectedProjectName);

            if (index >= 0) {
                cbProjects.select(index);
            }
        }
    }

    private void addButtonSelectionListeners() {
        btnUpdateProjects.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateProjects();
                validate();
            }
        });

        btnFindQTestModule.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                findQTestModule();
                validate();
            }
        });

        btnBrowseKatalonFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                findTestCaseFolder();
                validate();
            }
        });

        cbProjects.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                QTestProject[] qTestProjects = qTestProjectsMap.values()
                        .toArray(new QTestProject[qTestProjectsMap.values().size()]);
                int index = cbProjects.getSelectionIndex();
                if (index >= 0) {
                    if (qTestProject != null && !qTestProject.equals(qTestProjects[index])) {
                        qTestModule = null;
                        txtQTestModule.setText("");
                    }
                    qTestProject = qTestProjects[index];
                    validate();
                }
            }
        });
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        if (StringUtils.isBlank(folderId)) {
            newShell.setText(StringConstants.DIA_TITLE_CREATE_TEST_CASE_REPO);
        } else {
            newShell.setText(StringConstants.DIA_TITLE_EDIT_TEST_CASE_REPO);
        }
    }

    public List<String> getFolderIds() {
        return folderIds;
    }

    public void setFolderIds(List<String> folderIds) {
        this.folderIds = folderIds;
    }

    private void updateProjects() {
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
        try {
            List<QTestProject> updatedProjects = QTestIntegrationProjectManager
                    .getAllProject(QTestSettingCredential.getCredential(projectDir));
            mergeProjects(updatedProjects);
            updateProjectComboboxItems();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.DIA_MSG_UNABLE_TO_UPDATE_PROJECT, e.getMessage());
        }
    }

    private void mergeProjects(List<QTestProject> updatedProjects) {
        for (QTestProject updatedQTestProject : updatedProjects) {
            for (QTestProject currentQTestProject : qTestProjectsMap.values()) {
                if (updatedQTestProject.getId() == currentQTestProject.getId()) {
                    updatedQTestProject.setTestCaseFolderIds(currentQTestProject.getTestCaseFolderIds());
                    updatedQTestProject.setTestSuiteFolderIds(currentQTestProject.getTestSuiteFolderIds());
                    break;
                }
            }
        }
        qTestProjectsMap.clear();
        updateQTestProjectsMap(updatedProjects);

        updateProjectComboboxItems();
    }

    private void findQTestModule() {
        try {
            String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
            QTestModule moduleRoot = QTestIntegrationFolderManager
                    .getModuleRoot(QTestSettingCredential.getCredential(projectDir), qTestProject);
            TestCaseRootSelectionDialog testCaseRootSelectionDialog = new TestCaseRootSelectionDialog(
                    Display.getDefault().getActiveShell(), moduleRoot, true);
            testCaseRootSelectionDialog.setProjectDir(projectDir);
            testCaseRootSelectionDialog.setQTestProject(qTestProject);
            if (testCaseRootSelectionDialog.open() == Dialog.OK) {
                qTestModule = testCaseRootSelectionDialog.getSelectedModule();
                txtQTestModule.setText(qTestModule.getName());
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.DIA_MSG_UNABLE_TO_UPDATE_MODULE, e.getMessage());
        }
    }

    private void findTestCaseFolder() {
        try {
            EntityProvider entityProvider = new TestCaseFolderEntityProvider(folderIds);
            TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(this.getShell(), new EntityLabelProvider(),
                    entityProvider, new EntityViewerFilter(entityProvider));
            dialog.setAllowMultiple(false);
            dialog.setTitle(StringConstants.DIA_TITLE_TEST_CASE_FOLDER_BROWSER);
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            FolderEntity rootFolder = FolderController.getInstance().getTestCaseRoot(currentProject);
            FolderTreeEntity rootFolderTreeEntity = new FolderTreeEntity(rootFolder, null);

            FolderEntity selectedFolderEntity = FolderController.getInstance().getFolderByDisplayId(currentProject,
                    folderId);
            if (selectedFolderEntity != null) {
                dialog.setInitialSelection(new FolderTreeEntity(selectedFolderEntity, TreeEntityUtil
                        .createSelectedTreeEntityHierachy(selectedFolderEntity.getParentFolder(), rootFolder)));
            }
            dialog.setInput(Arrays.asList(rootFolderTreeEntity));
            if (dialog.open() == Dialog.OK) {
                Object[] results = dialog.getResult();
                if (results == null || results.length != 1) {
                    return;
                }
                FolderTreeEntity folderTreeEntity = (FolderTreeEntity) results[0];
                folderId = ((FolderEntity) folderTreeEntity.getObject()).getIdForDisplay();
                txtKatalonFolder.setText(folderId);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.DIA_MSG_UNABLE_TO_FIND_TEST_CASE_FOLDER,
                    e.getClass().getSimpleName());
        }
    }

    public TestCaseRepo getTestCaseRepo() {
        TestCaseRepo repo = new TestCaseRepo();
        repo.setQTestModule(qTestModule);
        repo.setQTestProject(qTestProject);
        repo.setFolderId(folderId);
        return repo;
    }

    private void validate() {
        if (qTestProject == null || qTestModule == null || folderId == null) {
            getButton(OK).setEnabled(false);
        } else {
            getButton(OK).setEnabled(true);
        }
    }

    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(newShellStyle | SWT.RESIZE);
    }
}
