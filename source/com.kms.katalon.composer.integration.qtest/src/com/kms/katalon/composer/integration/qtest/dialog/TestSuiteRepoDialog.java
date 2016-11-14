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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
import com.kms.katalon.composer.integration.qtest.model.TestSuiteRepo;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationProjectManager;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.setting.QTestSettingCredential;

public class TestSuiteRepoDialog extends Dialog {
    private Composite container;

    private Text txtKatalonFolder;

    private Button btnUpdateProjects;

    private Button btnBrowseKatalonFolder;

    private Combo cbProjects;

    private QTestProject selectedQTestProject;

    private String selectedFolderId;

    private List<String> folderIds;

    private Map<Long, QTestProject> qTestProjectsMap;

    public TestSuiteRepoDialog(Shell parentShell, List<QTestProject> qTestProjects, List<String> folderIds,
            TestSuiteRepo testSuiteRepo) {
        super(parentShell);
        this.folderIds = folderIds;
        qTestProjectsMap = new LinkedHashMap<Long, QTestProject>();
        updateQTestProjectsMap(qTestProjects);
        if (testSuiteRepo != null) {
            selectedQTestProject = testSuiteRepo.getQTestProject();
            selectedFolderId = testSuiteRepo.getFolderId();
        }
    }

    protected Control createDialogArea(Composite parent) {
        container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.numColumns = 3;

        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setText(StringConstants.DIA_TITLE_QTEST_PROJECT);

        cbProjects = new Combo(container, SWT.NONE);
        cbProjects.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        btnUpdateProjects = new Button(container, SWT.NONE);
        btnUpdateProjects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnUpdateProjects.setText(StringConstants.UPDATE);

        Label lblKatalonFolder = new Label(container, SWT.NONE);
        lblKatalonFolder.setText(StringConstants.DIA_TITLE_KATALON_FOLDER);

        txtKatalonFolder = new Text(container, SWT.BORDER);
        txtKatalonFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        btnBrowseKatalonFolder = new Button(container, SWT.NONE);
        btnBrowseKatalonFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnBrowseKatalonFolder.setText(StringConstants.BROWSE);

        return container;
    }

    private void updateProjectComboboxItems() {
        String selectedProjectName = cbProjects.getText();

        List<String> projectNames = new ArrayList<String>();
        for (QTestProject qTestProject : qTestProjectsMap.values()) {
            projectNames.add(qTestProject.getName());
        }

        cbProjects.setItems(projectNames.toArray(new String[projectNames.size()]));

        if (cbProjects.getItemCount() <= 0) return;

        if (selectedProjectName.isEmpty()) {
            cbProjects.select(0);
        } else {
            int index = projectNames.indexOf(selectedProjectName);
            if (index >= 0) cbProjects.select(index);
        }
    }

    @Override
    public void create() {
        super.create();

        addButtonSelectionListeners();
        initilize();
        validate();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 165);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        if (StringUtils.isBlank(selectedFolderId)) {
            newShell.setText(StringConstants.DIA_TITLE_CREATE_TEST_SUITE_REPO);
        } else {
            newShell.setText(StringConstants.DIA_TITLE_EDIT_TEST_SUITE_REPO);
        }
    }

    private void initilize() {
        if (selectedQTestProject != null) {
            cbProjects.setText(selectedQTestProject.getName());
        }
        updateProjectComboboxItems();

        if (selectedFolderId != null) {
            txtKatalonFolder.setText(selectedFolderId);
        }
    }

    private void updateProjects() {
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
        try {
            List<QTestProject> updatedProjects = QTestIntegrationProjectManager.getAllProject(QTestSettingCredential
                    .getCredential(projectDir));
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

    private void updateQTestProjectsMap(List<QTestProject> qTestProjects) {
        for (QTestProject qTestProject : qTestProjects) {
            qTestProjectsMap.put(qTestProject.getId(), qTestProject);
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

        btnBrowseKatalonFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                findTestSuiteFolder();
                validate();
            }
        });

        cbProjects.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                QTestProject[] qTestProjects = qTestProjectsMap.values().toArray(
                        new QTestProject[qTestProjectsMap.values().size()]);
                int index = cbProjects.getSelectionIndex();
                if (index >= 0) {
                    selectedQTestProject = qTestProjects[index];
                    validate();
                }
            }
        });
    }

    protected void findTestSuiteFolder() {
        try {
            EntityProvider entityProvider = new TestCaseFolderEntityProvider(folderIds);
            TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(this.getShell(),
                    new EntityLabelProvider(), entityProvider, new EntityViewerFilter(entityProvider));
            dialog.setAllowMultiple(false);
            dialog.setTitle(StringConstants.DIA_TITLE_TEST_SUITE_FOLDER_BROWSER);
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            FolderEntity testSuiteRootFolder = FolderController.getInstance().getTestSuiteRoot(currentProject);
            FolderTreeEntity testSuiteRootFolderTreeEntity = new FolderTreeEntity(testSuiteRootFolder, null);

            dialog.setInput(Arrays.asList(testSuiteRootFolderTreeEntity));

            FolderEntity selectedFolderEntity = FolderController.getInstance().getFolderByDisplayId(currentProject,
                    selectedFolderId);
            if (selectedFolderEntity != null) {
                dialog.setInitialSelection(new FolderTreeEntity(selectedFolderEntity, TreeEntityUtil
                        .createSelectedTreeEntityHierachy(selectedFolderEntity.getParentFolder(), testSuiteRootFolder)));
            }
            if (dialog.open() == Dialog.OK) {
                Object[] results = dialog.getResult();
                if (results == null || results.length != 1) return;
                FolderTreeEntity folderTreeEntity = (FolderTreeEntity) results[0];
                selectedFolderId = ((FolderEntity) folderTreeEntity.getObject()).getIdForDisplay();
                txtKatalonFolder.setText(selectedFolderId);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.DIA_MSG_UNABLE_TO_FIND_TEST_SUITE_FOLDER,
                    e.getMessage());
        }
    }

    public TestSuiteRepo getTestSuiteRepo() {
        TestSuiteRepo repo = new TestSuiteRepo();
        repo.setQTestProject(selectedQTestProject);
        repo.setFolderId(selectedFolderId);
        return repo;
    }

    private void validate() {
        if (selectedQTestProject == null || selectedFolderId == null || selectedFolderId.isEmpty()) {
            getButton(OK).setEnabled(false);
        } else {
            getButton(OK).setEnabled(true);
        }
    }

    public Map<Long, QTestProject> getQTestProjectsMap() {
        return qTestProjectsMap;
    }

}
