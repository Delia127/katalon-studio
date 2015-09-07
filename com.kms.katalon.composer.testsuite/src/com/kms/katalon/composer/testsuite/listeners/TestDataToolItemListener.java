package com.kms.katalon.composer.testsuite.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.constants.TestDataToolItemConstants;
import com.kms.katalon.composer.testsuite.parts.TestSuitePart;
import com.kms.katalon.composer.testsuite.providers.TestDataTreeContentProvider;
import com.kms.katalon.composer.testsuite.tree.TestDataLinkTreeNode;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestDataToolItemListener extends SelectionAdapter {

	private TreeViewer testDataLinkTreeViewer;
	private TestSuitePart mpart;

	public TestDataToolItemListener(TreeViewer treeViewer, TestSuitePart mpart) {
		super();
		this.testDataLinkTreeViewer = treeViewer;
		this.mpart = mpart;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (mpart.getSelectedTestCaseLink() == null) {
			MessageDialog.openInformation(null, "Information", "Please select a test case.");
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

		if (toolItem.getText() == null) return;

		switch (toolItem.getToolTipText()) {
			case TestDataToolItemConstants.ADD:
				if (e.detail == SWT.ARROW) {
					createDropdownMenuAddItem(toolItem);
				} else {
					performAddTestDataLink(TestDataToolItemConstants.ADD_AFTER);
				}
				return;
			case TestDataToolItemConstants.REMOVE:
				removeTestDataLink();
				return;
			case TestDataToolItemConstants.UP:
				upTestDataLink();
				return;
			case TestDataToolItemConstants.DOWN:
				downTestDataLink();
				return;
			case TestDataToolItemConstants.MAP:
				mapTestDataLink();
				return;
			case TestDataToolItemConstants.MAPALL:
				mapAllTestDataLink();
				return;
			default:
				return;
		}
	}

	private void menuItemSelected(SelectionEvent e) {
		MenuItem menuItem = (MenuItem) e.getSource();
		if (menuItem.getText() == null) return;
		switch (menuItem.getText()) {
			case TestDataToolItemConstants.ADD_AFTER:
				performAddTestDataLink(TestDataToolItemConstants.ADD_AFTER);
				return;
			case TestDataToolItemConstants.ADD_BEFORE:
				performAddTestDataLink(TestDataToolItemConstants.ADD_BEFORE);
				return;
			case TestDataToolItemConstants.ADD_CHILDREN:
				performAddTestDataLink(TestDataToolItemConstants.ADD_CHILDREN);
				return;
			default:
				return;
		}
	}

	private void createDropdownMenuAddItem(ToolItem toolItemAdd) {
		Rectangle rect = toolItemAdd.getBounds();
		Point pt = toolItemAdd.getParent().toDisplay(new Point(rect.x, rect.y));

		Menu menu = new Menu(toolItemAdd.getParent().getShell());

		MenuItem mnAddBefore = new MenuItem(menu, SWT.NONE);
		mnAddBefore.setText(TestDataToolItemConstants.ADD_BEFORE);
		mnAddBefore.addSelectionListener(this);

		MenuItem mnAddAfter = new MenuItem(menu, SWT.NONE);
		mnAddAfter.setText(TestDataToolItemConstants.ADD_AFTER);
		mnAddAfter.addSelectionListener(this);

		MenuItem mnAddChildren = new MenuItem(menu, SWT.NONE);
		mnAddChildren.setText(TestDataToolItemConstants.ADD_CHILDREN);
		mnAddChildren.addSelectionListener(this);

		menu.setLocation(pt.x, pt.y + rect.height);
		menu.setVisible(true);
	}

	private void performAddTestDataLink(String offset) {
		try {
			ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
			if (currentProject == null) return;

			EntityProvider entityProvider = new EntityProvider();
			TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(testDataLinkTreeViewer.getTree()
					.getShell(), new EntityLabelProvider(), new EntityProvider(),
					new EntityViewerFilter(entityProvider));

			dialog.setAllowMultiple(true);
			dialog.setTitle(StringConstants.LIS_TITLE_TEST_DATA_BROWSER);

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

				List<TestDataLinkTreeNode> addedTestDataLinkTreeNodes = addTestDataToTreeView(dataFileEntities, offset);

				if (addedTestDataLinkTreeNodes.size() > 0) {
					selectTreeNodes(addedTestDataLinkTreeNodes);
					mpart.refreshVariableTable();
					mpart.setDirty(true);
				}
			}

		} catch (Exception e) {
			LoggerSingleton.logError(e);
			MessageDialog.openWarning(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
					StringConstants.LIS_ERROR_MSG_UNABLE_TO_ADD_TEST_DATA);
		}
	}

	private void selectTreeNodes(List<TestDataLinkTreeNode> treeNodes) {
		testDataLinkTreeViewer.getTree().deselectAll();
		testDataLinkTreeViewer.getTree().setFocus();
		if (treeNodes.get(0).getParentNode() == null) {
			TreeItem[] addedTreeItems = new TreeItem[treeNodes.size()];
			TestDataTreeContentProvider provider = (TestDataTreeContentProvider) testDataLinkTreeViewer
					.getContentProvider();
			for (int i = 0; i < treeNodes.size(); i++) {
				int index = provider.getDataLinks().indexOf(treeNodes.get(i).getTestDataLink());
				addedTreeItems[i] = testDataLinkTreeViewer.getTree().getItem(index);
			}
			testDataLinkTreeViewer.getTree().setSelection(addedTreeItems);
		} else {
			testDataLinkTreeViewer.setSelection(new StructuredSelection(treeNodes));
		}
	}

	private List<TestDataLinkTreeNode> addTestDataToTreeView(List<DataFileEntity> testDataEntities, String offset)
			throws Exception {
		List<TestDataLinkTreeNode> addedTestDataLinkTreeNodes = new ArrayList<TestDataLinkTreeNode>();
		StructuredSelection selection = (StructuredSelection) testDataLinkTreeViewer.getSelection();
		for (int i = 0; i < testDataEntities.size(); i++) {
			DataFileEntity testData = testDataEntities.get(i);

			TestCaseTestDataLink newTestDataLink = createTestDataLink(testData);
			TestDataLinkTreeNode newTestDataLinkNode = new TestDataLinkTreeNode(Integer.toString(i + 1),
					newTestDataLink);

			if (selection == null || selection.getFirstElement() == null) {
				TestDataTreeContentProvider provider = (TestDataTreeContentProvider) testDataLinkTreeViewer
						.getContentProvider();
				addNewTestDataLinkToRootNode(newTestDataLink, provider.getDataLinks().size());

			} else {
				TestDataLinkTreeNode selectedNode = (TestDataLinkTreeNode) selection.getFirstElement();
				TestDataLinkTreeNode parentSelectedNode = selectedNode.getParentNode();
				TestDataTreeContentProvider provider = (TestDataTreeContentProvider) testDataLinkTreeViewer
						.getContentProvider();

				switch (offset) {
					case TestDataToolItemConstants.ADD_AFTER:
						if (parentSelectedNode == null) {
							int selectedIndex = provider.getDataLinks().indexOf(selectedNode.getTestDataLink());
							addNewTestDataLinkToRootNode(newTestDataLink, selectedIndex + i + 1);
						} else {
							int selectedIndex = parentSelectedNode.getChildrenNode().indexOf(selectedNode);
							addChildNodeToParentNode(newTestDataLinkNode, parentSelectedNode, selectedIndex + i + 1);
						}
						break;
					case TestDataToolItemConstants.ADD_BEFORE:
						if (parentSelectedNode == null) {
							int indexBefore = provider.getDataLinks().indexOf(selectedNode.getTestDataLink());
							addNewTestDataLinkToRootNode(newTestDataLink, indexBefore);
						} else {
							int selectedIndex = parentSelectedNode.getChildrenNode().indexOf(selectedNode);
							addChildNodeToParentNode(newTestDataLinkNode, parentSelectedNode, selectedIndex);
						}
						break;
					case TestDataToolItemConstants.ADD_CHILDREN:
						addChildNodeToParentNode(newTestDataLinkNode, selectedNode, selectedNode.getChildrenNode()
								.size());
						break;
				}
			}
			addedTestDataLinkTreeNodes.add(newTestDataLinkNode);
		}
		return addedTestDataLinkTreeNodes;
	}

	private void addChildNodeToParentNode(TestDataLinkTreeNode childNode, TestDataLinkTreeNode parentNode, int index) {
		parentNode.addChildNode(childNode, index);
		testDataLinkTreeViewer.refresh(parentNode);
		if (!testDataLinkTreeViewer.getExpandedState(parentNode)) {
			testDataLinkTreeViewer.setExpandedState(parentNode, true);
		}
	}

	private void addNewTestDataLinkToRootNode(TestCaseTestDataLink newTestDataLink, int index) {
		TestDataTreeContentProvider provider = (TestDataTreeContentProvider) testDataLinkTreeViewer
				.getContentProvider();
		provider.getDataLinks().add(index, newTestDataLink);
		testDataLinkTreeViewer.refresh();
	}

	private TestCaseTestDataLink createTestDataLink(DataFileEntity testData) throws Exception {
		TestCaseTestDataLink testDataLink = new TestCaseTestDataLink();
		testDataLink.setTestDataId(TestDataController.getInstance().getIdForDisplay(testData));

		return testDataLink;
	}

	private void removeTestDataLink() {
		StructuredSelection selection = (StructuredSelection) testDataLinkTreeViewer.getSelection();
		if (selection == null || selection.size() == 0) return;
		@SuppressWarnings("unchecked")
		Iterator<TestDataLinkTreeNode> iterator = selection.toList().iterator();

		while (iterator.hasNext()) {
			TestDataLinkTreeNode linkNode = iterator.next();
			if (linkNode.getParentNode() == null) {
				TestDataTreeContentProvider provider = (TestDataTreeContentProvider) testDataLinkTreeViewer
						.getContentProvider();
				provider.getDataLinks().remove(linkNode.getTestDataLink());
				testDataLinkTreeViewer.refresh();

			} else {
				TestDataLinkTreeNode parentNode = linkNode.getParentNode();
				parentNode.removeChildNode(linkNode);
				testDataLinkTreeViewer.refresh(parentNode);
			}

			for (VariableLink variableLink : mpart.getSelectedTestCaseLink().getVariableLinks()) {

				if (variableLink.getType() == VariableType.DATA_COLUMN
						&& variableLink.getTestDataLinkId().equals(linkNode.getTestDataLink().getId())) {
					variableLink.setTestDataLinkId("");
					variableLink.setValue("");
				}
			}
		}
		mpart.refreshVariableTable();
		mpart.setDirty(true);
	}

	@SuppressWarnings("unchecked")
	private void upTestDataLink() {
		StructuredSelection selection = (StructuredSelection) testDataLinkTreeViewer.getSelection();
		if (selection == null) return;

		TestDataLinkTreeNode selectedNode = (TestDataLinkTreeNode) selection.getFirstElement();

		if (selectedNode == null) return;

		TestDataTreeContentProvider contentProvider = (TestDataTreeContentProvider) testDataLinkTreeViewer
				.getContentProvider();

		TestDataLinkTreeNode parentNode = selectedNode.getParentNode();

		if (parentNode == null) {
			int index = contentProvider.getDataLinks().indexOf(selectedNode.getTestDataLink());

			if (index <= 0) return;

			Collections.swap(contentProvider.getDataLinks(), index, index - 1);
			testDataLinkTreeViewer.refresh();
		} else {
			int index = parentNode.getChildrenNode().indexOf(selectedNode);
			if (index <= 0) return;

			Collections.swap(parentNode.getChildrenNode(), index, index - 1);
			Collections.swap(parentNode.getTestDataLink().getChildrenLink(), index, index - 1);

			parentNode.resetChildrenId();
			testDataLinkTreeViewer.refresh(parentNode, true);
		}

		selectTreeNodes(selection.toList().subList(0, 1));
		mpart.refreshVariableTable();
		mpart.setDirty(true);
	}

	@SuppressWarnings("unchecked")
	private void downTestDataLink() {
		StructuredSelection selection = (StructuredSelection) testDataLinkTreeViewer.getSelection();
		if (selection == null) return;

		TestDataLinkTreeNode selectedNode = (TestDataLinkTreeNode) selection.getFirstElement();

		if (selectedNode == null) return;

		TestDataTreeContentProvider contentProvider = (TestDataTreeContentProvider) testDataLinkTreeViewer
				.getContentProvider();

		TestDataLinkTreeNode parentNode = selectedNode.getParentNode();

		if (parentNode == null) {
			int index = contentProvider.getDataLinks().indexOf(selectedNode.getTestDataLink());

			if (index >= contentProvider.getDataLinks().size() - 1) return;

			Collections.swap(contentProvider.getDataLinks(), index, index + 1);
			testDataLinkTreeViewer.refresh();
		} else {

			int index = parentNode.getChildrenNode().indexOf(selectedNode);

			if (index >= parentNode.getChildrenNode().size() - 1) return;

			Collections.swap(parentNode.getChildrenNode(), index, index + 1);
			Collections.swap(parentNode.getTestDataLink().getChildrenLink(), index, index + 1);

			parentNode.resetChildrenId();

			testDataLinkTreeViewer.refresh(parentNode, true);
		}

		selectTreeNodes(selection.toList().subList(0, 1));
		mpart.refreshVariableTable();
		mpart.setDirty(true);
	}

	private void mapTestDataLink() {

	}

	private void mapAllTestDataLink() {
		Map<String, String[]> columnNameHashmap = new LinkedHashMap<String, String[]>();
		Map<String, TestCaseTestDataLink> dataLinkHashMap = new LinkedHashMap<String, TestCaseTestDataLink>();

		ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();

		for (TestCaseTestDataLink dataLink : TestSuiteController.getInstance().getAllTestCaseTestDataLinks(
				mpart.getSelectedTestCaseLink())) {
			try {
				TestData testData = TestDataFactory.findTestDataForExternalBundleCaller(dataLink.getTestDataId(),
						projectEntity.getFolderLocation());
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
			TestSuiteTestCaseLink testCaseLink = mpart.getSelectedTestCaseLink();
			TestCaseEntity testCaseEntity = TestCaseController.getInstance().getTestCaseByDisplayId(
					testCaseLink.getTestCaseId());

			for (VariableLink variableLink : mpart.getSelectedTestCaseLink().getVariableLinks()) {

				VariableEntity variable = TestCaseController.getInstance().getVariable(testCaseEntity,
						variableLink.getVariableId());
				if (variable != null) {
					for (Entry<String, String[]> entry : columnNameHashmap.entrySet()) {
						boolean isFound = false;

						for (String columnName : entry.getValue()) {
							if (variable.getName().equals(columnName)) {
								TestCaseTestDataLink dataLink = dataLinkHashMap.get(entry.getKey());

								variableLink.setType(VariableType.DATA_COLUMN);
								variableLink.setTestDataLinkId(dataLink.getId());
								variableLink.setValue(variable.getName());

								isFound = true;
							}
						}

						if (isFound) {
							break;
						}
					}
				}
			}
			mpart.refreshVariableTable();
			mpart.setDirty(true);

			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "", StringConstants.LIS_INFO_MSG_DONE);
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
