package com.katalon.plugin.smart_xpath.settings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.openqa.selenium.Keys;

import com.katalon.plugin.smart_xpath.constant.SmartXPathMessageConstants;
import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.components.impl.handler.KSEFeatureAccessHandler;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.viewer.CustomEditorActivationStrategy;
import com.kms.katalon.composer.components.viewer.CustomTreeViewerFocusCellManager;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.components.KeywordTreeViewerToolTipSupport;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants.AddAction;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.parts.ITestCasePart;
import com.kms.katalon.composer.testcase.providers.AstTreeItemLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstTreeTableContentProvider;
import com.kms.katalon.composer.testcase.providers.PerformActionComposite;
import com.kms.katalon.composer.testcase.providers.TestCaseSelectionListener;
import com.kms.katalon.composer.testcase.support.ItemColumnEditingSupport;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.composer.testcase.util.TestCaseMenuUtil;
import com.kms.katalon.composer.testcase.views.FocusCellOwnerDrawForManualTestcase;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.execution.session.ExecutionSession;
import com.kms.katalon.feature.KSEFeature;
import com.kms.katalon.util.collections.Pair;

public class ExecutionExcludeWithKeywordsPart implements ITestCasePart, PerformActionComposite {
	
	private Composite tableExcludeObjectsWithKeywordsComposite;
	
	private TestCaseTreeTableInput treeTableInput;
	
	private ToolItem tltmAddVariable;

    private TestCaseSelectionListener selectionListener = new TestCaseSelectionListener(this);
	
	private TreeViewer treeTable;
	
    private CustomTreeViewerFocusCellManager focusCellManager;
	
	private List<Pair<String, Boolean>> excludeKeywords = new ArrayList<Pair<String,Boolean>>();
	
	public TreeViewer getTreeTable() {
		return treeTable;
	}
	
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
		tltmAddVariable = new ToolItem(toolBar, SWT.DROP_DOWN);

		tltmAddVariable.setText("Add");
		tltmAddVariable.setImage(ImageManager.getImage(IImageKeys.ADD_16));
        Menu addMenu = new Menu(tltmAddVariable.getParent().getShell());
		tltmAddVariable.setData(addMenu);
        TestCaseMenuUtil.fillActionMenu(TreeTableMenuItemConstants.AddAction.Add, selectionListener, addMenu);
        tltmAddVariable.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
		        if (event.detail == SWT.ARROW) {
		        } else {
		        	List<KeywordClass> builtinKeyword = KeywordController.getInstance().getBuiltInKeywordClasses();
		        	System.out.println(builtinKeyword);
		            treeTableInput.addNewDefaultBuiltInKeyword(NodeAddType.Add);
		        }
			}
        });

		ToolItem tltmRemoveVariable = new ToolItem(toolBar, SWT.NONE);
		tltmRemoveVariable.setText("Remove");
		tltmRemoveVariable.setImage(ImageManager.getImage(IImageKeys.DELETE_16));

		tableExcludeObjectsWithKeywordsComposite = new Composite(excludeKeywordsGroup, SWT.NONE);

		tableExcludeObjectsWithKeywordsComposite.setLayoutData(new GridData(SWT.FILL, SWT.WRAP, true, true, 1, 1));

		treeTable = new CTreeViewer(tableExcludeObjectsWithKeywordsComposite,  SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		Tree childTableTree = treeTable.getTree();
	    childTableTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	    childTableTree.setHeaderVisible(true);
	    childTableTree.setLinesVisible(ControlUtils.shouldLineVisble(childTableTree.getDisplay()));

        TreeColumnLayout treeColumnLayout = new TreeColumnLayout();
	    tableExcludeObjectsWithKeywordsComposite.setLayout(treeColumnLayout);

        addTreeTableColumn(treeTable, treeColumnLayout, "Keyword", 200, 100,
                new AstTreeItemLabelProvider(), new ItemColumnEditingSupport(treeTable, this));

        treeTable.setContentProvider(new AstTreeTableContentProvider());

        setTreeTableActivation();

        KeywordTreeViewerToolTipSupport.enableFor(treeTable);

        ScriptNodeWrapper wrapper;
        wrapper = new ScriptNodeWrapper();
        wrapper.addDefaultImports();
        wrapper.addImport(Keys.class);

        treeTableInput = new TestCaseTreeTableInput(wrapper, treeTable, this);
        treeTable.setInput(treeTableInput.getMainClassNode().getAstChildren());
//		List<AstTreeTableNode> astTreeTableNodes = new ArrayList<AstTreeTableNode>();
		//        treeTable.setInput(excludeKeywords);
	    return excludeKeywordsComposite;
	}

    private void setTreeTableActivation() {
        int activationBitMask = ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                | ColumnViewerEditor.KEYBOARD_ACTIVATION;
        FocusCellOwnerDrawForManualTestcase focusCellHighlighter = new FocusCellOwnerDrawForManualTestcase(treeTable);
        focusCellManager = new CustomTreeViewerFocusCellManager(treeTable, focusCellHighlighter);
        CustomEditorActivationStrategy editorActivationStrategy = new CustomEditorActivationStrategy(treeTable,
                focusCellHighlighter);
        TreeViewerEditor.create(treeTable, focusCellManager, editorActivationStrategy, activationBitMask);
    }

	
    private void addDefaultWebServiceKeyword() {
        MethodCallExpressionWrapper sendRequestMethodCall = AstKeywordsInputUtil.generateBuiltInKeywordExpression("WS",
                "sendRequest", treeTableInput.getMainClassNode());
        treeTableInput.addNewAstObject(new ExpressionStatementWrapper(sendRequestMethodCall),
                treeTableInput.getSelectedNode(), NodeAddType.Add);
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

	@Override
	public void performToolItemSelected(ToolItem toolItem, SelectionEvent selectionEvent) {
		getTreeTable().applyEditorValue();
        if (toolItem.equals(tltmAddVariable)) {
            if (selectionEvent.detail == SWT.ARROW && toolItem.getData() instanceof Menu) {
                Rectangle rect = toolItem.getBounds();
                Point pt = toolItem.getParent().toDisplay(new Point(rect.x, rect.y));
                Menu menu = (Menu) toolItem.getData();
                menu.setLocation(pt.x, pt.y + rect.height);
                menu.setVisible(true);
            } else {
                treeTableInput.addNewDefaultBuiltInKeyword(NodeAddType.Add);
            }
            return;
        }
	}

	@Override
	public void performMenuItemSelected(MenuItem menuItem) {
	}

}
