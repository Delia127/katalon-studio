package com.kms.katalon.composer.integration.qtest.dialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.dialog.model.ModuleDownloadedPreviewTreeNode;
import com.kms.katalon.composer.integration.qtest.dialog.model.TestCaseDownloadedPreviewTreeNode;
import com.kms.katalon.composer.integration.qtest.dialog.provider.QTestDownloadedTreeContentProvider;
import com.kms.katalon.composer.integration.qtest.dialog.provider.QTestDownloadedTreeLabelProvider;
import com.kms.katalon.composer.integration.qtest.dialog.provider.QTestDownloadedTreeStateListener;
import com.kms.katalon.composer.integration.qtest.model.FolderModulePair;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationFolderManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestCaseManager;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;

public class TestCaseTreeDownloadedPreviewDialog extends Dialog {
    private Composite container;
    private QTestModule module;
    private FolderEntity folderEntity;
    private CheckboxTreeViewer checkboxTreeViewer;
    private Object[] selectedElement;
    private Label lblSelectedItems;
    private Label lblTotalItems;
    private int totalItemsCount;

    public TestCaseTreeDownloadedPreviewDialog(Shell parentShell, FolderEntity folder, QTestModule module) {
        super(parentShell);
        this.module = module;
        this.folderEntity = folder;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 500);
    }

    protected Control createDialogArea(Composite parent) {
        container = (Composite) super.createDialogArea(parent);

        Composite compositeHeader = new Composite(container, SWT.NONE);
        compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        compositeHeader.setLayout(new GridLayout(1, false));

        Label lblHeader = new Label(compositeHeader, SWT.NONE);
        lblHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblHeader.setText(StringConstants.DIA_INFO_TEST_CASE_DOWNLOADED_PREVIEW);

        Composite compositeSelected = new Composite(compositeHeader, SWT.NONE);
        compositeSelected.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout gl_compositeSelected = new GridLayout(2, false);
        gl_compositeSelected.marginHeight = 0;
        gl_compositeSelected.marginWidth = 0;
        compositeSelected.setLayout(gl_compositeSelected);

        lblSelectedItems = new Label(compositeSelected, SWT.NONE);

        lblTotalItems = new Label(compositeSelected, SWT.NONE);
        lblTotalItems.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Composite compositeTable = new Composite(container, SWT.NONE);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        checkboxTreeViewer = new CheckboxTreeViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION);
        Tree tree = checkboxTreeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        tree.setHeaderVisible(true);

        TreeViewerColumn treeViewerColumnName = new TreeViewerColumn(checkboxTreeViewer, SWT.NONE);
        TreeColumn trclmnName = treeViewerColumnName.getColumn();
        trclmnName.setText(StringConstants.NAME);

        TreeViewerColumn treeViewerColumnType = new TreeViewerColumn(checkboxTreeViewer, SWT.NONE);
        TreeColumn trclmnType = treeViewerColumnType.getColumn();
        trclmnType.setText(StringConstants.CM_TYPE);

        TreeViewerColumn treeViewerColumnStatus = new TreeViewerColumn(checkboxTreeViewer, SWT.NONE);
        TreeColumn trclmnStatus = treeViewerColumnStatus.getColumn();
        trclmnStatus.setText(StringConstants.STATUS);

        TreeColumnLayout tableLayout = new TreeColumnLayout();
        tableLayout.setColumnData(trclmnName, new ColumnWeightData(80, 0));
        tableLayout.setColumnData(trclmnType, new ColumnWeightData(0, 100));
        tableLayout.setColumnData(trclmnStatus, new ColumnWeightData(0, 150));
        compositeTable.setLayout(tableLayout);

        checkboxTreeViewer.setContentProvider(new QTestDownloadedTreeContentProvider());
        checkboxTreeViewer.setLabelProvider(new QTestDownloadedTreeLabelProvider());
        checkboxTreeViewer.addCheckStateListener(new QTestDownloadedTreeStateListener(this));

        return container;
    }

    @Override
    public void create() {
        super.create();
        setInput();
    }

    private void setInput() {
        try {
            ModuleDownloadedPreviewTreeNode rootTree = getModuleUpdatedPreviewTreeItem(module, folderEntity, null);
            isModuleAvailableForCreating(rootTree);
            List<Object> chilren = new ArrayList<Object>();
            chilren.addAll(rootTree.getChildModuleTrees());
            chilren.addAll(rootTree.getChildTestCaseTrees());

            checkboxTreeViewer.setInput(chilren);
            totalItemsCount = 0;
            for (Object rootElement : chilren) {
                checkboxTreeViewer.setSubtreeChecked(rootElement, true);
                totalItemsCount += getChildCount(rootElement);
            }

            checkboxTreeViewer.expandAll();
            updateStatus();

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected void setShellStyle(int arg) {
        super.setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
    }

    private ModuleDownloadedPreviewTreeNode getModuleUpdatedPreviewTreeItem(QTestModule module,
            FolderEntity folderEntity, ModuleDownloadedPreviewTreeNode parentModuleTree) throws Exception {
        ModuleDownloadedPreviewTreeNode moduleTree = new ModuleDownloadedPreviewTreeNode(module, folderEntity,
                parentModuleTree);

        if (folderEntity != null) {
            List<TestCaseEntity> childTestCaseEntities = FolderController.getInstance().getTestCaseChildren(
                    folderEntity);
            module.getChildTestCases().removeAll(getChildQTestCases(childTestCaseEntities));
        }

        for (QTestTestCase qTestCaseNew : module.getChildTestCases()) {
            TestCaseDownloadedPreviewTreeNode testCaseTree = new TestCaseDownloadedPreviewTreeNode(moduleTree,
                    qTestCaseNew);
            moduleTree.getChildTestCaseTrees().add(testCaseTree);
        }

        List<FolderEntity> childFolderEntities = new ArrayList<FolderEntity>();

        if (folderEntity != null) {
            childFolderEntities.addAll(FolderController.getInstance().getChildFolders(folderEntity));
        }

        Map<Long, FolderModulePair> qTestModuleMap = getChildQTestModules(childFolderEntities);

        for (QTestModule childModule : module.getChildModules()) {
            if (!qTestModuleMap.containsKey(childModule.getId())) {
                qTestModuleMap.put(childModule.getId(), new FolderModulePair(null, childModule));
            } else {
                FolderModulePair pair = qTestModuleMap.get(childModule.getId());
                pair.setModule(childModule);

                qTestModuleMap.put(childModule.getId(), pair);
            }
        }

        for (FolderModulePair pair : qTestModuleMap.values()) {
            ModuleDownloadedPreviewTreeNode childModuleTree = getModuleUpdatedPreviewTreeItem(pair.getModule(),
                    pair.getFolder(), moduleTree);
            moduleTree.getChildModuleTrees().add(childModuleTree);
        }

        return moduleTree;
    }

    private boolean isModuleAvailableForCreating(ModuleDownloadedPreviewTreeNode moduleTree) {
        int index = 0;
        while (index < moduleTree.getChildModuleTrees().size()) {
            ModuleDownloadedPreviewTreeNode childModuleTree = moduleTree.getChildModuleTrees().get(index);
            boolean isChildQualified = isModuleAvailableForCreating(childModuleTree);
            if (!isChildQualified) {
                moduleTree.getChildModuleTrees().remove(index);
            } else {
                index++;
            }
        }

        if (moduleTree.getChildModuleTrees().size() > 0) {
            return true;
        }

        if (moduleTree.getFolderEntity() == null) {
            return true;
        }

        if (moduleTree.getChildTestCaseTrees().size() > 0) {
            return true;
        }

        return false;
    }

    private Map<Long, FolderModulePair> getChildQTestModules(List<FolderEntity> folderEntities) {
        Map<Long, FolderModulePair> qTestModuleMap = new LinkedHashMap<Long, FolderModulePair>();

        for (FolderEntity folderEntity : folderEntities) {
            IntegratedEntity folderIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(folderEntity);
            
            if (folderIntegratedEntity == null) {
                continue;
            }
            
            QTestModule qTestModule = QTestIntegrationFolderManager
                    .getQTestModuleByIntegratedEntity(folderIntegratedEntity);

            if (qTestModule != null) {
                FolderModulePair folderModulePair = new FolderModulePair(folderEntity, qTestModule);
                qTestModuleMap.put(qTestModule.getId(), folderModulePair);
            }

        }
        return qTestModuleMap;
    }

    private List<QTestTestCase> getChildQTestCases(List<TestCaseEntity> testCaseEntities) {
        List<QTestTestCase> qTestCases = new ArrayList<QTestTestCase>();

        for (TestCaseEntity testCase : testCaseEntities) {
            IntegratedEntity testCaseIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(testCase);
            QTestTestCase qTestCase = QTestIntegrationTestCaseManager
                    .getQTestTestCaseByIntegratedEntity(testCaseIntegratedEntity);

            if (qTestCase != null) {
                qTestCases.add(qTestCase);
            }

        }
        return qTestCases;
    }

    public void updateStatus() {
        int selectedItemsCount = checkboxTreeViewer.getCheckedElements().length;
        lblSelectedItems.setText(Integer.toString(selectedItemsCount));
        lblTotalItems.setText("items selected, " + Integer.toString(totalItemsCount) + " items total.");
        lblTotalItems.getParent().layout(true);

        if (selectedItemsCount == 0) {
            getButton(OK).setEnabled(false);
        } else {
            getButton(OK).setEnabled(true);
        }
    }

    private int getChildCount(Object item) {
        QTestDownloadedTreeContentProvider contentProvider = (QTestDownloadedTreeContentProvider) checkboxTreeViewer
                .getContentProvider();
        Object[] children = contentProvider.getChildren(item);
        int childrenSize = 0;
        if (children != null) {
            for (int index = 0; index < children.length; index++) {
                childrenSize += getChildCount(children[index]);
            }
        }
        return childrenSize + 1;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.DIA_TITLE_TEST_CASE_DOWNLOADED_PREVIEW);
    }

    @Override
    protected void okPressed() {
        selectedElement = checkboxTreeViewer.getCheckedElements();

        super.okPressed();
    }

    public Object[] selectedElements() {
        return selectedElement;
    }
}
