package com.kms.katalon.composer.testsuite.listeners;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.LicenseUtil;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testsuite.constants.ComposerTestsuiteMessageConstants;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.constants.ToolItemConstants;
import com.kms.katalon.composer.testsuite.dialogs.TestDataSelectionDialog;
import com.kms.katalon.composer.testsuite.parts.TestSuitePartDataBindingView;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.license.models.LicenseType;

public class TestDataToolItemListener extends SelectionAdapter {

    private TableViewer tableViewer;

    private TestSuitePartDataBindingView view;

    public TestDataToolItemListener(TableViewer treeViewer, TestSuitePartDataBindingView view) {
        super();
        this.tableViewer = treeViewer;
        this.view = view;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (view.getSelectedTestCaseLink() == null) {
            MessageDialog.openInformation(null, StringConstants.INFORMATION,
                    StringConstants.LIS_INFO_SELECT_A_TEST_CASE);
            return;
        }

        if (e.getSource() == null) return;

        if (e.getSource() instanceof ToolItem) {
            toolItemSelected(e);
        } else if (e.getSource() instanceof MenuItem) {
            menuItemSelected(e);
        }
    }

    private void toolItemSelected(SelectionEvent e) {
        ToolItem toolItem = (ToolItem) e.getSource();

        final String text = toolItem.getText();
        if (text == null) {
            return;
        }
        if (ToolItemConstants.ADD.equals(text)) {
            if (e.detail == SWT.ARROW) {
                createDropdownMenuAddItem(toolItem);
            } else {
                performAddTestDataLink(ToolItemConstants.ADD_AFTER);
            }
            return;
        }
        if (ToolItemConstants.REMOVE.equals(text)) {
            removeTestDataLink();
            return;
        }
        if (ToolItemConstants.UP.equals(text)) {
            upTestDataLink();
            return;
        }
        if (ToolItemConstants.DOWN.equals(text)) {
            downTestDataLink();
            return;
        }
        if (ToolItemConstants.MAP.equals(text)) {
            mapTestDataLink();
            return;
        }
        if (ToolItemConstants.MAPALL.equals(text)) {
            mapAllTestDataLink();
            return;
        }
    }

    private void menuItemSelected(SelectionEvent e) {
        MenuItem menuItem = (MenuItem) e.getSource();
        final String text = menuItem.getText();
        if (text == null) {
            return;
        }
        if (ToolItemConstants.ADD_AFTER.equals(text)) {
            performAddTestDataLink(ToolItemConstants.ADD_AFTER);
            return;
        }
        if (ToolItemConstants.ADD_BEFORE.equals(text)) {
            performAddTestDataLink(ToolItemConstants.ADD_BEFORE);
            return;
        }
    }

    private void createDropdownMenuAddItem(ToolItem toolItemAdd) {
        Rectangle rect = toolItemAdd.getBounds();
        Point pt = toolItemAdd.getParent().toDisplay(new Point(rect.x, rect.y));

        Menu menu = new Menu(toolItemAdd.getParent().getShell());

        MenuItem mnAddBefore = new MenuItem(menu, SWT.NONE);
        mnAddBefore.setText(ToolItemConstants.ADD_BEFORE);
        mnAddBefore.addSelectionListener(this);

        MenuItem mnAddAfter = new MenuItem(menu, SWT.NONE);
        mnAddAfter.setText(ToolItemConstants.ADD_AFTER);
        mnAddAfter.addSelectionListener(this);

        menu.setLocation(pt.x, pt.y + rect.height);
        menu.setVisible(true);
    }

    private void performAddTestDataLink(String offset) {
        try {
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            if (currentProject == null) {
                return;
            }
            
            boolean isEnterpriseAccount = LicenseUtil.isNotFreeLicense();
            int items = getTableItems().size();
            if (!isEnterpriseAccount && items >= 1) {
                MessageDialog.openWarning(tableViewer.getTable().getShell(), GlobalStringConstants.INFO,
                        ComposerTestsuiteMessageConstants.DIA_INFO_KSE_COMBINE_MULTI_DATASOURCE);
                return;
            }

            EntityProvider entityProvider = new EntityProvider();
            TestDataSelectionDialog dialog = new TestDataSelectionDialog(tableViewer.getTable().getShell(),
                    new EntityLabelProvider(), new EntityProvider(), new EntityViewerFilter(entityProvider));

            FolderEntity rootFolder = FolderController.getInstance().getTestDataRoot(currentProject);
            dialog.setInput(TreeEntityUtil.getChildren(null, rootFolder));

            if (dialog.open() == Dialog.OK && (dialog.getResult() != null)) {
                List<DataFileEntity> dataFileEntities = new ArrayList<DataFileEntity>();
                for (Object childResult : dialog.getResult()) {
                    if (childResult instanceof TestDataTreeEntity) {
                        DataFileEntity testData = (DataFileEntity) ((TestDataTreeEntity) childResult).getObject();
                        if (testData == null) continue;
                        dataFileEntities.add(testData);
                    } else if (childResult instanceof FolderTreeEntity) {
                        dataFileEntities.addAll(getTestDatasFromFolderTree((FolderTreeEntity) childResult));
                    }
                }
                
                if (!isEnterpriseAccount && (items + dataFileEntities.size()) > 1) {
                    MessageDialog.openWarning(tableViewer.getTable().getShell(), GlobalStringConstants.INFO,
                            ComposerTestsuiteMessageConstants.DIA_INFO_KSE_COMBINE_MULTI_DATASOURCE);
                    return;
                }

                List<TestCaseTestDataLink> addedTestDataLinkTreeNodes = addTestDataToTreeView(dataFileEntities, offset);

                if (addedTestDataLinkTreeNodes.size() > 0) {
                    tableViewer.refresh();
                    tableViewer.setSelection(new StructuredSelection(addedTestDataLinkTreeNodes));
                    view.refreshVariableTable();
                    view.setDirty(true);
                }
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openWarning(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.LIS_ERROR_MSG_UNABLE_TO_ADD_TEST_DATA);
        }
    }

    private List<TestCaseTestDataLink> getTableItems() {
        return view.getSelectedTestCaseLink().getTestDataLinks();
    }

    private List<TestCaseTestDataLink> addTestDataToTreeView(List<DataFileEntity> testDataEntities, String offset)
            throws Exception {
        List<TestCaseTestDataLink> addedTestDataLinkTreeNodes = new ArrayList<TestCaseTestDataLink>();
        int selectedIndex = tableViewer.getTable().getSelectionIndex();

        for (int i = 0; i < testDataEntities.size(); i++) {
            DataFileEntity testData = testDataEntities.get(i);

            TestCaseTestDataLink newTestDataLink = createTestDataLink(testData);
            if (ToolItemConstants.ADD_AFTER.equals(offset)) {
                if (selectedIndex < 0) {
                    int itemCount = tableViewer.getTable().getItemCount();
                    getTableItems().add(itemCount, newTestDataLink);
                    selectedIndex = itemCount;
                } else {
                    getTableItems().add(selectedIndex + 1, newTestDataLink);
                    selectedIndex++;
                }
            } else if (ToolItemConstants.ADD_BEFORE.equals(offset)) {
                if (selectedIndex <= 0) {
                    getTableItems().add(0, newTestDataLink);
                    selectedIndex = 1;
                } else {
                    getTableItems().add(selectedIndex, newTestDataLink);
                    selectedIndex++;
                }
            }
            addedTestDataLinkTreeNodes.add(newTestDataLink);
        }
        return addedTestDataLinkTreeNodes;
    }

    private TestCaseTestDataLink createTestDataLink(DataFileEntity testData) throws Exception {
        TestCaseTestDataLink testDataLink = new TestCaseTestDataLink();
        testDataLink.setTestDataId(testData.getIdForDisplay());

        return testDataLink;
    }

    private void removeTestDataLink() {
        StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
        if (selection == null || selection.size() == 0) return;
        @SuppressWarnings("unchecked")
        Iterator<TestCaseTestDataLink> iterator = selection.toList().iterator();

        while (iterator.hasNext()) {
            TestCaseTestDataLink linkNode = iterator.next();

            for (VariableLink variableLink : view.getVariableLinks()) {

                if ((variableLink.getType() == VariableType.DATA_COLUMN || variableLink.getType() == VariableType.DATA_COLUMN_INDEX) 
                        && variableLink.getTestDataLinkId().equals(linkNode.getId())) {
                    variableLink.setTestDataLinkId("");
                    variableLink.setValue("");
                }
            }
        }

        getTableItems().removeAll(selection.toList());
        tableViewer.refresh();
        view.refreshVariableTable();
        view.setDirty(true);
    }

    @SuppressWarnings("unchecked")
    private void upTestDataLink() {
        IStructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
        if (selection == null || selection.size() == 0) {
            return;
        }

        List<TestCaseTestDataLink> selectedLinks = selection.toList();

        sortListByAscending(getTableItems(), selectedLinks);

        boolean needToRefresh = false;
        for (TestCaseTestDataLink selectedLink : selectedLinks) {

            int selectedIndex = getTableItems().indexOf(selectedLink);
            if (selectedIndex > 0) {
                TestCaseTestDataLink linkBefore = (TestCaseTestDataLink) getTableItems().get(selectedIndex - 1);

                // Avoid swap 2 objects that are both selected
                if (selectedLinks.contains(linkBefore)) {
                    continue;
                }

                Collections.swap(getTableItems(), selectedIndex - 1, selectedIndex);
                needToRefresh = true;
            }
        }

        if (needToRefresh) {
            tableViewer.refresh();
            view.refreshVariableTable();
            view.setDirty(true);
        }
    }

    @SuppressWarnings("unchecked")
    private void downTestDataLink() {
        IStructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
        if (selection == null || selection.size() == 0) {
            return;
        }

        List<TestCaseTestDataLink> selectedLinks = selection.toList();

        sortListByDescending(getTableItems(), selectedLinks);

        boolean needToRefresh = false;
        for (TestCaseTestDataLink selectedLink : selectedLinks) {

            int selectedIndex = getTableItems().indexOf(selectedLink);
            if (selectedIndex < getTableItems().size() - 1) {
                TestCaseTestDataLink linkAfter = (TestCaseTestDataLink) getTableItems().get(selectedIndex + 1);

                // Avoid swap 2 objects that are both selected
                if (selectedLinks.contains(linkAfter)) {
                    continue;
                }

                Collections.swap(getTableItems(), selectedIndex, selectedIndex + 1);
                needToRefresh = true;
            }
        }

        if (needToRefresh) {
            tableViewer.refresh();
            view.refreshVariableTable();
            view.setDirty(true);
        }
    }

    private void sortListByAscending(final List<TestCaseTestDataLink> data,
            List<TestCaseTestDataLink> testDataLinks) {
        Collections.sort(testDataLinks, new Comparator<TestCaseTestDataLink>() {

            @Override
            public int compare(TestCaseTestDataLink arg0, TestCaseTestDataLink arg1) {
                return data.indexOf(arg0) - data.indexOf(arg1);
            }
        });
    }
    
    private void sortListByDescending(final List<TestCaseTestDataLink> data,
            List<TestCaseTestDataLink> testDataLinks) {
        Collections.sort(testDataLinks, new Comparator<TestCaseTestDataLink>() {

            @Override
            public int compare(TestCaseTestDataLink arg0, TestCaseTestDataLink arg1) {
                return data.indexOf(arg1) - data.indexOf(arg0);
            }
        });

    }

    private void mapTestDataLink() {

    }

    private void mapAllTestDataLink() {
        Map<String, String[]> columnNameHashmap = new LinkedHashMap<String, String[]>();
        Map<String, TestCaseTestDataLink> dataLinkHashMap = new LinkedHashMap<String, TestCaseTestDataLink>();

        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();

        for (TestCaseTestDataLink dataLink : view.getSelectedTestCaseLink().getTestDataLinks()) {
            try {
//                TestData testData = TestDataFactory.findTestDataForExternalBundleCaller(dataLink.getTestDataId(),
//                        projectEntity.getFolderLocation());
            	TestData testData = TestDataController.getInstance().getTestDataInstance(dataLink.getTestDataId(), projectEntity.getFolderLocation());
                if (testData == null) {
                    continue;
                }

                String[] columnNames = testData.getColumnNames();
                if (columnNames != null) {
                    columnNameHashmap.put(dataLink.getId(), columnNames);
                    dataLinkHashMap.put(dataLink.getId(), dataLink);
                }
            } catch (Exception e) {
                // Ignore it because user might not set data source for test
                // data.
            }
        }

        try {
            TestSuiteTestCaseLink testCaseLink = view.getSelectedTestCaseLink();
            TestCaseEntity testCaseEntity = TestCaseController.getInstance().getTestCaseByDisplayId(
                    testCaseLink.getTestCaseId());

            int matches = 0;
            for (VariableLink variableLink : view.getSelectedTestCaseLink().getVariableLinks()) {

                VariableEntity variable = TestCaseController.getInstance().getVariable(testCaseEntity,
                        variableLink.getVariableId());
                if (variable == null) {
                    continue;
                }

                for (Entry<String, String[]> entry : columnNameHashmap.entrySet()) {
                    boolean isFound = false;

                    for (String columnName : entry.getValue()) {
                        if (variable.getName().equalsIgnoreCase(columnName)) {
                            TestCaseTestDataLink dataLink = dataLinkHashMap.get(entry.getKey());

                            variableLink.setType(VariableType.DATA_COLUMN);
                            variableLink.setTestDataLinkId(dataLink.getId());
                            variableLink.setValue(columnName);
                            matches++;
                            isFound = true;
                        }
                    }

                    if (isFound) {
                        break;
                    }
                }
            }

            MessageDialog.openInformation(null, StringConstants.INFO,
                    MessageFormat.format(StringConstants.LIS_INFO_MSG_MAP_DONE, Integer.toString(matches)));
            if (matches > 0) {
                view.refreshVariableTable();
                view.setDirty(true);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private List<DataFileEntity> getTestDatasFromFolderTree(FolderTreeEntity folderTree) {
        List<DataFileEntity> lstTestData = new ArrayList<DataFileEntity>();
        try {
            for (Object child : folderTree.getChildren()) {
                if (child instanceof TestDataTreeEntity) {
                    DataFileEntity dataFile = (DataFileEntity) ((TestDataTreeEntity) child).getObject();
                    if (dataFile != null) {
                        lstTestData.add(dataFile);
                    }
                } else if (child instanceof FolderTreeEntity) {
                    lstTestData.addAll(getTestDatasFromFolderTree((FolderTreeEntity) child));
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return lstTestData;
    }

}
