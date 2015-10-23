package com.kms.katalon.composer.integration.qtest.wizard.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.dialog.provider.TestCaseFolderEntityProvider;
import com.kms.katalon.composer.integration.qtest.wizard.AbstractWizardPage;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public class TestSuiteFolderSelectionWizardPage extends AbstractWizardPage {

    private TreeViewer treeViewer;
    private Composite folderBrowserComposite;

    public TestSuiteFolderSelectionWizardPage() {
    }

    @Override
    public boolean canFlipToNextPage() {
        return (treeViewer != null && treeViewer.getSelection() != null && !treeViewer.getSelection().isEmpty());
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void createStepArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));

        Composite headerComposite = new Composite(composite, SWT.NONE);
        headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        headerComposite.setLayout(new GridLayout(1, false));

        Label lblHeader = new Label(headerComposite, SWT.WRAP);
        lblHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));
        lblHeader.setText(StringConstants.WZ_P_TEST_SUITE_INFO);

        folderBrowserComposite = new Composite(composite, SWT.NONE);
        folderBrowserComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    }

    @Override
    public void setInput(Map<String, Object> sharedData) {
        EntityProvider entityProvider = new TestCaseFolderEntityProvider(new ArrayList<String>());
        TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(null, new EntityLabelProvider(),
                entityProvider, new EntityViewerFilter(entityProvider));
        dialog.setAllowMultiple(false);
        dialog.setTitle(StringConstants.DIA_TITLE_TEST_CASE_FOLDER_BROWSER);
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        FolderEntity rootFolder;

        try {
            rootFolder = FolderController.getInstance().getTestSuiteRoot(currentProject);
            FolderTreeEntity rootFolderTreeEntity = new FolderTreeEntity(rootFolder, null);

            dialog.setInput(Arrays.asList(rootFolderTreeEntity));

            treeViewer = dialog.createTreeViewer(folderBrowserComposite);

            Object selection = sharedData.get("testSuiteFolder");
            if (selection != null) {
                treeViewer.setSelection(new StructuredSelection(selection));
            } else {
                treeViewer.setSelection(StructuredSelection.EMPTY);
            }
            folderBrowserComposite.layout(true, true);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public void registerControlModifyListeners() {
        treeViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                firePageChanged();
            }
        });
    }

    @Override
    public Map<String, Object> storeControlStates() {
        Map<String, Object> sharedData = new HashMap<String, Object>();
        sharedData.put("testSuiteFolder", ((IStructuredSelection) treeViewer.getSelection()).getFirstElement());
        return sharedData;
    }

    @Override
    public String getTitle() {
        return StringConstants.WZ_P_TEST_SUITE_TITLE;
    }
}
