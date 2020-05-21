package com.katalon.plugin.smart_xpath.settings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import com.katalon.plugin.smart_xpath.constant.SmartXPathMessageConstants;
import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.components.KeywordTreeViewerToolTipSupport;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.parts.ITestCasePart;
import com.kms.katalon.composer.testcase.providers.AstTreeItemLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstTreeTableContentProvider;
import com.kms.katalon.composer.testcase.support.ItemColumnEditingSupport;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.util.collections.Pair;

public class ExecutionExcludeWithKeywordsPart implements ITestCasePart {
	
	private Composite tableExcludeObjectsWithKeywordsComposite;
	
	private List<Pair<String, Boolean>> excludeKeywords = new ArrayList<Pair<String,Boolean>>();

	public Composite createContent(Composite parent) {
		Composite excludeKeywordsComposite = new Composite(parent, SWT.NONE);
		excludeKeywordsComposite.setLayout(new GridLayout(1, false));
		excludeKeywordsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Group excludeKeywordsGroup = new Group(excludeKeywordsComposite, SWT.NONE);
		excludeKeywordsGroup.setLayout(new GridLayout());
		excludeKeywordsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		excludeKeywordsGroup.setText(SmartXPathMessageConstants.LABEL_EXCLUDE_OBJECTS_USED_WITH_KEYWORDS);

		Composite compositeToolbar = new Composite(excludeKeywordsGroup, SWT.NONE);
		compositeToolbar.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeToolbar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		ToolBar toolBar = new ToolBar(compositeToolbar, SWT.FLAT | SWT.RIGHT);
		toolBar.setForeground(ColorUtil.getToolBarForegroundColor());
		ToolItem tltmAddVariable = new ToolItem(toolBar, SWT.DROP_DOWN);

		tltmAddVariable.setText("Add");
		tltmAddVariable.setImage(ImageManager.getImage(IImageKeys.ADD_16));
        Menu addMenu = new Menu(tltmAddVariable.getParent().getShell());
        tltmAddVariable.setData(addMenu);

		ToolItem tltmRemoveVariable = new ToolItem(toolBar, SWT.NONE);
		tltmRemoveVariable.setText("Remove");
		tltmRemoveVariable.setImage(ImageManager.getImage(IImageKeys.DELETE_16));

		tableExcludeObjectsWithKeywordsComposite = new Composite(excludeKeywordsGroup, SWT.NONE);

		tableExcludeObjectsWithKeywordsComposite.setLayoutData(new GridData(SWT.FILL, SWT.WRAP, true, true, 1, 1));

		TreeViewer treeTable = new CTreeViewer(tableExcludeObjectsWithKeywordsComposite,  SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		Tree childTableTree = treeTable.getTree();
	    childTableTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	    childTableTree.setHeaderVisible(true);
	    childTableTree.setLinesVisible(ControlUtils.shouldLineVisble(childTableTree.getDisplay()));

        TreeColumnLayout treeColumnLayout = new TreeColumnLayout();
	    tableExcludeObjectsWithKeywordsComposite.setLayout(treeColumnLayout);

        addTreeTableColumn(treeTable, treeColumnLayout, "Keyword", 200, 100,
                new AstTreeItemLabelProvider(), new ItemColumnEditingSupport(treeTable, this));

        treeTable.setContentProvider(new AstTreeTableContentProvider());

        KeywordTreeViewerToolTipSupport.enableFor(treeTable);

//        ScriptNodeWrapper wrapper;
//        wrapper = new ScriptNodeWrapper();
//        wrapper.addDefaultImports();

//        TestCaseTreeTableInput treeTableInput = new TestCaseTreeTableInput(wrapper, treeTable, this);
//        treeTable.setInput(treeTableInput.getMainClassNode().getAstChildren());
//		List<AstTreeTableNode> astTreeTableNodes = new ArrayList<AstTreeTableNode>();
//		astTreeTableNodes.add(new AstTreeTableNode());
        treeTable.setInput(excludeKeywords);
	    return excludeKeywordsComposite;
	}

    private TreeViewerColumn addTreeTableColumn(TreeViewer parent, TreeColumnLayout treeColumnLayout, String headerText,
            int width, int weight, CellLabelProvider labelProvider, EditingSupport editingSupport) {
        TreeViewerColumn treeTableColumn = new TreeViewerColumn(parent, SWT.NONE);
        TreeColumn treeColumn = treeTableColumn.getColumn();
        treeColumn.setWidth(width);
        treeColumn.setMoveable(true);
        treeColumn.setText(headerText);
        treeTableColumn.setLabelProvider(labelProvider);
        treeTableColumn.setEditingSupport(editingSupport);
        treeColumnLayout.setColumnData(treeTableColumn.getColumn(),
                new ColumnWeightData(weight, treeColumn.getWidth()));

        return treeTableColumn;
    }

	@Override
	public void setDirty(boolean isDirty) {
	}

	@Override
	public void addVariables(VariableEntity[] variables) {
	}

	@Override
	public VariableEntity[] getVariables() {
		return null;
	}

	@Override
	public void deleteVariables(List<VariableEntity> variableList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TestCaseEntity getTestCase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TestCaseTreeTableInput getTreeTableInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AstTreeTableNode> getDragNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createDynamicGotoMenu(Menu menu) {
		// TODO Auto-generated method stub
		
	}

}
